package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserScoreFlow;
import com.jbp.common.request.PageParamRequest;


public interface UserScoreFlowService extends IService<UserScoreFlow> {


    void add(Integer uid, Integer score, String type, String desc, String remark, Integer surplusScore);

    PageInfo<UserScoreFlow> getList(Integer uid, PageParamRequest pageParamRequest);
}