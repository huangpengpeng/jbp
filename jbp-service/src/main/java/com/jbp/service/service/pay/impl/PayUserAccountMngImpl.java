package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayUserAccount;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.pay.PayUserAccountDao;
import com.jbp.service.service.pay.PayUserAccountMng;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayUserAccountMngImpl extends ServiceImpl<PayUserAccountDao, PayUserAccount> implements PayUserAccountMng {
    @Override
    public PageInfo<PayUserAccount> page(PageParamRequest pageParamRequest, String accountName, String accountNo, Integer merId) {
        Page<PayUserAccount> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayUserAccount> q = new LambdaQueryWrapper<>();
        q.like(StringUtils.isNotEmpty(accountName), PayUserAccount::getAccountName, accountName)
                .eq(StringUtils.isNotEmpty(accountNo), PayUserAccount::getAccountNo, accountNo)
                .eq(merId != null && merId.intValue() > 0, PayUserAccount::getMerId, merId)
                .orderByAsc(PayUserAccount::getId);
        return CommonPage.copyPageInfo(page, list(q));
    }
}
