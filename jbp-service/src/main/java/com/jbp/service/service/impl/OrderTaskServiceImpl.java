package com.jbp.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.jbp.service.service.*;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.bill.Bill;
import com.jbp.common.model.bill.MerchantBill;
import com.jbp.common.model.order.*;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.system.SystemNotification;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserBrokerageRecord;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.model.user.UserToken;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.RedisUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreOrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private PayService payService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private MerchantBillService merchantBillService;
    @Autowired
    private BillService billService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    SystemNotificationService systemNotificationService;
    @Autowired
    UserTokenService userTokenService;
    @Autowired
    TemplateMessageService templateMessageService;


    /**
     * 用户取消订单
     */
    @Override
    public void cancelByUser() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.cancelByUser | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = userCancelOrder(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 用户取消订单
     *
     * @param orderNo 订单号
     */
    private Boolean userCancelOrder(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("用户取消支付订单，订单不存在，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL)) {
            logger.error("用户取消支付订单，订单状态该异常，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            logger.error("用户取消支付订单，订单状态取消异常，订单号为:{}", orderNo);
            return Boolean.FALSE;
        }
        if (order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_SYSTEM)) {
            return Boolean.TRUE;
        }
        User user = userService.getById(order.getUid());
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(order.getOrderNo());
        List<Integer> couponIdList = merchantOrderList.stream().filter(e -> e.getCouponId() > 0).map(MerchantOrder::getCouponId).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            //写订单日志
            orderStatusService.createLog(orderNo, "cancel_order", "用户取消订单");
            // 退优惠券
            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.rollbackByIds(couponIdList);
            }
            // 退积分
            if (order.getUseIntegral() > 0) {
                userService.updateIntegral(order.getUid(), order.getUseIntegral(), Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord userIntegralRecord = initOrderCancelIntegralRecord(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }
            Boolean rollbackStock = rollbackStock(order);
            if (!rollbackStock) {
                e.setRollbackOnly();
                logger.error("订单回滚库存失败,订单号:{}", order.getOrderNo());
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 用户积分记录——订单取消
     * @param uid 用户ID
     * @param useIntegral 使用的积分
     * @param integral 用户当前积分
     * @param orderNo 订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initOrderCancelIntegralRecord(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(StrUtil.format("订单取消，退回金额抵扣积分：{}", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral + useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 订单退款
     */
    @Override
    public void orderRefund() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderRefund | size:" + size);
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
                Boolean result = refundAfterProcessing(orderNoData.toString());
                if (!result) {
                    redisUtil.lPush(redisKey, orderNoData);
                }
            } catch (Exception e) {
                logger.error("订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderNoData);
            }
        }
    }

    /**
     * 订单退款后置处理
     *
     * @param refundOrderNo 退款订单号
     */
    private Boolean refundAfterProcessing(String refundOrderNo) {
        RefundOrder refundOrder = refundOrderService.getInfoException(refundOrderNo);
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)) {
            throw new CrmebException("退款单状态异常");
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        String orderNo = order.getOrderNo();
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);
        List<UserIntegralRecord> integralRecordAddList = CollUtil.newArrayList();
        List<UserIntegralRecord> integralRecordUpdateList = CollUtil.newArrayList();
        // 退使用积分
        if (refundOrder.getRefundUseIntegral() > 0) {
            UserIntegralRecord userIntegralRecord = refundUseIntegralRecordInit(refundOrder, refundOrderInfo);
            integralRecordAddList.add(userIntegralRecord);
        }
        // 退赠送积分
        if (refundOrder.getRefundGainIntegral() > 0) {
            // 获取此订单支付时的商户订单积分记录
            UserIntegralRecord gainIntegralRecord = userIntegralRecordService.getByOrderNoAndType(orderNo, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
            if (gainIntegralRecord.getStatus() < IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE) {
                if (gainIntegralRecord.getIntegral().equals(refundOrder.getRefundGainIntegral())) {
                    gainIntegralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION);
                    gainIntegralRecord.setUpdateTime(DateUtil.date());
                } else {
                    gainIntegralRecord.setIntegral(gainIntegralRecord.getIntegral() - refundOrder.getRefundGainIntegral());
                    gainIntegralRecord.setUpdateTime(DateUtil.date());
                }
                integralRecordUpdateList.add(gainIntegralRecord);
            }
        }
        // 分佣返还
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        if (refundOrder.getRefundFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
            // 获取对应分佣记录
            List<UserBrokerageRecord> userBrokerageRecordList = userBrokerageRecordService.getByOrderNo(orderNo);
            if (CollUtil.isNotEmpty(userBrokerageRecordList) && userBrokerageRecordList.get(0).getStatus() < BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE) {
                userBrokerageRecordList.forEach(r -> {
                    if (r.getBrokerageLevel().equals(1)) {
                        if (r.getPrice().compareTo(refundOrderInfo.getRefundFirstBrokerageFee()) == 0) {
                            r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                        } else {
                            r.setPrice(r.getPrice().subtract(refundOrderInfo.getRefundFirstBrokerageFee()));
                        }
                    }
                    if (r.getBrokerageLevel().equals(2)) {
                        if (r.getPrice().compareTo(refundOrderInfo.getRefundSecondBrokerageFee()) == 0) {
                            r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                        } else {
                            r.setPrice(r.getPrice().subtract(refundOrderInfo.getRefundSecondBrokerageFee()));
                        }
                    }
                    brokerageRecordList.add(r);
                });
            }
        }
        OrderProfitSharing orderProfitSharing = orderProfitSharingService.getByOrderNo(orderNo);
        orderProfitSharing.setProfitSharingRefund(orderProfitSharing.getProfitSharingRefund().add(refundOrder.getRefundPrice()));
        if (refundOrderInfo.getRefundUseIntegral() > 0) {
            orderProfitSharing.setRefundUseIntegral(orderProfitSharing.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
            orderProfitSharing.setRefundIntegralPrice(orderProfitSharing.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
        }

        BigDecimal platReplacePrice = refundOrderInfo.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee());
        refundOrder.setMerchantRefundPrice(refundOrder.getRefundPrice().subtract(platReplacePrice));
        if (CollUtil.isEmpty(brokerageRecordList)) {
            refundOrder.setIsReplace(true);
        }

        MerchantBill merchantBill = initMerchantBillRefund(refundOrder);

        return transactionTemplate.execute(e -> {
            // 回滚库存
            refundRollbackStock(refundOrderInfo);
            // 扣商户资金
            merchantService.operationBalance(refundOrder.getMerId(), refundOrder.getMerchantRefundPrice(), Constants.OPERATION_TYPE_SUBTRACT);
            merchantBillService.save(merchantBill);
            // 更新退款单
            refundOrderService.updateById(refundOrder);

            if (CollUtil.isNotEmpty(integralRecordAddList)) {
                userIntegralRecordService.saveBatch(integralRecordAddList);
            }
            if (CollUtil.isNotEmpty(integralRecordUpdateList)) {
                userIntegralRecordService.updateBatchById(integralRecordUpdateList);
            }
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            } else {
                // 平台代扣记录
                Bill bill = new Bill();
                bill.setOrderNo(refundOrder.getRefundOrderNo());
                bill.setMerId(refundOrder.getMerId());
                bill.setPm(BillConstants.BILL_PM_SUB);
                bill.setAmount(platReplacePrice);
                bill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
                bill.setMark(StrUtil.format("订单退款，平台代扣佣金应退金额{}元", platReplacePrice));
                billService.save(bill);
            }
            if (refundOrderInfo.getRefundUseIntegral() > 0) {
                userService.updateIntegral(refundOrder.getUid(), refundOrderInfo.getRefundUseIntegral(), Constants.OPERATION_TYPE_ADD);
                Bill bill = new Bill();
                bill.setOrderNo(refundOrder.getRefundOrderNo());
                bill.setMerId(refundOrder.getMerId());
                bill.setPm(BillConstants.BILL_PM_ADD);
                bill.setAmount(refundOrderInfo.getRefundIntegralPrice());
                bill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
                bill.setMark(StrUtil.format("订单退款，平台代扣积分抵扣金额返还{}元", refundOrderInfo.getRefundIntegralPrice()));
                billService.save(bill);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 初始化商户帐单-退款
     *
     * @param refundOrder 退款单
     */
    private MerchantBill initMerchantBillRefund(RefundOrder refundOrder) {
        MerchantBill merchantBill = new MerchantBill();
        merchantBill.setMerId(refundOrder.getMerId());
        merchantBill.setType(BillConstants.BILL_TYPE_REFUND_ORDER);
        merchantBill.setOrderNo(refundOrder.getRefundOrderNo());
        merchantBill.setUid(refundOrder.getUid());
        merchantBill.setPm(BillConstants.BILL_PM_SUB);
        merchantBill.setAmount(refundOrder.getMerchantRefundPrice());
        merchantBill.setMark(StrUtil.format("订单{}退款{}元，商户支出{}元", refundOrder.getOrderNo(), refundOrder.getRefundPrice(), refundOrder.getMerchantRefundPrice()));
        return merchantBill;
    }

    /**
     * 退款回滚库存
     *
     * @param refundOrderInfo 退款单详情
     */
    private void refundRollbackStock(RefundOrderInfo refundOrderInfo) {
        productService.operationStock(refundOrderInfo.getProductId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD);
        ProductAttrValue attrValue = productAttrValueService.getById(refundOrderInfo.getAttrValueId());
        productAttrValueService.operationStock(attrValue.getId(), refundOrderInfo.getApplyRefundNum(), Constants.OPERATION_TYPE_ADD, attrValue.getType(), attrValue.getVersion());
    }

    /**
     * 初始化退积分抵扣部分积分记录
     *
     * @param refundOrder     退款单
     * @param refundOrderInfo 退款单详情
     */
    private UserIntegralRecord refundUseIntegralRecordInit(RefundOrder refundOrder, RefundOrderInfo refundOrderInfo) {
        UserIntegralRecord record = new UserIntegralRecord();
        record.setUid(refundOrder.getUid());
        record.setLinkId(refundOrder.getRefundOrderNo());
        record.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER_REFUND);
        record.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        record.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_REFUND);
        record.setIntegral(refundOrderInfo.getRefundUseIntegral());
        record.setMark(StrUtil.format("订单退款，返还支付使用的{}积分", record.getIntegral()));
        record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return record;
    }

//    /**
//     * 完成订单
//
//     * @since 2020-07-09
//     */
//    @Override
//    public void complete() {
//        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER;
//        Long size = redisUtil.getListSize(redisKey);
//        logger.info("OrderTaskServiceImpl.complete | size:" + size);
//        if (size < 1) {
//            return;
//        }
//        for (int i = 0; i < size; i++) {
//            //如果10秒钟拿不到一个数据，那么退出循环
//            Object data = redisUtil.getRightPop(redisKey, 10L);
//            if (null == data) {
//                continue;
//            }
//            try {
//                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
//                boolean result = storeOrderTaskService.complete(storeOrder);
//                if (!result) {
//                    redisUtil.lPush(redisKey, data);
//                }
//            } catch (Exception e) {
//                redisUtil.lPush(redisKey, data);
//            }
//        }
//    }

    /**
     * 订单支付成功后置处理
     */
    @Override
    public void orderPaySuccessAfter() {
        String redisKey = TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderPaySuccessAfter | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                Boolean result = payService.payAfterProcessingTemp(String.valueOf(data));
//                Boolean result = payService.payAfterProcessing(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                logger.error("order pay task error exception : {}", e.getMessage());
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 自动取消未支付订单
     */
    @Override
    public void autoCancel() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = orderAutoCancel(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 支付订单自动取消
     *
     * @param orderNo 订单编号
     */
    private Boolean orderAutoCancel(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("自动取消支付订单，订单不存在，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getPaid()) {
            return Boolean.TRUE;
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL)) {
            return Boolean.TRUE;
        }
        if (!order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            return Boolean.TRUE;
        }
        // 获取过期时间
        String cancelStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ORDER_CANCEL_TIME);
        DateTime cancelTime;
        if (StrUtil.isBlank(cancelStr)) {
            cancelStr = "5";
        }
        cancelTime = DateUtil.offset(order.getCreateTime(), DateField.MINUTE, Integer.parseInt(cancelStr));
        long between = DateUtil.between(cancelTime, DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// 未到过期时间继续循环
            return Boolean.FALSE;
        }

        User user = userService.getById(order.getUid());
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(order.getOrderNo());
        List<Integer> couponIdList = merchantOrderList.stream().filter(e -> e.getCouponId() > 0).map(MerchantOrder::getCouponId).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            orderService.cancel(order.getOrderNo(), false);
            //写订单日志
            orderStatusService.createLog(order.getOrderNo(), "cancel", "到期未支付系统自动取消");
            // 退优惠券
            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.rollbackByIds(couponIdList);
            }
            // 退积分
            if (order.getUseIntegral() > 0) {
                userService.updateIntegral(order.getUid(), order.getUseIntegral(), Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord userIntegralRecord = initOrderCancelIntegralRecord(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }
            // 回滚库存
            Boolean rollbackStock = rollbackStock(order);
            if (!rollbackStock) {
                e.setRollbackOnly();
                logger.error("订单回滚库存失败,订单号:{}", order.getOrderNo());
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 订单回滚库存
     *
     * @param order 订单
     * @return 回滚结果
     */
    private Boolean rollbackStock(Order order) {
        // 查找出商品详情
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        if (CollUtil.isEmpty(orderDetailList)) {
            logger.error("订单回滚库存未找到商品详情,订单号:{}", order.getOrderNo());
            return false;
        }
        return transactionTemplate.execute(e -> {
            for (OrderDetail orderDetail : orderDetailList) {
                Product product = productService.getById(orderDetail.getProductId());
                if (ObjectUtil.isNotNull(product)) {
                    productService.operationStock(product.getId(), orderDetail.getPayNum(), Constants.OPERATION_TYPE_ADD);
                }
                ProductAttrValue productAttrValue = productAttrValueService.getById(orderDetail.getAttrValueId());
                if (ObjectUtil.isNotNull(productAttrValue)) {
                    productAttrValueService.operationStock(productAttrValue.getId(), orderDetail.getPayNum(), Constants.OPERATION_TYPE_ADD, productAttrValue.getType(), productAttrValue.getVersion());
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 订单收货
     */
    @Override
    public void orderReceiving() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderReceiving | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                Boolean result = takeDeliveryAfter(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 收货后续处理
     *
     * @param orderNo 订单号
     */
    private Boolean takeDeliveryAfter(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("订单收货task处理，未找到订单，orderNo={}", orderNo);
            return Boolean.FALSE;
        }
        User user = userService.getById(order.getUid());
        // 获取佣金记录
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkNoAndLinkType(orderNo, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        logger.info("收货处理佣金条数：" + recordList.size());
        for (UserBrokerageRecord record : recordList) {
            if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                throw new CrmebException(StrUtil.format("订单收货task处理，订单佣金记录不是创建状态，orderNo={}", orderNo));
            }
            // 佣金进入冻结期
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
            // 计算解冻时间
            long thawTime = DateUtil.current(false);
            if (record.getFrozenTime() > 0) {
                DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
                thawTime = dateTime.getTime();
            }
            record.setThawTime(thawTime);
        }

        // 获取积分记录
        List<UserIntegralRecord> integralRecordList = userIntegralRecordService.findListByOrderNoAndUid(orderNo, order.getUid());
        logger.info("收货处理积分条数：" + integralRecordList.size());
        List<UserIntegralRecord> userIntegralRecordList = integralRecordList.stream().filter(e -> e.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)).collect(Collectors.toList());
        for (UserIntegralRecord record : userIntegralRecordList) {
            if (!record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE)) {
                throw new CrmebException(StrUtil.format("订单收货task处理，订单积分记录不是创建状态，orderNo={}", orderNo));
            }
            // 佣金进入冻结期
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
            // 计算解冻时间
            long thawTime = DateUtil.current(false);
            if (record.getFrozenTime() > 0) {
                DateTime dateTime = DateUtil.offsetDay(new Date(), record.getFrozenTime());
                thawTime = dateTime.getTime();
            }
            record.setThawTime(thawTime);
        }

        Boolean execute = transactionTemplate.execute(e -> {
            // 日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_USER_TAKE_DELIVERY, OrderStatusConstants.ORDER_LOG_MESSAGE_TAKE);
            // 分佣-佣金进入冻结期
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.updateBatchById(recordList);
            }
            // 积分进入冻结期
            if (CollUtil.isNotEmpty(userIntegralRecordList)) {
                userIntegralRecordService.updateBatchById(userIntegralRecordList);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送消息 确认收获通知
            pushMessageOrder(order, user);
        }
        return execute;
    }

    /**
     * 订单自动完成
     */
    @Override
    public void autoComplete() {
        Integer autoCompleteDay = Integer.parseInt(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ORDER_AUTO_COMPLETE_DAY));

        List<Order> orderList = orderService.findCanCompleteOrder(autoCompleteDay);
        if (CollUtil.isEmpty(orderList)) {
            logger.info("OrderTaskServiceImpl.autoComplete | size:0");
            return ;
        }
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.batchCompleteByOrderNo(orderNoList);
            orderNoList.forEach(orderNo -> {
                orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_COMPLETE, "订单已完成");
            });
            return Boolean.TRUE;
        });
        if (execute) {
            logger.error("订单自动完成：更新数据库失败，orderNoList = {}", JSON.toJSONString(orderNoList));
        }
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(Order order, User user) {

        SystemNotification notification = systemNotificationService.getByMark(NotifyConstants.RECEIPT_GOODS_MARK);
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            return;
        }
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号

        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && notification.getIsWechat().equals(1)) {
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您购买的商品已确认收货！");
            temMap.put("keyword1", order.getOrderNo());
            temMap.put("keyword2", "已收货");
            temMap.put("keyword3", CrmebDateUtil.nowDateTimeStr());
            temMap.put("keyword4", "详情请进入订单查看");
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
            templateMessageService.pushTemplateMessage(notification.getWechatId(), temMap, userToken.getToken());
        } else if (notification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            List<OrderDetail> orderInvoiceDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<String> productNameList = orderInvoiceDetailList.stream().map(OrderDetail::getProductName).collect(Collectors.toList());
            // 获取商品名称
            String storeNameAndCarNumString = String.join(",", productNameList);

            // 组装数据
            if (StrUtil.isBlank(storeNameAndCarNumString)) {
                return ;
            }
            if (storeNameAndCarNumString.length() > 20) {
                storeNameAndCarNumString = storeNameAndCarNumString.substring(0, 15) + "***";
            }
//        temMap.put("character_string6", storeOrder.getOrderId());
//        temMap.put("phrase4", "已收货");
//        temMap.put("time7", DateUtil.nowDateTimeStr());
//        temMap.put("thing1", storeNameAndCarNumString);
//        temMap.put("thing5", "您购买的商品已确认收货！");
            temMap.put("character_string6", order.getOrderNo());
            temMap.put("date5", CrmebDateUtil.nowDateTimeStr());
            temMap.put("thing2", storeNameAndCarNumString);
            templateMessageService.pushMiniTemplateMessage(notification.getRoutineId(), temMap, userToken.getToken());
        }
    }

    /**
     * 订单自动收货
     */
    @Override
    public void autoTakeDelivery() {
        int day = 14;
        String autoDay = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ORDER_AUTO_TAKE_DELIVERY_DAY);
        if (StrUtil.isNotBlank(autoDay) && Integer.parseInt(autoDay) >= 1) {
            day = Integer.parseInt(autoDay);
        }
        DateTime nowDate = DateUtil.date();
        DateTime dateTime = DateUtil.offsetDay(nowDate, -day);
        List<Order> orderList = orderService.findAwaitTakeDeliveryOrderList(dateTime.toString());
        if (CollUtil.isEmpty(orderList)) {
            return;
        }
        orderList.forEach(order -> {
            if (order.getType().equals(1)) {// 视频号订单
                // TODO 视频号订单自动收货
            }
        });
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Boolean execute = transactionTemplate.execute(e -> {
            orderNoList.forEach(orderNo -> {
                orderService.takeDelivery(orderNo);
                orderDetailService.takeDelivery(orderNo);
            });
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("自动收货操作数据数失败：订单号:{}", StringUtils.join(orderNoList, ","));
            return;
        }
        //后续操作放入redis
        orderNoList.forEach(orderNo -> {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, orderNo);
        });
    }
}
