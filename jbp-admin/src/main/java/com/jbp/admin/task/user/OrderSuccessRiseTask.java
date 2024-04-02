package com.jbp.admin.task.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.constants.TaskConstants;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.RedisUtil;
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
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private RedisUtil redisUtil;

    public void rise() {
        logger.info("---OrderSuccessRiseTask rise------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        String redisKey = TaskConstants.TASK_ORDER_SUCCESS_USER_RISE_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderSuccessRiseTask.rise | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderNoData = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(orderNoData)) {
                continue;
            }
            try {
                userCapaService.asyncRiseCapa(Integer.valueOf(orderNoData.toString()));
                userCapaXsService.asyncRiseCapaXs(Integer.valueOf(orderNoData.toString()));
            } catch (Exception e) {
                logger.error("订单成功升级错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderNoData);
            }
        }
    }

}
