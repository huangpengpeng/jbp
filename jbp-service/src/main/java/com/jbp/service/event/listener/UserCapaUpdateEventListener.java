package com.jbp.service.event.listener;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.event.UserCapaUpdateEvent;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserCapaUpdateEventListener implements ApplicationListener<UserCapaUpdateEvent> {

    @Resource
    private UserInvitationJumpService invitationJumpService;
    @Autowired
    private Environment environment;
    @Resource
    private CapaService capaService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;

    @Async
    @Override
    public void onApplicationEvent(UserCapaUpdateEvent userCapaUpdateEvent) {

        UserCapaUpdateEvent.EventDto eventDto = userCapaUpdateEvent.getEventDto();
        UserCapa userCapa = eventDto.getUserCapa();
        // 合纵创享 达到最大等级切换关系
        String zdAccount = environment.getProperty("zhcx.zd");
        if (StringUtils.isNotEmpty(zdAccount)) {
            Long maxCapaId = capaService.getMaxCapa().getId();
            // 1.没有达到最大等级跳出
            if (NumberUtils.compare(eventDto.getTagCapaId(), maxCapaId) < 0) {
                return;
            }
            // 2.本身绑定的是总店退出
            User zdUser = userService.getByAccount(zdAccount);
            UserInvitation userInvitation = userInvitationService.getByUser(userCapa.getUid());
            Integer orgPid = userInvitation.getPId(); // 历史上级，也是我下级的上级
            if (orgPid.intValue() == zdUser.getId().intValue()) {
                return;
            }
            //  3.满足培育下级的级别
            List<Capa> pre = capaService.getPre(maxCapaId);
            List<Long> usableCapaIdList = pre.stream().map(Capa::getId).collect(Collectors.toList());
            usableCapaIdList.add(maxCapaId);
            // 获取到我的一阶
            List<UserInvitation> nextList = userInvitationService.getNextList(userCapa.getUid());
            // 从一阶里面找2个比我等级低一级的客户，成为我的培育下级
            nextList = nextList.stream().sorted(Comparator.comparing(UserInvitation::getUId)).collect(Collectors.toList());
            int i = 0;
            for (UserInvitation invitation : nextList) {
                UserCapa nextCapa = userCapaService.getByUser(invitation.getUId());
                if (nextCapa != null && usableCapaIdList.contains(nextCapa.getCapaId())) {
                    invitation.setMId(invitation.getPId());
                    userInvitationService.updateById(invitation);
                    i++;
                }
                if (i >= 2) {
                    break;
                }
            }
            // 4.断开自己的上级 和 一阶的上级
            userInvitationService.del(userCapa.getUid());
            for (UserInvitation invitation : nextList) {
                userInvitationService.del(invitation.getUId());
            }
            // 5.自己链接总店
            userInvitationService.band(userCapa.getUid(), zdUser.getId(), false, true, true);
            invitationJumpService.add(userCapa.getUid(), zdUser.getId(), orgPid);
            // 6.一阶绑定原有上级
            for (UserInvitation invitation : nextList) {
                userInvitationService.band(invitation.getUId(), orgPid, false, true, true);
                // 增加关系跳转
                invitationJumpService.add(invitation.getUId(), orgPid, userInvitation.getPId());
            }
        }
    }
}