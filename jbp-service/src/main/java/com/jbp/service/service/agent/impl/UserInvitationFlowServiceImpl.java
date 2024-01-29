package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserInvitationFlowDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationFlowServiceImpl extends ServiceImpl<UserInvitationFlowDao, UserInvitationFlow> implements UserInvitationFlowService {

    @Resource
    private TeamService teamService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserService userService;


    /**
     * 用户关系如果变更需要将更当前用户有关的的记录全部删除重新生成
     */
    @Override
    public void clear(Integer uId) {
        LambdaQueryWrapper<UserInvitationFlow> wrapper = new LambdaQueryWrapper();
        wrapper.eq(UserInvitationFlow::getUId, uId).or().eq(UserInvitationFlow::getPId, uId);
        remove(wrapper);
    }

    /**
     * 刷新用户关系+团队
     */
    @Override
    public void refreshFlowAndTeam(Integer uId) {
        // 查询出所有的上级
        List<UserUpperDto> upperList = userInvitationService.getAllUpper(uId);
        // 如果上级没有就直接返回
        if (CollectionUtils.isEmpty(upperList)) {
            return;
        }
        // 更新团队
        teamUserService.deleteByUid(uId);
        Team team = teamService.getLastTeam(uId, upperList);
        if (team != null) {
            teamUserService.save(uId, team.getId());
        }
        // 获取所有的上级添加关系
        List<UserInvitationFlow> list = Lists.newArrayList();
        for (UserUpperDto upper : upperList) {
            if (upper.getPId() != null && upper.getPId() > 0) {
                UserInvitationFlow flow = new UserInvitationFlow(uId, upper.getPId(), upper.getLevel());
                list.add(flow);
            }
        }
        // 保存 list空 mybatis自带剔除
        saveBatch(list);
    }

    @Override
    public PageInfo<UserInvitationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitationFlow> lqw = new LambdaQueryWrapper<UserInvitationFlow>()
                .eq(!ObjectUtil.isNull(uid), UserInvitationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserInvitationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(level),UserInvitationFlow::getLevel,level);
        Page<UserInvitationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitationFlow> list = list(lqw);
        list.forEach(e -> {
            e.setUAccount(userService.getById(e.getUId()).getAccount());
            e.setPAccount(userService.getById(e.getPId()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
