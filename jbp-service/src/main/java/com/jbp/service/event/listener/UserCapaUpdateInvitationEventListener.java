package com.jbp.service.event.listener;

import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.service.event.UserCapaUpdateEvent;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserCapaUpdateInvitationEventListener implements ApplicationListener<UserCapaUpdateEvent> {
    @Resource
    private UserInvitationService userInvitationService;

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
