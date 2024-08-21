package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.request.UserScoreRequest;


public interface UserScoreService extends IService<UserScore> {


    void increase(Integer uid, Integer score,String desc);

    void reduce(Integer uid, Integer score,String desc);


    void updateUserCapa(UserScoreRequest request);

}