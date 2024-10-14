package com.jbp.service.service.pay.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.pay.PayCompanyInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.pay.PayCompanyInfoDao;
import com.jbp.service.service.pay.PayCompanyInfoMng;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayCompanyInfoMngImpl extends ServiceImpl<PayCompanyInfoDao, PayCompanyInfo> implements PayCompanyInfoMng {

    @Override
    public PageInfo<PayCompanyInfo> page(PageParamRequest pageParamRequest, String name, String status) {
        Page<PayCompanyInfo> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayCompanyInfo> q = new LambdaQueryWrapper<>();
        q.like(StringUtils.isNotEmpty(name), PayCompanyInfo::getName, name)
                .eq(StringUtils.isNotEmpty(status), PayCompanyInfo::getStatus, status)
                .orderByAsc(PayCompanyInfo::getId);
        return CommonPage.copyPageInfo(page, list(q));
    }

}
