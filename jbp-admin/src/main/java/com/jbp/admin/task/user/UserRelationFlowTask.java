package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.service.agent.UserRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 刷新用户服务层级关系和等级
 */
@Component("UserRelationFlowTask")
public class UserRelationFlowTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserRelationFlowTask.class);

    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserRelationFlowService userRelationFlowService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    public void refresh() {
        // cron : 0 0 1 * * ?
        logger.info("---UserRelationFlowTask refresh------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        if (StringUtils.isNotEmpty(stringRedisTemplate.opsForValue().get("refresh"))){
            return;
        }
        stringRedisTemplate.opsForValue().set("refresh","1");
        try {
            List<UserRelation> noFlowList = userRelationService.getNoFlowList();
            for (UserRelation userRelation : noFlowList) {
                userRelationFlowService.refresh(userRelation.getUId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserRelationFlowTask.refresh" + " | msg : " + e.getMessage());
        }finally {
            stringRedisTemplate.delete("refresh");
        }
    }
}
