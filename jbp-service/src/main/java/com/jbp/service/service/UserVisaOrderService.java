package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserVisaOrderRecordResponse;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.common.response.UserVisaResponse;


public interface UserVisaOrderService extends IService<UserVisaOrder> {

    PageInfo<UserVisaOrderRecordResponse> pageList(String account, PageParamRequest pageParamRequest);

}