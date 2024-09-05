package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserScoreRequest;
import com.jbp.common.request.agent.UserScoreEditRequest;


public interface UserScoreService extends IService<UserScore> {


    void increase(Integer uid, Integer score, String desc, String remark);

    void reduce(Integer uid, Integer score, String desc, String remark);


    void updateUserCapa(UserScoreRequest request);

    PageInfo<UserScore> getList(Integer uid, String nickname, String phone, PageParamRequest pageParamRequest);

    Boolean edit(UserScoreEditRequest request);

    String export(Integer uid, String nickname, String phone);
}