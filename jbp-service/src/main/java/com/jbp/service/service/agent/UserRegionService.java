package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserRegion;
import com.jbp.common.request.PageParamRequest;


public interface UserRegionService extends IService<UserRegion> {

    UserRegion getByUser(Long uid);

    UserRegion getByArea(String province, String city, String area, String status);

    PageInfo<UserRegion> pageList(Integer uid, String province, String city, String area, PageParamRequest pageParamRequest);
}
