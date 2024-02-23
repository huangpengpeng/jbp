package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Team;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.TeamDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class TeamServiceImpl extends ServiceImpl<TeamDao, Team> implements TeamService {

    @Resource
    TeamUserService teamUserService;
    @Resource
    private UserInvitationFlowService userInvitationFlowService;

    /**
     * 获取最近的一个团队
     */
    @Override
    public Team getLastTeam(Integer uid, List<UserUpperDto> allUpper) {
        // 自己是最近的那一个团队
        Team team = getByLeader(uid);
        if (team != null) {
            return team;
        }
        for (UserUpperDto userUpperDto : allUpper) {
            team = getByLeader(userUpperDto.getPId());
            if (team != null) {
                return team;
            }
        }
        return null;
    }

    @Override
    public Team getByLeader(Integer leaderId) {
        return getOne(new LambdaQueryWrapper<Team>().eq(Team::getLeaderId, leaderId));
    }

    @Override
    public Team getByName(String name) {
        return getOne(new LambdaQueryWrapper<Team>().eq(Team::getName, name));
    }

    @Override
    public void delete(Integer id) {
        Team team = getById(id);
        removeById(id);
        userInvitationFlowService.clear(team.getLeaderId());
    }


    @Override
    public void editName(Integer id, String name) {
        Team team = getById(id);
        team.setId(id);
        team.setName(name);
        updateById(team);
    }

    @Override
    public void save(Integer leaderId, String name) {
        if (getByLeader(leaderId) != null) {
            throw new CrmebException("改用户已有团队不允许重复添加");
        }
        if (getByName(name) != null) {
            throw new CrmebException("团队名称重复不允许重复添加");
        }
        Team team = new Team(name, leaderId);
        save(team);
//        添加团队用户
        teamUserService.save(leaderId, team.getId());
        userInvitationFlowService.clear(team.getLeaderId());
    }

    @Override
    public PageInfo<Team> pageList(String name, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Team> lambdaQueryWrapper = new LambdaQueryWrapper<Team>()
                .eq(StringUtils.isNotEmpty(name), Team::getName, name);
        Page<Team> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(lambdaQueryWrapper));
    }

    @Override
    public List<Team> getByNameList(String name) {
        LambdaQueryWrapper<Team> lambdaQueryWrapper = new LambdaQueryWrapper<Team>()
                .eq(!ObjectUtil.isNull(name) && !name.equals(""), Team::getName, name);
        return list(lambdaQueryWrapper);
    }

}
