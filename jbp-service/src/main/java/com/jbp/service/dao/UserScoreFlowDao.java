package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserScoreFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserScoreFlowDao extends BaseMapper<UserScoreFlow> {


    List<UserScoreFlow> getList(@Param("uid") Integer uid);

}
