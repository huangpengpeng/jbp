package com.jbp.service.service.pay.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCash;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.pay.PayUnifiedOrderDao;
import com.jbp.service.service.pay.PayCashMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import com.jbp.service.service.pay.PayUserMng;
import com.jbp.service.service.pay.PayUserSubMerchantMng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayUnifiedOrderMngImpl extends UnifiedServiceImpl<PayUnifiedOrderDao, PayUnifiedOrder> implements PayUnifiedOrderMng {

    @Resource
    private PayUserMng payUserMng;
    @Resource
    private PayCashMng payCashMng;
    @Resource
    private PayUserSubMerchantMng payUserSubMerchantMng;

    @Override
    public PayUnifiedOrder create(String token, String method) {
        PayCash payCash = payCashMng.getByToken(token);
        if (payCash == null || payCash.getExpireTime().before(DateTimeUtils.getNow())) {
            throw new CrmebException("收银台已过期");
        }
        // 查询订单
        PayUser payUser = payUserMng.getByAppKey(payCash.getAppKey());
        PayUnifiedOrder order = getByTxnSeqno(payUser.getId(), payCash.getTxnSeqno());
        if (order != null) {
            if (order.getTxnSeqno().equals("SUCCESS")) {
                throw new CrmebException("订单已成功");
            }
            if (order.getTxnSeqno().equals("FAIL")) {
                throw new CrmebException("订单已失败");
            }
            if (ArithmeticUtils.equals(order.getPayAmt(), payCash.getPayAmt())) {
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
                .payMethod(method).txnSeqno(payCash.getTxnSeqno()).orderInfo(payCash.getOrderInfo()).ext(payCash.getExt())
                .payAmt(payCash.getPayAmt()).refundAmt(BigDecimal.ZERO).status("PENDING").createTime(payCash.getCreateTime())
                .notifyUrl(payCash.getNotifyUrl()).returnUrl(payCash.getReturnUrl())
                .build();
        save(order);
        // todo 调用三方支付

        return order;
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
