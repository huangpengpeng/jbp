package com.jbp.service.event.listener;

import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.service.event.UserCapaUpdateEvent;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserCapaUpdateInvitationEventListener implements ApplicationListener<UserCapaUpdateEvent> {

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
    private SystemConfigService systemConfigService;

    @Async
    @Override
    public void onApplicationEvent(UserCapaUpdateEvent userCapaUpdateEvent) {

        UserCapaUpdateEvent.EventDto eventDto = userCapaUpdateEvent.getEventDto();
        UserCapa userCapa = eventDto.getUserCapa();
        UserInvitation userInvitation = userInvitationService.getByUser(userCapa.getUid());
        if (userInvitation != null && !userInvitation.getIfForce()) {
            userInvitation.setIfForce(true);
            userInvitationService.updateById(userInvitation);
        }


    }
}
