package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.b2b.Team;
import com.jbp.common.model.b2b.TeamUser;
import com.jbp.service.dao.b2b.TeamDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.UserInvitationFlowService;
import com.jbp.service.service.UserInvitationService;
import com.jbp.service.service.TeamUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamDao, Team> implements TeamService {

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
        delete(id);
        userInvitationFlowService.clear(team.getLeaderId());
    }


    @Override
    public void editName(Integer id, String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        updateById(team);
    }

    @Override
    public void save(Integer leaderId, String name) {
        if (getByLeader(leaderId) != null) {
            throw new CrmebException("已经是团队长不允许重复添加");
        }
        if (getByName(name) != null) {
            throw new CrmebException("团队名称重复不允许重复添加");
        }
        Team team = new Team(name, leaderId);
        save(team);
        userInvitationFlowService.clear(team.getLeaderId());
    }

}
