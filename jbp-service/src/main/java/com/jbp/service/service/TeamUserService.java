package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.TeamUser;

public interface TeamUserService extends IService<TeamUser> {

    TeamUser save(Integer uId, Integer tId);

    TeamUser getByUser(Integer uId);

    void deleteByUid(Integer uId);
}
