package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.Team;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface TeamService extends IService<Team> {

    Team getLastTeam(Integer uid, List<UserUpperDto> allUpper);

    Team getByLeader(Integer leaderId);

    Team getByName(String name);

    void delete(Integer id);

    void editName(Integer id, String name);

    void save(Integer leaderId, String name);

     PageInfo<Team> pageList(String name, PageParamRequest pageParamRequest);
}
