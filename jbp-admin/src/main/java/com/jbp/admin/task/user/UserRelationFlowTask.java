package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.service.agent.UserRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;


    public void refresh() {
        // cron : 0 0 1 * * ?
        logger.info("---UserRelationFlowTask refresh------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("UserRelationFlowTask.refresh", 1);
        //2.设置锁的过期时间,防止死锁
        if(!task){
            //没有争抢(设置)到锁
            logger.info("上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("UserRelationFlowTask.refresh",10, TimeUnit.MINUTES);
        try {
            List<UserRelation> noFlowList = userRelationService.getNoFlowList();
            for (UserRelation userRelation : noFlowList) {
                userRelationFlowService.refresh(userRelation.getUId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserRelationFlowTask.refresh" + " | msg : " + e.getMessage());
        }finally {
            redisTemplate.delete("UserRelationFlowTask.refresh");
        }
    }
}
