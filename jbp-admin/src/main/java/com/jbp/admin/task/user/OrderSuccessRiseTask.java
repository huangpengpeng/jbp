package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.OrderSuccessMsgService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("OrderSuccessRiseTask")
public class OrderSuccessRiseTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderSuccessRiseTask.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private OrderSuccessMsgService orderSuccessMsgService;
    @Autowired
    private RedisTemplate redisTemplate;

    public void rise() {

        // 1.加锁成功
        Boolean task = redisTemplate.opsForValue().setIfAbsent("OrderSuccessRiseTask.rise", 1);
        //2.设置锁的过期时间,防止死锁
        if (!task) {
            //没有争抢(设置)到锁
            logger.info("OrderSuccessRiseTask.rise上一次任务未执行完成退出");
            return;//方法结束
        }
        redisTemplate.expire("OrderSuccessRiseTask.rise", 10, TimeUnit.MINUTES);

        // cron : 0 0 1 * * ?
        logger.info("---OrderSuccessRiseTask rise------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        try {
            List<OrderSuccessMsg> list = orderSuccessMsgService.list(new QueryWrapper<OrderSuccessMsg>().lambda().orderByDesc(OrderSuccessMsg::getId).last(" limit 10"));
            for (OrderSuccessMsg orderSuccessMsg : list) {
                Order order = orderService.getByOrderNo(orderSuccessMsg.getOrdersSn());
                userCapaService.asyncRiseCapa(order.getUid());
                userCapaXsService.asyncRiseCapaXs(order.getUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderSuccessRiseTask.rise" + " | msg : " + e.getMessage());
        } finally {
            redisTemplate.delete("OrderSuccessRiseTask.rise");
        }
    }
}
