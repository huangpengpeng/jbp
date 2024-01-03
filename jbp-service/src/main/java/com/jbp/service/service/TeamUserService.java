package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.request.PageParamRequest;

public interface TeamUserService extends IService<TeamUser> {

    TeamUser save(Integer uId, Integer tId);

    TeamUser getByUser(Integer uId);

    void deleteByUid(Integer uId);

     PageInfo<TeamUser> pageList(Integer tid, Integer uid, PageParamRequest pageParamRequest);
}
