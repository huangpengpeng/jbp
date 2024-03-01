package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.request.PageParamRequest;

public interface LztAcctService extends IService<LztAcct> {

    LztAcct getByUserId(String userId);

    LztAcct create(Integer merId, String userId, String userType, String userNo, String username, String bankAccount);

    LztAcct details(String userId);

    PageInfo<LztAcct> pageList(Integer merId, String userId, String username,  PageParamRequest pageParamRequest);

}
