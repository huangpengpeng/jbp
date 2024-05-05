package com.jbp.admin.task.user;

import com.jbp.admin.task.order.OrderPaySuccessTask;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.TmpUserService;
import com.jbp.service.service.agent.WalletGivePlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("TemUserTask")
public class TemUserTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderPaySuccessTask.class);

    @Autowired
    private TmpUserService tmpUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 1分钟同步一次数据
     */
    public void create() {
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("TemUserTask.create", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("TemUserTask.create", 10, TimeUnit.MINUTES);
        logger.info("---TemUserTask.create------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            tmpUserService.create();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("TemUserTask.create" + " | msg : " + e.getMessage());
        } finally {
            redisTemplate.delete("TemUserTask.create");
        }
    }
}
