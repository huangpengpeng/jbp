package com.jbp.admin.task.order;

import com.jbp.common.constants.PayConstants;
import com.jbp.common.kqbill.result.KqPayQueryResult;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.RechargeOrder;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付结果同步任务
 */
@Component("OrderPayResultSyncTask")
public class OrderPayResultSyncTask {

    @Resource
    private OrderService orderService;
    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private PayCallbackService payCallbackService;
    @Resource
    private RechargeOrderService rechargeOrderService;
    @Resource
    private KqPayService kqPayService;

    private static final Logger logger = LoggerFactory.getLogger(OrderPayResultSyncTask.class);

    public void payResultSync() {
        // cron : 0 */1 * * * ?
        logger.info("---OrderPayResultSyncTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            List<Order> orders = orderService.getWaitPayList(15);
            for (Order order : orders) {
                if (order.getPayChannel().equals(PayConstants.PAY_TYPE_LIANLIAN) && StringUtils.isNotEmpty(order.getOutTradeNo())) {
                    QueryPaymentResult result = lianLianPayService.queryPayResult(order.getOrderNo());
                    if (result != null || "TRADE_SUCCESS".equals(result.getTxn_status())) {
                        payCallbackService.lianLianPayCallback(result);
                    }
                } else if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_KQ)) {
                    KqPayQueryResult result = kqPayService.queryPayResult(order.getOrderNo());
                    if (result != null && result.ifSuccess()) {
                        payCallbackService.kqPayCallback(result);
                    }
                }
            }
            List<RechargeOrder> rechargeOrders = rechargeOrderService.getWaitPayList(15);
            for (RechargeOrder order : rechargeOrders) {
                if (order.getPayChannel().equals(PayConstants.PAY_TYPE_LIANLIAN) && StringUtils.isNotEmpty(order.getOutTradeNo())) {
                    QueryPaymentResult result = lianLianPayService.queryPayResult(order.getOrderNo());
                    if (result != null || "TRADE_SUCCESS".equals(result.getTxn_status())) {
                        payCallbackService.lianLianPayCallback(result);
                    }
                } else if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_KQ)) {
                    KqPayQueryResult result = kqPayService.queryPayResult(order.getOrderNo());
                    if (result != null && result.ifSuccess()) {
                        payCallbackService.kqPayCallback(result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderPayResultSyncTask.task" + " | msg : " + e.getMessage());
        }
    }


}
