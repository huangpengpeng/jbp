package com.jbp.service.event.listener;

import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.service.event.UserCapaUpdateEvent;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserScoreService;
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
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserScoreService userScoreService;

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


        //升级后增加分数
        //合伙人分数
        String risePartnerMark = systemConfigService.getValueByKey("rise_partner_mark");
        //事业合伙人
        String riseCauseMark = systemConfigService.getValueByKey("rise_cause_mark");
        //分公司
        String riseFilialeMark = systemConfigService.getValueByKey("rise_filiale_mark");
        Integer score = 0;
        if (userCapa.getCapaId() == 2) {
            score =  risePartnerMark == null? 0: Integer.valueOf(risePartnerMark);
        } else if (userCapa.getCapaId() == 3) {
            score = riseCauseMark == null? 0:  Integer.valueOf(riseCauseMark);
        } else if (userCapa.getCapaId() == 4) {
            score = riseFilialeMark == null? 0: Integer.valueOf(riseFilialeMark);
        }
        if(score > 0 ) {
            userScoreService.increase(userCapa.getUid(), score, "升级");
        }

    }
}
