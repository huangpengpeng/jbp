package com.jbp.admin.task.order;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.service.service.AsyncService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.OrderTaskService;
import com.jbp.service.service.impl.AsyncServiceImpl;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 订单修复
 */
@Component("OrderRepairTask")
public class OrderRepairTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRepairTask.class);

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private OrderService orderService;

    public void repairOrderPaySuccessSplit() {
        //cron : 0 */1 * * * ?
        logger.info("---OrderRepairTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {

            String sql = " select * from eb_order where  paid=1 and status=1 and level=0 and is_del is false ";
            List<Map<String, Object>> maps = SqlRunner.db().selectList(sql);
            for (Map<String, Object> map : maps) {
                String order_no = MapUtils.getString(map, "order_no");
                asyncService.orderPaySuccessSplit(order_no);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderRepairTask.task" + " | msg : " + e.getMessage());
        }
    }


}
