package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.common.response.UserVisaResponse;


public interface UserVisaService extends IService<UserVisa> {



    void updateVisa(Integer id,String platform);

    UserVisaResponse getVisaTask(String signTaskId);

    PageInfo<UserVisaRecordResponse> pageList(String account, PageParamRequest pageParamRequest);

}