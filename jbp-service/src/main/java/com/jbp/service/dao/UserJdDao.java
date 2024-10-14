package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.user.UserJd;
import com.jbp.common.response.UserJdResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface UserJdDao extends BaseMapper<UserJd> {

    List<UserJdResponse> getList(@Param(value = "account") String account, @Param(value = "nickname") String nickname);


}
