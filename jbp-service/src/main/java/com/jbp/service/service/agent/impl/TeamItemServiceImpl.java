package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.TeamItem;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.TeamItemDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.agent.TeamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class TeamItemServiceImpl extends ServiceImpl<TeamItemDao, TeamItem> implements TeamItemService {

    @Autowired
    private TeamService teamService;

    @Override
    public PageInfo<TeamItem> pageList(Integer tid, String name, PageParamRequest pageParamRequest) {
        Page<TeamItem> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<TeamItem> lqw = new LambdaQueryWrapper<>();
        lqw.eq(!Objects.isNull(tid), TeamItem::getTid, tid);
        lqw.like(!StringUtils.isEmpty(name), TeamItem::getName, name);
        lqw.orderByDesc(TeamItem::getId);
        List<TeamItem> list = list(lqw);
        if (list.isEmpty()){
            return CommonPage.copyPageInfo(page, list);
        }
        list.forEach(e->{
            Team team = teamService.getById(e.getTid());
            e.setTeamName(team != null ? team.getName() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void add(Integer tid, String name) {
        if (tid == null || StringUtils.isEmpty(name)) {
            throw new CrmebException("参数不完整");
        }
        Team team = teamService.getById(tid);
        if (team == null){
            throw new CrmebException("该团队不存在");
        }
        TeamItem item = getOne(new QueryWrapper<TeamItem>().lambda().eq(TeamItem::getTid, tid));
        if (item != null){
            throw new CrmebException("该团队已存在项目,请勿重复添加！");
        }
        TeamItem teamItem = new TeamItem();
        teamItem.setTid(tid);
        teamItem.setName(name);
        save(teamItem);
    }

    @Override
    public void edit(Integer id, Integer tid, String name) {
        if (id == null || StringUtils.isEmpty(name) || tid == null) {
            throw new CrmebException("参数不完整");
        }
        Team team = teamService.getById(tid);
        if (team == null){
            throw new CrmebException("该团队不存在");
        }
        TeamItem teamItem = getById(id);
        if (teamItem == null) {
            throw new CrmebException("团队项目不存在");
        }
        TeamItem item = getOne(new QueryWrapper<TeamItem>().lambda().eq(TeamItem::getTid, tid).eq(TeamItem::getId, id));
        if (item == null){
            TeamItem one = getOne(new QueryWrapper<TeamItem>().lambda().eq(TeamItem::getTid, tid));
            if (one  != null){
                throw new CrmebException("该团队已存在项目,请勿重复添加！");
            }
        }
        teamItem.setTid(tid);
        teamItem.setName(name);
        updateById(teamItem);
    }
}
