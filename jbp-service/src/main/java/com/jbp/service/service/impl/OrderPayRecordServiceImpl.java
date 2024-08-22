package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.lianlian.result.PaymentGwResult;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.RefundQueryResult;
import com.jbp.common.lianlian.result.RefundResult;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.common.model.order.OrderPayRecord;
import com.jbp.common.model.order.OrderPayRefundRecord;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.OrderPayRecordDao;
import com.jbp.service.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderPayRecordServiceImpl extends ServiceImpl<OrderPayRecordDao, OrderPayRecord> implements OrderPayRecordService {

    @Resource
    private OrderPayChannelService payChannelService;
    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private OrderPayRefundRecordService orderPayRefundRecordService;
    @Resource
    private YopService yopService;

    @Override
    public OrderPayRecord getByOrderNo(String orderNo) {
        return getOne(new LambdaQueryWrapper<OrderPayRecord>().eq(OrderPayRecord::getOrderNo, orderNo).last("limit 1"));
    }

    @Override
    public OrderPayRecord scanPay(String payeeName, Integer merId, String payMethod, BigDecimal payPrice, String remark, String ip) {
        // 业务单号
        String orderNo = StringUtils.N_TO_10("SCAN_");
        //  支付单号
        String payNo = StringUtils.N_TO_10("PAY_");
        OrderPayChannel payChannel = payChannelService.getServer(merId, payMethod);
        String notify_url = payChannel.getNotifyUrl() + "/" + orderNo;
        String return_url = payChannel.getReturnUrl() + "/" + orderNo;

        OrderPayRecord orderPayRecord = new OrderPayRecord(payChannel.getId(), merId, payeeName,
                OrderPayRecord.StatusEnum.收款码.toString(), orderNo, payNo,
                payPrice, payPrice.multiply(payChannel.getFeeScale()));

        if (payChannel.getPayChannel().equals("连连")) {
            if (payChannel.getPayMethod().equals("微信")) {
                PaymentGwResult paymentGwResult = lianLianPayService.wechatScanPay(payChannel, payNo, payPrice,
                        payPrice.multiply(payChannel.getFeeScale()), notify_url, return_url, remark, ip);
                orderPayRecord.setReceiptNo(paymentGwResult.getAccp_txno());
                orderPayRecord.setOrderResultInfo(JSONObject.toJSONString(paymentGwResult));

            }
            if (payChannel.getPayMethod().equals("支付宝")) {
                PaymentGwResult paymentGwResult = lianLianPayService.aliScanPay(payChannel, payNo, payPrice,
                        payPrice.multiply(payChannel.getFeeScale()), notify_url, return_url, remark, ip);
                orderPayRecord.setReceiptNo(paymentGwResult.getAccp_txno());
                orderPayRecord.setOrderResultInfo(JSONObject.toJSONString(paymentGwResult));
            }
        }
        save(orderPayRecord);
        return orderPayRecord;
    }

    @Override
    public void callBack(String orderNo) {
        List<OrderPayRecord> list = getWaitPayByOrderNo(orderNo);
        for (OrderPayRecord record : list) {
            OrderPayChannel payChannel = payChannelService.getById(record.getOrderPayChannelId());
            if (payChannel.getPayChannel().equals("连连")) {
                QueryPaymentResult result = lianLianPayService.queryPayResult(record.getPayNo());
                if (result != null) {
                    record.setQueryResultInfo(JSONObject.toJSONString(result));
                    if ("TRADE_SUCCESS".equals(result.getTxn_status())) {
                        record.setStatus("已付款");
                        record.setPayTime(DateTimeUtils.format(DateTimeUtils.parseDate(result.getFinish_time()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                        record.setUnionPayNo(result.getChnl_txno());

                    }
                    if ("TRADE_CLOSE".equals(result.getTxn_status())) {
                        record.setStatus("交易失败");
                    }
                    record.setQueryResultInfo(JSONObject.toJSONString(result));
                }
            }
        }
    }

    @Override
    public List<OrderPayRecord> getWaitPayByOrderNo(String orderNo) {
        return list(new QueryWrapper<OrderPayRecord>().lambda().eq(OrderPayRecord::getOrderNo, orderNo));
    }

    @Override
    public OrderPayRecord getByPayNo(String payNo) {
        return getOne(new QueryWrapper<OrderPayRecord>().lambda().eq(OrderPayRecord::getPayNo, payNo));
    }

    @Override
    public void refund(String payNo, BigDecimal refundAmt, String remark) {
        OrderPayRecord record = getByPayNo(payNo);
        if (record == null) {
            throw new RuntimeException("支付订单不存在");
        }
        if (!record.getStatus().equals("已付款")) {
            throw new RuntimeException("支付订单未支付成功不允许退款");
        }
        BigDecimal totalRefundAmt = refundAmt.add(record.getRefundPrice());
        if (ArithmeticUtils.gt(totalRefundAmt, record.getPayPrice())) {
            BigDecimal subtract = record.getPayPrice().subtract(record.getRefundPrice());
            throw new RuntimeException("退款总金额超出支付金额，可退金额:" + subtract);
        }
        String payRefundNo = StringUtils.N_TO_10("REFUND_");
        OrderPayRefundRecord refundRecord = new OrderPayRefundRecord(record.getOrderPayChannelId(), record.getId(),
                payRefundNo, refundAmt, remark);
        orderPayRefundRecordService.save(refundRecord);

        OrderPayChannel payChannel = payChannelService.getById(record.getOrderPayChannelId());
        String notify_url = payChannel.getNotifyUrl() + "/" + payRefundNo;
        if (payChannel.getPayChannel().equals("连连")) {
            String refundTime = DateTimeUtils.format(refundRecord.getRefundTime(),
                    DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
            RefundResult refund = lianLianPayService.refund(payChannel, payRefundNo, refundAmt.toString(), payNo, notify_url, refundTime);
            refundRecord.setOrderResultInfo(JSONObject.toJSONString(refund));
        }
        orderPayRefundRecordService.updateById(refundRecord);
        record.setRefundPrice(record.getRefundPrice().add(refundAmt));
        updateById(record);
    }

    @Override
    public void refundCallBack(String refundNo) {
        OrderPayRefundRecord refundRecord = orderPayRefundRecordService.getOne(new QueryWrapper<OrderPayRefundRecord>().lambda().eq(OrderPayRefundRecord::getPayRefundNo, refundNo));
        if (refundRecord == null) {
            return;
        }
        OrderPayChannel payChannel = payChannelService.getById(refundRecord.getOrderPayChannelId());
        OrderPayRecord record = getById(refundRecord.getOrderPayRecordId());
        if (refundRecord.getStatus().equals("退款中")) {
            String refundTime = DateTimeUtils.format(refundRecord.getRefundTime(),
                    DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
            RefundQueryResult refundQueryResult = lianLianPayService.refundQuery(payChannel, refundNo, refundTime);
            if (refundQueryResult != null) {
                refundRecord.setQueryResultInfo(JSONObject.toJSONString(refundQueryResult));
                if ("2".equals(refundQueryResult.getSta_refund())) {
                    refundRecord.setStatus("已退款");
                }
                if ("3".equals(refundQueryResult.getSta_refund())) {
                    refundRecord.setStatus("退款失败");
                    record.setRefundPrice(record.getRefundPrice().subtract(refundRecord.getRefundPrice()));
                    updateById(record);
                }
            }
        }
        orderPayRefundRecordService.updateById(refundRecord);
    }
}
