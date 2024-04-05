package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户等级任务
 */
@Component("UserCapaTask")
public class UserCapaTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserCapaTask.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每天凌晨1点执行
     */
    public void refreshUserCapa() {
        // cron : 0 0 1 * * ?
        logger.info("---UserCapaTask refreshUserCapa------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        Boolean task = redisTemplate.opsForValue().setIfAbsent("UserCapaTask.refreshUserCapa", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("UserCapaTask.refreshUserCapa上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("UserCapaTask.refreshUserCapa", 500, TimeUnit.MINUTES);

        try {
            List<User> list = userService.list();
            for (User user : list) {
                userCapaService.riseCapa(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserCapaTask.refreshUserCapa" + " | msg : " + e.getMessage());
        }finally {
            redisTemplate.delete("UserCapaTask.refreshUserCapa");
        }
    }
}
