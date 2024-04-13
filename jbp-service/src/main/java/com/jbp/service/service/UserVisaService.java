package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.response.UserVisaResponse;


public interface UserVisaService extends IService<UserVisa> {



    void updateVisa(Integer id,String platform);

    UserVisaResponse getVisaTask(String signTaskId);


}