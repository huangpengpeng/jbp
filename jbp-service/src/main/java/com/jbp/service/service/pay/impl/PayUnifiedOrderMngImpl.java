package com.jbp.service.service.pay.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCashier;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
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
    private PayUserSubMerchantMng payUserSubMerchantMng;
    @Resource
    private PayAggregationMng payAggregationMng;

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
            if (ArithmeticUtils.equals(order.getPayAmt(), payCashier.getPayAmt())) {
                throw new CrmebException("单号重复");
            }
            // 订单关闭创建新的订单
            order.setStatus("FAIL");
            updateById(order);
        }
        // 随机支付渠道
        PayUserSubMerchant subMerchant = payUserSubMerchantMng.get(payUser.getId(), method);
        // 保存支付订单
        order = PayUnifiedOrder.builder().merId(payUser.getMerId()).payUserId(payUser.getId())
                .channelName(subMerchant.getChannelName()).channelCode(subMerchant.getChannelCode())
                .merchantName(subMerchant.getMerchantName()).merchantNo(subMerchant.getMerchantNo())
                .payUserAccountName(subMerchant.getPayUserAccountName()).payUserAccountNo(subMerchant.getPayUserAccountNo())
                .payMethod(method).txnSeqno(payCashier.getTxnSeqno()).orderInfo(payCashier.getOrderInfo()).ext(payCashier.getExt())
                .payAmt(payCashier.getPayAmt()).refundAmt(BigDecimal.ZERO).status("PROCESSING").createTime(payCashier.getCreateTime())
                .notifyUrl(payCashier.getNotifyUrl()).returnUrl(payCashier.getReturnUrl())
                .build();
        save(order);
        // todo 调用三方支付
        PayCreateResponse payCreateResponse = payAggregationMng.create();
        return payCreateResponse;
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
