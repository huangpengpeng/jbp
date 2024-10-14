package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.pay.PayUserSubMerchantDao;
import com.jbp.service.service.pay.PayUserSubMerchantMng;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayUserSubMerchantMngImpl extends ServiceImpl<PayUserSubMerchantDao, PayUserSubMerchant> implements PayUserSubMerchantMng {
    @Override
    public PageInfo<PayUserSubMerchant> page(PageParamRequest pageParamRequest, String payUserAccountName, String payUserAccountNo) {
        Page<PayUserSubMerchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayUserSubMerchant> q = new LambdaQueryWrapper<>();
        q.like(StringUtils.isNotEmpty(payUserAccountName), PayUserSubMerchant::getPayUserAccountName, payUserAccountName)
                .eq(StringUtils.isNotEmpty(payUserAccountNo), PayUserSubMerchant::getPayUserAccountNo, payUserAccountNo)
                .orderByAsc(PayUserSubMerchant::getId);
        return CommonPage.copyPageInfo(page, list(q));
    }

    @Override
    public PayUserSubMerchant get(Long payUserId, String method) {
        List<PayUserSubMerchant> list = getByPayUser(payUserId);
        if (CollectionUtils.isEmpty(list)) {
            throw new CrmebException("该商户没有可用的收款渠道");
        }
        if ("wechatPay".equals(method)) {
            list = FunctionUtil.filter(list, s -> "正常".equals(s.getWechatStatus()));
            if (CollectionUtils.isEmpty(list)) {
                throw new CrmebException("该商户没有可用的微信收款渠道");
            }
        }
        if ("aliPay".equals(method)) {
            list = FunctionUtil.filter(list, s -> "正常".equals(s.getAliStatus()));
            if (CollectionUtils.isEmpty(list)) {
                throw new CrmebException("该商户没有可用的支付宝收款渠道");
            }
        }
        if ("quickPay".equals(method)) {
            list = FunctionUtil.filter(list, s -> "正常".equals(s.getQuickStatus()));
            if (CollectionUtils.isEmpty(list)) {
                throw new CrmebException("该商户没有可用的银行卡收款渠道");
            }
        }
        // 打乱顺序
        Collections.shuffle(list);
        return list.get(0);
    }

    @Override
    public List<PayUserSubMerchant> getByPayUser(Long payUserId) {
        LambdaQueryWrapper<PayUserSubMerchant> l = new LambdaQueryWrapper<>();
        l.eq(PayUserSubMerchant::getPayUserId, payUserId);
        return list(l);
    }
}
