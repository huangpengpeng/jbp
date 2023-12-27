package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;

import java.util.List;

public interface WhiteUserService extends IService<WhiteUser> {
    PageInfo<WhiteUser> pageList(WhiteUserRequest request, PageParamRequest pageParamRequest);

    Boolean add(WhiteUserRequest userWhiteRequest);

    Boolean batchSave(List<UserWhiteExpress> userWhiteExpresses);

}
