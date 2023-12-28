package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.service.dao.agent.TeamUserDao;
import com.jbp.service.service.TeamUserService;
import org.springframework.stereotype.Service;

@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserDao, TeamUser> implements TeamUserService {

    @Override
    public TeamUser save(Integer uId, Integer tId) {
        TeamUser teamUser = new TeamUser(uId, tId);
        save(teamUser);
        return teamUser;
    }

    @Override
    public TeamUser getByUser(Integer uId) {
        return getOne(new LambdaQueryWrapper<TeamUser>().eq(TeamUser::getUid, uId));
    }

    @Override
    public void deleteByUid(Integer uId) {
         remove(new LambdaQueryWrapper<TeamUser>().eq(TeamUser::getUid, uId));
    }
}
