package com.jbp.service.event.listener;

import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
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
        // 合纵创享
        String zdAccount = environment.getProperty("zhcx.zd");
        if (StringUtils.isEmpty(zdAccount)) {
            return;
        }
        // 达到最大等级切换关系
        UserCapaUpdateEvent.EventDto eventDto = userCapaUpdateEvent.getEventDto();
        Long maxCapaId = capaService.getMaxCapa().getId();
        // 1.没有达到最大等级跳出
        if (NumberUtils.compare(eventDto.getTagCapaId(), maxCapaId) < 0) {
            return;
        }
        UserCapa userCapa = eventDto.getUserCapa();
        String orgAccount = environment.getProperty("zhcx.org");
        User user = userService.getById(userCapa.getUid());
        if (user.getAccount().equals(orgAccount) || user.getAccount().equals(zdAccount)) {
            return;
        }
        // 2.本身绑定的是总店退出
        User zdUser = userService.getByAccount(zdAccount);
        UserInvitation userInvitation = userInvitationService.getByUser(userCapa.getUid());
        // 历史上级，也是我下级的上级
        Integer orgPid = userInvitation.getPId();
        if (orgPid.intValue() == zdUser.getId().intValue()) {
            return;
        }
        // 3.断开自己的上级 和 一阶的上级
        userInvitationService.del(userCapa.getUid());
        // 4.自己绑定总店
        userInvitationService.band(userCapa.getUid(), zdUser.getId(), false, true, true);
        invitationJumpService.add(userCapa.getUid(), zdUser.getId(), orgPid);
        // 5..全部一阶断开
        List<UserInvitation> nextList = userInvitationService.getNextList(userCapa.getUid());
        nextList = nextList.stream().sorted(Comparator.comparing(UserInvitation::getUId)).collect(Collectors.toList());
        for (UserInvitation invitation : nextList) {
            userInvitationService.del(invitation.getUId());
        }
        //  6..满足培育下级的级别
        List<Capa> pre = capaService.getPre(maxCapaId);
        List<Long> usableCapaIdList = pre.stream().map(Capa::getId).collect(Collectors.toList());
        // 7.一阶绑定原有上级
        int j = 0;
        for (int i = 0; i < nextList.size(); i++) {
            UserInvitation invitation = nextList.get(i);
            if (orgPid != null) {
                userInvitationService.band(invitation.getUId(), orgPid, false, true, true);
            }
            // 8.增加关系跳转
            invitationJumpService.add(invitation.getUId(), orgPid, userInvitation.getPId());
            // 9.增加培育
            UserCapa nextUserCapa = userCapaService.getByUser(invitation.getUId());
            if (nextUserCapa != null && usableCapaIdList.contains(nextUserCapa.getCapaId()) && !invitationJumpService.ifJump(invitation.getUId())) {
                if (i < 2) {
                    invitation = userInvitationService.getByUser(invitation.getUId());
                    invitation.setMId(userCapa.getUid());
                    userInvitationService.updateById(invitation);
                }
                j++;
            }
        }
    }
}
