package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyAddRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyEditRequest;

public interface UserOfflineSubsidyService extends IService<UserOfflineSubsidy> {

    PageInfo<UserOfflineSubsidy> pageList(Integer uid, Integer provinceId, Integer cityId, Integer areaId, PageParamRequest pageParamRequest);

    Boolean add(UserOfflineSubsidyAddRequest request);

    Boolean edit(UserOfflineSubsidyEditRequest request);

    UserOfflineSubsidy getByArea(String province, String city, String area, String status);





}
