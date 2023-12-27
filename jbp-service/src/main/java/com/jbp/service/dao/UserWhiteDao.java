package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserWhite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserWhiteDao extends BaseMapper<UserWhite> {
    List<UserWhite> selectUserWhiteList(@Param("ew") LambdaQueryWrapper<UserWhite> lambdaQueryWrapper);

}
