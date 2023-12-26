package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.UserWhite;
import com.jbp.common.model.user.White;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserWhiteRequest;
import com.jbp.common.result.CommonResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserWhiteService extends IService<UserWhite> {
    PageInfo<UserWhite> pageList(UserWhiteRequest request,PageParamRequest pageParamRequest);

    UserWhite add(UserWhiteRequest userWhiteRequest);

    Boolean batch(List<Long> id);


    List<UserWhite> importUserWhite(List<UserWhiteExpress> userWhiteExpresses);

}
