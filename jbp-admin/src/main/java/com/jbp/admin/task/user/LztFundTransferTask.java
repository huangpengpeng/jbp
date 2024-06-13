package com.jbp.admin.task.user;

import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.agent.LztFundTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component("LztFundTransferTask")
public class LztFundTransferTask {

    @Resource
    private LztFundTransferService lztFundTransferService;
    @Resource
    private RedisTemplate redisTemplate;


    //日志
    private static final Logger logger = LoggerFactory.getLogger(LztFundTransferTask.class);

    /**
     * 自动出款
     */
    public void send() {
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("LztFundTransferTask.send", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("银行账户自动划拨上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("LztFundTransferTask.send", 60, TimeUnit.MINUTES);
        logger.info("---LztFundTransferTask.send------银行账户自动划拨: 执行时间 Time - {}", CrmebDateUtil.nowDateTime());
        try {
            lztFundTransferService.autoFundTransfer();
            logger.info("---LztFundTransferTask.send------银行账户自动划拨: 执行时间完成 Time - {}", CrmebDateUtil.nowDateTime());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LztFundTransferTask.send  银行账户自动划拨" + " | msg : " + e.getMessage());
        } finally {
            redisTemplate.delete("LztFundTransferTask.send");
        }
    }
}
