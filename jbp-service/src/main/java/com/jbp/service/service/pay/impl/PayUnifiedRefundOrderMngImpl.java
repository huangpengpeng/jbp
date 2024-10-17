package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.*;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.request.pay.PayRefundQueryRequest;
import com.jbp.common.request.pay.PayRefundRequest;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.pay.PayUnifiedRefundOrderDao;
import com.jbp.service.service.pay.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayUnifiedRefundOrderMngImpl extends UnifiedServiceImpl<PayUnifiedRefundOrderDao, PayUnifiedRefundOrder> implements PayUnifiedRefundOrderMng {

    @Resource
    private PayUserMng payUserMng;
    @Resource
    private PayCashierMng payCashierMng;
    @Resource
    private PaySubMerchantMng paySubMerchantMng;
    @Resource
    private PayUserSubMerchantMng payUserSubMerchantMng;
    @Resource
    private PayAggregationMng payAggregationMng;
    @Resource
    private PayChannelMng payChannelMng;
   @Resource
   private PayUnifiedOrderMng payUnifiedOrderMng;
    @Override
    public PayRefundResponse refund(PayRefundRequest request) {
        PayUser payUser = payUserMng.getByAppKey(request.getAppKey());
        PayUnifiedOrder payUnifiedOrder = payUnifiedOrderMng.getByTxnSeqno(payUser.getId(), request.getTxnSeqno());
        if (payUnifiedOrder == null || !payUnifiedOrder.getStatus().equals("SUCCESS")) {
            throw new CrmebException("订单未支付成功");
        }
        PayChannel payChannel = payChannelMng.getByCode(payUnifiedOrder.getChannelCode());
        PaySubMerchant subMerchant = paySubMerchantMng.getByMerchantNo(payUnifiedOrder.getMerchantNo());

        BigDecimal subtract = payUnifiedOrder.getPayAmt().subtract(payUnifiedOrder.getRefundAmt()).subtract(request.getRefundAmt());
        if (ArithmeticUtils.less(subtract, BigDecimal.ZERO)) {
            throw new CrmebException("可退金额不足");
        }
        PayUnifiedRefundOrder refundOrder = PayUnifiedRefundOrder.builder()
                .merId(payUnifiedOrder.getMerId())
                .payUserId(payUnifiedOrder.getPayUserId())
                .userNo(payUnifiedOrder.getUserNo())
                .channelName(payUnifiedOrder.getChannelName())
                .channelCode(payUnifiedOrder.getChannelCode())
                .merchantName(payUnifiedOrder.getMerchantName())
                .merchantNo(payUnifiedOrder.getMerchantNo())
                .payUserAccountName(payUnifiedOrder.getPayUserAccountName())
                .payUserAccountNo(payUnifiedOrder.getPayUserAccountNo())
                .payMethod(payUnifiedOrder.getPayMethod())
                .payRefundNo(request.getRefundNo())
                .txnSeqno(payUnifiedOrder.getTxnSeqno())
                .refundAmt(request.getRefundAmt())
                .createTime(DateTimeUtils.getNow())
                .build();

        PayRefundResponse result = payAggregationMng.refund(payChannel, payUser, subMerchant, payUnifiedOrder, refundOrder);
        if (result != null) {
            refundOrder.setStatus(result.getStatus());
            if (StringUtils.isNotEmpty(result.getSuccessTime())) {
                refundOrder.setRefundTime(DateTimeUtils.parseDate(result.getSuccessTime()));
            }
            refundOrder.setPayChannelSeqno(result.getPlatformRefundTxno());
            saveOrUpdate(refundOrder);
        }
        payUnifiedOrder.setRefundAmt(payUnifiedOrder.getRefundAmt().add(request.getRefundAmt()));
        payUnifiedOrderMng.updateById(payUnifiedOrder);
        return result;
    }

    @Override
    public PayRefundResponse refundQuery(PayRefundQueryRequest request) {

        PayUser payUser = payUserMng.getByAppKey(request.getAppKey());
        PayUnifiedOrder payUnifiedOrder = payUnifiedOrderMng.getByTxnSeqno(payUser.getId(), request.getTxnSeqno());
        if (payUnifiedOrder == null || !payUnifiedOrder.getStatus().equals("SUCCESS")) {
            throw new CrmebException("订单未支付成功");
        }
        PayChannel payChannel = payChannelMng.getByCode(payUnifiedOrder.getChannelCode());
        PaySubMerchant subMerchant = paySubMerchantMng.getByMerchantNo(payUnifiedOrder.getMerchantNo());

        PayUnifiedRefundOrder refundOrder = getOne(new LambdaQueryWrapper<PayUnifiedRefundOrder>().eq(PayUnifiedRefundOrder::getPayUserId, payUser.getId()).eq(PayUnifiedRefundOrder::getPayRefundNo, request.getRefundNo()));
        if (refundOrder == null) {
            throw new CrmebException("退款订单未支付成功");
        }
        PayRefundResponse response = new PayRefundResponse(payUser.getAppKey(), payUnifiedOrder.getTxnSeqno(), refundOrder.getPayRefundNo(),
                refundOrder.getRefundAmt().toString(), DateTimeUtils.format(refundOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        if (refundOrder.getStatus().equals("SUCCESS") || refundOrder.getStatus().equals("FAIL")) {
            response.setStatus(refundOrder.getStatus());
        } else {
            response = payAggregationMng.refundQuery(payChannel, payUser, subMerchant,
                    payUnifiedOrder, refundOrder);
        }
        return response;
    }

    @Override
    public PayUnifiedRefundOrder refresh(PayUnifiedRefundOrder order) {
        if (order.getStatus().equals("SUCCESS") || order.getStatus().equals("FAIL")) {
            return order;
        }
        PayUser payUser = payUserMng.getById(order.getPayUserId());
        PayChannel payChannel = payChannelMng.getByCode(order.getChannelCode());
        PaySubMerchant subMerchant = paySubMerchantMng.getByMerchantNo(order.getMerchantNo());
        PayUnifiedOrder payUnifiedOrder = payUnifiedOrderMng.getByTxnSeqno(payUser.getId(), order.getTxnSeqno());
        PayRefundResponse response = payAggregationMng.refundQuery(payChannel, payUser, subMerchant,
                payUnifiedOrder, order);
        order.setStatus(response.getStatus());
        order.setPayChannelSeqno(response.getPlatformRefundTxno());
        if (StringUtils.isNotEmpty(response.getSuccessTime())) {
            order.setRefundTime(DateTimeUtils.parseDate(response.getSuccessTime()));
        }
        updateById(order);
        return order;
    }
}
