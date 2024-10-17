package com.jbp.service.service.pay.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.*;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.pay.PayUnifiedOrderDao;
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
public class PayUnifiedOrderMngImpl extends UnifiedServiceImpl<PayUnifiedOrderDao, PayUnifiedOrder> implements PayUnifiedOrderMng {

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

    @Override
    public PayCreateResponse create(String token, String method) {
        PayCashier payCashier = payCashierMng.getByToken(token);
        if (payCashier == null || payCashier.getExpireTime().before(DateTimeUtils.getNow())) {
            throw new CrmebException("收银台已过期");
        }
        // 查询订单
        PayUser payUser = payUserMng.getByAppKey(payCashier.getAppKey());
        PayUnifiedOrder order = getByTxnSeqno(payUser.getId(), payCashier.getTxnSeqno());
        if (order != null) {
            if (order.getTxnSeqno().equals("SUCCESS")) {
                throw new CrmebException("订单已成功");
            }
            if (order.getTxnSeqno().equals("FAIL")) {
                throw new CrmebException("订单已失败");
            }
            if (!ArithmeticUtils.equals(order.getPayAmt(), payCashier.getPayAmt())) {
                throw new CrmebException("单号重复");
            }
            // 订单关闭创建新的订单
             removeById(order);
        }
        // 随机支付渠道
        PayUserSubMerchant subMerchant = payUserSubMerchantMng.get(payUser.getId(), method);
        // 保存支付订单
        String channelNotifyUrl = "https://zs.jubaopeng.cc/api/front/publicly/pay/call/" + payUser.getAppKey() + "/" + payCashier.getTxnSeqno();
        String channelReturnUrl = "https://zs.jubaopeng.cc/pages/users/payCode/success?" + "appKey=" + payUser.getAppKey() + "&txnSeqno=" + payCashier.getTxnSeqno();
        order = PayUnifiedOrder.builder().merId(payUser.getMerId()).payUserId(payUser.getId()).userNo(payCashier.getUserNo())
                .channelName(subMerchant.getChannelName()).channelCode(subMerchant.getChannelCode())
                .merchantName(subMerchant.getMerchantName()).merchantNo(subMerchant.getMerchantNo())
                .payUserAccountName(subMerchant.getPayUserAccountName()).payUserAccountNo(subMerchant.getPayUserAccountNo())
                .payMethod(method).txnSeqno(payCashier.getTxnSeqno()).orderInfo(payCashier.getOrderInfo()).ext(payCashier.getExt())
                .payAmt(payCashier.getPayAmt()).refundAmt(BigDecimal.ZERO).status("PROCESSING").createTime(payCashier.getCreateTime())
                .notifyUrl(payCashier.getNotifyUrl()).returnUrl(payCashier.getReturnUrl()).ip(payCashier.getIp())
                .channelNotifyUrl(channelNotifyUrl).channelReturnUrl(channelReturnUrl)
                .build();
        save(order);
        PayChannel payChannel = payChannelMng.getByCode(subMerchant.getChannelCode());
        PaySubMerchant paySubMerchant = paySubMerchantMng.getByMerchantNo(subMerchant.getMerchantNo());
        PayCreateResponse payCreateResponse = payAggregationMng.create(payUser, payChannel, paySubMerchant, order);
        order.setPayChannelSeqno(payCreateResponse.getPlatformTxno());
        updateById(order);
        return payCreateResponse;
    }


    @Override
    public PayUnifiedOrder callBack(String appKey, String txnSeqno) {
        PayUser payUser = payUserMng.getByAppKey(appKey);
        PayUnifiedOrder payUnifiedOrder = getByTxnSeqno(payUser.getId(), txnSeqno);
        if (payUnifiedOrder == null) {
            return null;
        }
        if ("SUCCESS".equals(payUnifiedOrder.getStatus())) {
            return payUnifiedOrder;
        }
        if ("FAIL".equals(payUnifiedOrder.getStatus())) {
            return payUnifiedOrder;
        }
        PayChannel payChannel = payChannelMng.getByCode(payUnifiedOrder.getChannelCode());
        PaySubMerchant paySubMerchant = paySubMerchantMng.getByMerchantNo(payUnifiedOrder.getMerchantNo());
        PayQueryResponse response = payAggregationMng.query(payUser, payChannel, paySubMerchant, payUnifiedOrder);
        if (response == null) {
            return payUnifiedOrder;
        }
        payUnifiedOrder.setStatus(response.getStatus());
        if (StringUtils.isNotEmpty(response.getSuccessTime())) {
            payUnifiedOrder.setPayTime(DateTimeUtils.parseDate(response.getSuccessTime()));
        }
        if (StringUtils.isNotEmpty(response.getPlatformTxno())) {
            payUnifiedOrder.setPayChannelSeqno(response.getPlatformTxno());
        }
        updateById(payUnifiedOrder);
        return payUnifiedOrder;
    }

    @Override
    public PayQueryResponse query(String appKey, String txnSeqno) {
        PayUser payUser = payUserMng.getByAppKey(appKey);
        PayUnifiedOrder payUnifiedOrder = getByTxnSeqno(payUser.getId(), txnSeqno);
        if (payUnifiedOrder == null) {
            return null;
        }
        PayQueryResponse response = new PayQueryResponse(payUser.getAppKey(), payUnifiedOrder.getPayMethod(),
                payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayChannelSeqno(), payUnifiedOrder.getPayAmt().toString(),
                DateTimeUtils.format(payUnifiedOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        if ("SUCCESS".equals(payUnifiedOrder.getStatus())) {
            response.setStatus("SUCCESS");
            response.setSuccessTime(DateTimeUtils.format(payUnifiedOrder.getPayTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            response.setPlatformTxno(payUnifiedOrder.getPayChannelSeqno());
        }

        payUnifiedOrder = callBack(appKey, txnSeqno);
        if (payUnifiedOrder == null) {
            response.setStatus("FAIL");
            response.setPlatformTxno(payUnifiedOrder.getPayChannelSeqno());
        } else {
            response.setStatus(payUnifiedOrder.getStatus());
            if (payUnifiedOrder.getPayAmt() != null) {
                response.setSuccessTime(DateTimeUtils.format(payUnifiedOrder.getPayTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            }
            if (StringUtils.isNotEmpty(payUnifiedOrder.getPayChannelSeqno())) {
                response.setPlatformTxno(payUnifiedOrder.getPayChannelSeqno());
            }
        }
        return response;
    }

    @Override
    public PayUnifiedOrder success(String txnSeqno) {
        return null;
    }

    @Override
    public PayUnifiedOrder getByTxnSeqno(Long payUserId, String txnSeqno) {
        return getOne(new LambdaQueryWrapper<PayUnifiedOrder>().eq(PayUnifiedOrder::getPayUserId, payUserId).eq(PayUnifiedOrder::getTxnSeqno, txnSeqno));
    }

}
