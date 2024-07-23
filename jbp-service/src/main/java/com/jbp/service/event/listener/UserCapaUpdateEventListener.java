package com.jbp.service.event.listener;

import com.beust.jcommander.internal.Sets;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserInvitationJump;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
        // 店主 等级
        Long maxCapaId = capaService.getMaxCapa().getId();
        // VIP 等级
        Capa pre = capaService.getPre(maxCapaId).get(0);
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
        if (orgPid == null || orgPid.intValue() == zdUser.getId().intValue()) {
            return;
        }

        // 3.断开自己的上级
        userInvitationService.del(userCapa.getUid());
        // 4.自己绑定总店
        userInvitationService.band(userCapa.getUid(), zdUser.getId(), false, true, true);
        invitationJumpService.add(userCapa.getUid(), zdUser.getId(), orgPid);

        // 获取自己的一阶
        List<UserInvitation> nextList = userInvitationService.getNextList(userCapa.getUid());
        // 5.获取培育下级 有且仅有2个培育下级
        List<UserInvitation> mNextList = userInvitationService.getByMid(userCapa.getUid());

        Integer maxNum = 2;
        String property = environment.getProperty("zhcx.two");
        if(StringUtils.isNotEmpty(property) && StringUtils.equals("1", property)){
            maxNum = 1000;
        }
        Integer num = maxNum - mNextList.size();
        Set<Integer> peiyuNextList = Sets.newHashSet();
        if (num > 0) {
            List<Integer> mUserList = mNextList.stream().map(UserInvitation::getUId).collect(Collectors.toList());
            LinkedList<UserInvitationJump> jumpList = invitationJumpService.getFirst4OrgPid(userCapa.getUid());
            for (UserInvitationJump jump : jumpList) {
                UserCapa nextCapa = userCapaService.getByUser(jump.getUId());
                if (nextCapa != null && nextCapa.getCapaId().intValue() >= pre.getId().intValue() && !mUserList.contains(nextCapa.getUid())) {
                    peiyuNextList.add(jump.getUId());
                }
                if (peiyuNextList.size() == num) {
                    break;
                }
            }
            if (peiyuNextList.size() < num) {
                for (UserInvitation invitation : nextList) {
                    if (!invitationJumpService.ifJump(invitation.getUId()) && invitation.getMId() == null) {
                        UserCapa nextCapa = userCapaService.getByUser(invitation.getUId());
                        if (nextCapa != null && nextCapa.getCapaId().intValue() >= pre.getId().intValue() && !mUserList.contains(nextCapa.getUid())) {
                            peiyuNextList.add(invitation.getUId());
                        }
                        if (peiyuNextList.size() == num) {
                            break;
                        }
                    }
                }
            }
        }

        // 断开一阶
        for (UserInvitation invitation : nextList) {
            Integer nextUId = invitation.getUId();
            Integer mId = invitation.getMId();
            UserCapa nextUserCapa = userCapaService.getByUser(nextUId);
            if (nextUserCapa != null && nextUserCapa.getCapaId().intValue() >= pre.getId().intValue()) {
                userInvitationService.del(nextUId);
                userInvitationService.band(nextUId, orgPid, false, true, true);
                if (mId != null && mId.intValue() > 0) {
                    invitation = userInvitationService.getByUser(nextUId);
                    invitation.setMId(mId);
                    userInvitationService.updateById(invitation);
                }
                invitationJumpService.add(nextUId, orgPid, userCapa.getUid());
            }
        }

        // 设置自己的培育下级
        for (Integer uid : peiyuNextList) {
            UserInvitation invitation = userInvitationService.getByUser(uid);
            invitation.setMId(userCapa.getUid());
            userInvitationService.updateById(invitation);
        }

    }
}
