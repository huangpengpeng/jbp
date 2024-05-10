package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.TeamUserDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserDao, TeamUser> implements TeamUserService {
    @Resource
    private TeamUserDao teamUserDao;
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @Override
    public TeamUser save(Integer uId, Integer tId) {
        TeamUser teamUser = new TeamUser(uId, tId);
        save(teamUser);
        return teamUser;
    }

    @Override
    public TeamUser getByUser(Integer uId) {
         TeamUser one = getOne(new LambdaQueryWrapper<TeamUser>().eq(TeamUser::getUid, uId));
         if(one != null && one.getTid() != null){
             Team team = teamService.getById(one.getTid());
             if(team != null){
                 one.setName(team.getName());
             }
         }
        return one;
    }

    @Override
    public void deleteByUid(Integer uId) {
        remove(new LambdaQueryWrapper<TeamUser>().eq(TeamUser::getUid, uId));
    }

    @Override
    public PageInfo<TeamUser> pageList(Integer tid, String account, Integer teamLeader, PageParamRequest pageParamRequest) {
        Page<TeamUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<TeamUser> teamUserList = teamUserDao.pageList(tid, account, teamLeader);
        return CommonPage.copyPageInfo(page, teamUserList);
    }

    @Override
    public Map<Integer, TeamUser> getUidMapList(List<Integer> uidList) {
        LambdaQueryWrapper<TeamUser> lqw = new LambdaQueryWrapper<TeamUser>()
                .in(TeamUser::getUid, uidList);
        List<TeamUser> list = list(lqw);
        Map<Integer, TeamUser> teamUserMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return teamUserMap;
        }
        List<Team> teamList = teamService.list();
        Map<Integer, Team> teamMap = FunctionUtil.keyValueMap(teamList, Team::getId);
        list.forEach(e -> {
            Team team = teamMap.get(e.getTid());
            e.setName(team != null ? team.getName() : "");
            teamUserMap.put(e.getUid(), e);
        });
        return teamUserMap;
    }
}
