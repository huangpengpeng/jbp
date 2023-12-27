package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.b2b.Team;
import com.jbp.common.model.b2b.UserInvitationFlow;
import com.jbp.service.dao.b2b.UserInvitationFlowDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserInvitationFlowService;
import com.jbp.service.service.UserInvitationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInvitationFlowServiceImpl extends ServiceImpl<UserInvitationFlowDao, UserInvitationFlow> implements UserInvitationFlowService {

    @Resource
    private TeamService teamService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserInvitationService userInvitationService;


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
        // 没有关系退出
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 保存
        saveBatch(list);
    }
}
