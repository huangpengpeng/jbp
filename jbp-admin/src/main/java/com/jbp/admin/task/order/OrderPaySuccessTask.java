package com.jbp.admin.task.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.OrderTaskService;

import com.jbp.service.service.agent.OrderSuccessMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单支付成功后置task任务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Component("OrderPaySuccessTask")
public class OrderPaySuccessTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderPaySuccessTask.class);

    @Autowired
    private OrderSuccessMsgService orderSuccessMsgService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 1分钟同步一次数据
     */
    public void orderPayAfter() {
        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("OrderPaySuccessTask.orderPayAfter", 1);
        //2.设置锁的过期时间,防止死锁
        redisTemplate.expire("OrderPaySuccessTask.orderPayAfter",10, TimeUnit.MINUTES);
        if(!task){
            //没有争抢(设置)到锁
            logger.info("上一次任务未执行完成退出");
            return;//方法结束
        }
        // cron : 0 */1 * * * ?
        logger.info("---OrderPaySuccessTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            List<OrderSuccessMsg> list = orderSuccessMsgService.list(new QueryWrapper<OrderSuccessMsg>().lambda().eq(OrderSuccessMsg::getExec, false));
            for (OrderSuccessMsg msg : list) {
                orderSuccessMsgService.exec(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderPaySuccessTask.task" + " | msg : " + e.getMessage());
        }finally {
            redisTemplate.delete("OrderPaySuccessTask.orderPayAfter");
        }

    }

}
