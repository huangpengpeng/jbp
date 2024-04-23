package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserVisaOrderRecordResponse;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.service.dao.UserVisaOrderDao;
import com.jbp.service.service.UserVisaOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserVisaOrderServiceImpl extends ServiceImpl<UserVisaOrderDao, UserVisaOrder> implements UserVisaOrderService {

    @Resource
    private UserVisaOrderDao dao;

    @Override
    public PageInfo<UserVisaOrderRecordResponse> pageList(String account, PageParamRequest pageParamRequest) {


        Page<UserVisaOrderRecordResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserVisaOrderRecordResponse> activateInfoResponses = dao.getAdminPageList(account);

        return CommonPage.copyPageInfo(page, activateInfoResponses);
    }
}

