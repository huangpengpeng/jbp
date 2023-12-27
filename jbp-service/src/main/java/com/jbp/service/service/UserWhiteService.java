package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserWhiteRequest;

import java.util.List;

public interface UserWhiteService extends IService<WhiteUser> {
    PageInfo<WhiteUser> pageList(UserWhiteRequest request, PageParamRequest pageParamRequest);

    WhiteUser add(UserWhiteRequest userWhiteRequest);

    List<WhiteUser> batchSave(List<UserWhiteExpress> userWhiteExpresses);

}
