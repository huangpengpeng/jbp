package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.TeamUserDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserDao, TeamUser> implements TeamUserService {
    @Resource
    TeamService teamService;

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

    @Override
    public PageInfo<TeamUser> pageList(Integer tid, Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<TeamUser> teamUserLambdaQueryWrapper = new LambdaQueryWrapper<TeamUser>();
        teamUserLambdaQueryWrapper.eq(!ObjectUtil.isNull(tid), TeamUser::getTid, tid);
        teamUserLambdaQueryWrapper.eq(!ObjectUtil.isNull(uid), TeamUser::getUid, uid);
        Page<TeamUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<TeamUser> list = list(teamUserLambdaQueryWrapper);
        list.forEach(e->{e.setName(teamService.getById(e.getTid()).getName());});
        return CommonPage.copyPageInfo(page,list);
    }
}
