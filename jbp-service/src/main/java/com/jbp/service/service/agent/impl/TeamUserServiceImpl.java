package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.TeamUserDao;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserDao, TeamUser> implements TeamUserService {
    @Resource
    TeamUserDao teamUserDao;
    @Resource
    UserService userService;

    @Override
    public TeamUser save(Integer uId, Integer tId) {
        TeamUser byUser = getByUser(uId);
        if (!ObjectUtil.isNull(byUser)) {
            throw new CrmebException(StringUtils.format("%s账号已有团队", userService.getById(uId).getAccount()));
        }
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
    public PageInfo<TeamUser> pageList(Integer tid, String account, Integer teamLeader, PageParamRequest pageParamRequest) {
        Page<TeamUser> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<TeamUser> teamUserList = teamUserDao.pageList(tid, account, teamLeader);
        return CommonPage.copyPageInfo(page, teamUserList);
    }

}
