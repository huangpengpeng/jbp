package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserScoreDao extends BaseMapper<UserScore> {


    List<UserScore> getList(@Param("uid") Integer uid, @Param("nickname") String nickname, @Param("phone") String phone);
}
