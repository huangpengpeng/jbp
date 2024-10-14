package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.UserJd;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserJdResponse;


public interface UserJdService extends IService<UserJd> {


    PageInfo<UserJdResponse> getUserJdList(String account, String nickName, PageParamRequest pageRequest);

}