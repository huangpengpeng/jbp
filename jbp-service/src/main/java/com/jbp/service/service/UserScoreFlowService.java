package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserScoreFlow;


public interface UserScoreFlowService extends IService<UserScoreFlow> {



  void  add(Integer uid,Integer score,String type, String desc,String remark);

}