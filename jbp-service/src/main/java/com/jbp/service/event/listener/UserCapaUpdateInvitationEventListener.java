package com.jbp.service.event.listener;

import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.event.UserCapaUpdateEvent;
import com.jbp.service.service.SystemConfigService;
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
        Long oagCapaId = eventDto.getOrgCapaId();
        Long tagCapaId = eventDto.getTagCapaId();
        UserCapa userCapa = eventDto.getUserCapa();
        if(oagCapaId.intValue() != tagCapaId.intValue()){
            String ifOpen =  systemConfigService.getValueByKey("ifOpen");
            String capaId =  systemConfigService.getValueByKey("capaId");
            UserInvitation userInvitation =  userInvitationService.getByUser(userCapa.getUid());
            if(!userInvitation.getIfForce() && ifOpen.equals("1") && Long.valueOf(capaId).intValue() <= tagCapaId.intValue()){
                userInvitation.setIfForce(true);
                userInvitationService.updateById(userInvitation);
            }

        }

        }
    }
