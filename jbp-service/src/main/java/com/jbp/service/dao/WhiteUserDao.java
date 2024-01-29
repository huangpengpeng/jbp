package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.WhiteUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WhiteUserDao extends BaseMapper<WhiteUser> {

}
