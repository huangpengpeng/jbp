package com.jbp.service.service.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayCashier;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.request.pay.PayCashRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.pay.PayCashierDao;
import com.jbp.service.service.pay.PayCashierMng;
import com.jbp.service.service.pay.PayUnifiedOrderMng;
import com.jbp.service.service.pay.PayUserMng;
import com.jbp.service.service.pay.PayUserSubMerchantMng;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayCashierMngImpl extends ServiceImpl<PayCashierDao, PayCashier> implements PayCashierMng {

    @Resource
    private PayUnifiedOrderMng payUnifiedOrderMng;
    @Resource
    private PayUserMng payUserMng;
    @Resource
    private PayUserSubMerchantMng payUserSubMerchantMng;

    @Override
    public PayCashier save(PayCashRequest request) {
        // 检查订单是否存在
        PayCashier payCashier = getByTxnSeqno(request.getAppKey(), request.getTxnSeqno());
        if (payCashier != null) {
            if (!ArithmeticUtils.equals(payCashier.getPayAmt(), request.getPayAmt())) {
                throw new CrmebException("单号重复1");
            }
            // 检查订单是否成功 或者关闭
            PayUser payUser = payUserMng.getByAppKey(payCashier.getAppKey());
            PayUnifiedOrder order = payUnifiedOrderMng.getByTxnSeqno(payUser.getId(), request.getTxnSeqno());
            if (order != null) {
                if (order.getTxnSeqno().equals("SUCCESS")) {
                    throw new CrmebException("订单已成功");
                }
                if (order.getTxnSeqno().equals("FAIL")) {
                    throw new CrmebException("订单已失败");
                }
                if (ArithmeticUtils.equals(order.getPayAmt(), request.getPayAmt())) {
                    throw new CrmebException("单号重复2");
                }
            }
            payCashier.setOrderInfo(request.getOrderInfo());
            payCashier.setExt(request.getExt());
            payCashier.setCreateTime(DateTimeUtils.parseDate(request.getCreateTime()));
            payCashier.setExpireTime(DateTimeUtils.parseDate(request.getExpireTime()));
            payCashier.setUserNo(request.getUserNo());
            payCashier.setNotifyUrl(request.getNotifyUrl());
            payCashier.setReturnUrl(request.getReturnUrl());
            payCashier.setIp(request.getIp());
            updateById(payCashier);
            return payCashier;
        }
        String uuid = CrmebUtil.getUuid();
        payCashier = PayCashier.builder().token(uuid).appKey(request.getAppKey()).userNo(request.getUserNo()).txnSeqno(request.getTxnSeqno())
                .payAmt(request.getPayAmt()).orderInfo(request.getOrderInfo()).ext(request.getExt())
                .createTime(DateTimeUtils.parseDate(request.getCreateTime()))
                .expireTime(DateTimeUtils.parseDate(request.getExpireTime()))
                .notifyUrl(request.getNotifyUrl()).returnUrl(request.getReturnUrl())
                .ip(request.getIp())
                .build();
        save(payCashier);
        return payCashier;
    }

    @Override
    public PayCashier getByTxnSeqno(String appKey, String txnSeqno) {
        LambdaQueryWrapper<PayCashier> l = new LambdaQueryWrapper<>();
        l.eq(PayCashier::getAppKey, appKey).eq(PayCashier::getTxnSeqno, txnSeqno).last(" limit 1");
        PayCashier one = getOne(l);
        return one;
    }

    @Override
    public PayCashier getByToken(String token) {
        LambdaQueryWrapper<PayCashier> l = new LambdaQueryWrapper<>();
        l.eq(PayCashier::getToken, token);
        PayCashier one = getOne(l);
        return one;
    }

    @Override
    public JSONObject getPayMethod(String token) {
        PayCashier payCashier = getByToken(token);
        PayUser payUser = payUserMng.getByAppKey(payCashier.getAppKey());
        List<PayUserSubMerchant> list = payUserSubMerchantMng.getByPayUser(payUser.getId());
        List<String> methodList = Lists.newArrayList();
        List<PayUserSubMerchant> wechatList = FunctionUtil.filter(list, s -> "正常".equals(s.getWechatStatus()));
        if (CollectionUtils.isNotEmpty(wechatList)) {
            methodList.add("wechatPay");
        }
        List<PayUserSubMerchant> aliPayList = FunctionUtil.filter(list, s -> "正常".equals(s.getAliStatus()));
        if (CollectionUtils.isNotEmpty(aliPayList)) {
            methodList.add("aliPay");
        }

        List<PayUserSubMerchant> qucikPayList = FunctionUtil.filter(list, s -> "正常".equals(s.getQuickStatus()));
        if (CollectionUtils.isNotEmpty(qucikPayList)) {
            methodList.add("quickPay");
        }

        JSONObject jsonObject =new JSONObject();
        jsonObject.put("methodList",methodList);
        //金额
        jsonObject.put("payAmt",payCashier.getPayAmt());
        return jsonObject;
    }
}
