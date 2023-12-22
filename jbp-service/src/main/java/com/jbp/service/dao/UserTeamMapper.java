package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jbp.common.model.user.UserTeam;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserTeamMapper extends BaseMapper<UserTeam> {
    Page<UserTeam> adminPage(Page<UserTeam> page, @Param("map") Map map);

}
