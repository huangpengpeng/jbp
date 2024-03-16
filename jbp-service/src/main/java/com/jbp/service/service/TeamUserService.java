package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.request.PageParamRequest;

import java.util.List;
import java.util.Map;

public interface TeamUserService extends IService<TeamUser> {

    TeamUser save(Integer uId, Integer tId);

    TeamUser getByUser(Integer uId);

    void deleteByUid(Integer uId);

     PageInfo<TeamUser> pageList(Integer tid, String account,Integer teamLeader, PageParamRequest pageParamRequest);

    Map<Integer, TeamUser> getUidMapList(List<Integer> uidList);
}
