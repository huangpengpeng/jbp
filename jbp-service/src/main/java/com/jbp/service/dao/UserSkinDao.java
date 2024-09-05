package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserSkin;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserSkinDao extends BaseMapper<UserSkin> {
    List<UserSkin> getList(@Param("uid") Integer uid, @Param("nickname") String nickname, @Param("phone") String phone, @Param("startCreateTime") Date startCreateTime, @Param("endCreateTime") Date endCreateTime);
}
