package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserTeam;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserTeamService extends IService<UserTeam> {

    UserTeam getByUser(Long userId);


    UserTeam getByName(String name);

    Page<UserTeam> adminPage(Page<UserTeam> page,@Param("map") Map map);

    void refresh(Long userId);

    void add(Long userId, String teamName);

    void del(String teamName);


}
