package com.jbp.admin.task.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.agent.FundClearingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("FundClearingTask")
public class FundClearingTask {

    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FundClearingService fundClearingService;

    //日志
    private static final Logger logger = LoggerFactory.getLogger(FundClearingTask.class);

    /**
     * 自动出款
     */
    public void send() {
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("FundClearingTask.send", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("FundClearingTask.send", 60, TimeUnit.MINUTES);
        logger.info("---FundClearingTask.send------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            String yck = systemConfigService.getValueByKey("fund_clearing_status_yck");
            if (StringUtils.isNotEmpty(yck) && !"1".equals(yck)) {
                logger.info("---FundClearingTask.send------配置未开启");
                return;
            }
            List<FundClearing> list = fundClearingService.list(new LambdaQueryWrapper<FundClearing>().eq(FundClearing::getStatus, FundClearing.Constants.待出款.toString()).last(" limit 500"));
            List<Long> ids = list.stream().map(FundClearing::getId).collect(Collectors.toList());
            fundClearingService.updateSend(ids, "自动已出款");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FundClearingTask.send" + " | msg : " + e.getMessage());
        } finally {
            redisTemplate.delete("FundClearingTask.send");
        }
    }
}
