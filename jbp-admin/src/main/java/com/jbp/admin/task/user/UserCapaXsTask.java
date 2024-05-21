package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.user.User;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户星级任务
 */
@Component("UserCapaXsTask")
public class UserCapaXsTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(UserCapaXsTask.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private UserService userService;

    /**
     * 每天凌晨1点执行
     */
    public void refreshUserCapaXs() {
        // cron : 0 0 1 * * ?
        logger.info("---UserCapaXsTask refreshUserCapaXs------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        Boolean task = redisTemplate.opsForValue().setIfAbsent("UserCapaXsTask.refreshUserCapaXs", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("UserCapaXsTask.refreshUserCapaXs上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("UserCapaXsTask.refreshUserCapaXs", 500, TimeUnit.MINUTES);
        try {
            List<User> list = userService.list();
            for (User user : list) {
                userCapaXsService.riseCapaXs(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserCapaXsTask.refreshUserCapaXs" + " | msg : " + e.getMessage());
        } finally {
            redisTemplate.delete("UserCapaXsTask.refreshUserCapaXs");
        }
    }
}
