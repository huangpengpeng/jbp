package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCash;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.dao.pay.PayCashDao;
import com.jbp.service.service.pay.PayCashMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import com.jbp.service.service.pay.PayUserMng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayCashMngImpl extends ServiceImpl<PayCashDao, PayCash> implements PayCashMng {

    @Resource
    private PayUnifiedOrderMng payUnifiedOrderMng;
    @Resource
    private PayUserMng payUserMng;

    @Override
    public PayCash save(String appKey, String txnSeqno, BigDecimal payAmt, String orderInfo, String ext, Date createTime, Date expireTime) {
        // 检查订单是否存在
        PayCash payCash = getByTxnSeqno(appKey, txnSeqno);
        if (payCash != null) {
            if (ArithmeticUtils.equals(payCash.getPayAmt(), payAmt)) {
                throw new CrmebException("单号重复1");
            }
            // 检查订单是否成功 或者关闭
            PayUser payUser = payUserMng.getByAppKey(payCash.getAppKey());
            PayUnifiedOrder order = payUnifiedOrderMng.getByTxnSeqno(payUser.getId(), txnSeqno);
            if (order != null) {
                if (order.getTxnSeqno().equals("SUCCESS")) {
                    throw new CrmebException("订单已成功");
                }
                if (order.getTxnSeqno().equals("FAIL")) {
                    throw new CrmebException("订单已失败");
                }
                if (ArithmeticUtils.equals(order.getPayAmt(), payAmt)) {
                    throw new CrmebException("单号重复2");
                }
            }
            payCash.setOrderInfo(orderInfo);
            payCash.setExt(ext);
            payCash.setCreateTime(createTime);
            payCash.setExpireTime(expireTime);
            updateById(payCash);
            return payCash;
        }
        String uuid = CrmebUtil.getUuid();
        payCash = PayCash.builder().token(uuid).appKey(appKey).txnSeqno(txnSeqno).payAmt(payAmt).orderInfo(orderInfo).ext(ext).createTime(createTime).expireTime(expireTime).build();
        save(payCash);
        return payCash;
    }

    @Override
    public PayCash getByTxnSeqno(String appKey, String txnSeqno) {
        LambdaQueryWrapper<PayCash> l = new LambdaQueryWrapper<>();
        l.eq(PayCash::getAppKey, appKey).eq(PayCash::getTxnSeqno, txnSeqno).last(" limit 1");
        PayCash one = getOne(l);
        return one;
    }

    @Override
    public PayCash getByToken(String token) {
        LambdaQueryWrapper<PayCash> l = new LambdaQueryWrapper<>();
        l.eq(PayCash::getToken, token);
        PayCash one = getOne(l);
        return one;
    }
}
