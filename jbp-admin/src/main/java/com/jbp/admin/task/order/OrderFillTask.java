package com.jbp.admin.task.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.enums.OrderFillType;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 用户补单task任务
 */
@Component("OrderFillTask")
public class OrderFillTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderFillTask.class);

    @Autowired
    private OrderFillService orderFillService;

    /**
     * 1分钟同步一次数据
     */
    public void userFill() {
        // cron : 0 */1 * * * ?
        logger.info("---OrderFillTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {

            //查询过期的补单
            List<OrderFill> orderFillList = orderFillService.list(new QueryWrapper<OrderFill>().lambda().lt(OrderFill::getExpiredTime, new Date()).eq(OrderFill::getStatus, OrderFillType.待补单.getName()));
            for (OrderFill orderFill : orderFillList) {
                orderFillService.expired(orderFill);
            }

            //补单
            List<OrderFill> fillList = orderFillService.list(new QueryWrapper<OrderFill>().lambda().ge(OrderFill::getExpiredTime, new Date()).eq(OrderFill::getStatus, OrderFillType.待补单.getName()));
            for (OrderFill orderFill : fillList) {
                orderFillService.fill(orderFill);
            }



        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderFillTask.task" + " | msg : " + e.getMessage());
        }
    }
}
