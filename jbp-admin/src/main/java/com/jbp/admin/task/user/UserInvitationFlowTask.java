package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刷新用户销售层级关系和等级
 */
@Component("UserInvitationFlowTask")
public class UserInvitationFlowTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserInvitationFlowTask.class);

    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserInvitationFlowService userInvitationFlowService;


    public void refreshFlowAndTeam() {
        // cron : 0 0 1 * * ?
        logger.info("---UserInvitationFlowTask refreshFlowAndTeam------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        try {
            List<UserInvitation> noFlowList = userInvitationService.getNoFlowList();
            for (UserInvitation userInvitation : noFlowList) {
                userInvitationFlowService.refreshFlowAndTeam(userInvitation.getUId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserInvitationFlowTask.refreshFlowAndTeam" + " | msg : " + e.getMessage());
        }
    }
}