package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserCompany;
import com.jbp.common.request.PageParamRequest;

public interface UserCompanyService extends IService<UserCompany> {
    UserCompany getByCity(String province, String city);

    PageInfo<UserCompany> pageList(Integer uid, String province, String city, String area, PageParamRequest pageParamRequest);
}
