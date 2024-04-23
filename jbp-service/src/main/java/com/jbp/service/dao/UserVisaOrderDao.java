package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserVisaOrder;
import com.jbp.common.response.UserVisaOrderRecordResponse;
import com.jbp.common.response.UserVisaRecordResponse;

import java.util.List;


public interface UserVisaOrderDao extends BaseMapper<UserVisaOrder> {

    List<UserVisaOrderRecordResponse> getAdminPageList(String account);

}
