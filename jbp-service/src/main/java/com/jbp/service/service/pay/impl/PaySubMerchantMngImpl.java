package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.pay.PaySubMerchantDao;
import com.jbp.service.service.pay.PaySubMerchantMng;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PaySubMerchantMngImpl extends ServiceImpl<PaySubMerchantDao, PaySubMerchant> implements PaySubMerchantMng {
    @Override
    public PageInfo<PaySubMerchant> page(PageParamRequest pageParamRequest, String merchantName, String merchantNo) {
        Page<PaySubMerchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PaySubMerchant> q = new LambdaQueryWrapper<>();
        q.like(StringUtils.isNotEmpty(merchantName), PaySubMerchant::getMerchantName, merchantName)
                .eq(StringUtils.isNotEmpty(merchantNo), PaySubMerchant::getMerchantNo, merchantNo)
                .orderByAsc(PaySubMerchant::getId);
        return CommonPage.copyPageInfo(page, list(q));
    }
}
