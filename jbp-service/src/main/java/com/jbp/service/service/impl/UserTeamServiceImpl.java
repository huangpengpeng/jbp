package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jbp.common.model.UserUpperModel;
import com.jbp.common.model.user.UserTeam;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.UserTeamMapper;
import com.jbp.service.service.UserInvitationService;
import com.jbp.service.service.UserTeamService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements UserTeamService {

    @Resource
    private UserTeamMapper mapper;


    @Resource
    private UserInvitationService userInvitationService;


    @Override
    public UserTeam getByUser(Long userId) {
        return getOne(new QueryWrapper<UserTeam>().lambda().eq(UserTeam::getUserId, userId));
    }

    @Override
    public UserTeam getByName(String name) {
        return getOne(new QueryWrapper<UserTeam>().lambda().eq(UserTeam::getName, name).eq(UserTeam::getIfHead, true));
    }

    @Override
    public Page<UserTeam> adminPage(Page<UserTeam> page, Map map) {
        return mapper.adminPage(page, map);
    }

    /**
     * 只处理存在邀请关系 但是没有userTeam 记录的用户  【存在顶级账户上级是公司的 没有团队记录】
     * @param userId
     */
    @Override
    public void refresh(Long userId) {
        // 存在不用更新
        if (getByUser(userId) != null) {
            return;
        }
        // 获取全部上级
        List<UserUpperModel> allUpper = userInvitationService.getAllUpper(userId);


        // 为空给默认团队 默认名称+自己的ID
        if (allUpper.isEmpty()) {
            UserTeam userTeam = UserTeam.builder().userId(userId).name(UserTeam.Constants.默认空.toString() + userId).ifHead(false).build();
            save(userTeam);
            return;
        }
        // 整条线需要更新团队的用户
        List<UserUpperModel> noHeadList = Lists.newArrayList();
        String headName = "";
        for (UserUpperModel userUpperDto : allUpper) {
            headName = UserTeam.Constants.默认空.toString();
            UserTeam userTeam = getByUser(userUpperDto.getUId());
            if (userTeam != null) {
                headName = userTeam.getName();
                break;
            }
            headName = headName + userUpperDto.getUId(); // 没有团队  就绑定最上层的ID
            noHeadList.add(userUpperDto);
        }
        // 整条线没有团队的用户全部加上
        List<UserTeam> batchList = Lists.newArrayList();
        for (UserUpperModel userAllUnderDto : noHeadList) {
            UserTeam userTeam = UserTeam.builder().userId(userAllUnderDto.getUId()).name(headName).ifHead(false).build();
            batchList.add(userTeam);
        }
        saveBatch(batchList); // 这里上级一条线不会超过1000 所以不用拆分批量保存的记录
    }

    @Override
    public void add(Long userId, String teamName) {
        if (StringUtils.contains(teamName, UserTeam.Constants.默认空.toString())) {

            throw new RuntimeException("团队名称包含敏感词");
        }
        UserTeam userTeam = getByUser(userId);
        if (userTeam == null) {
            throw new RuntimeException("当前用户团队初始化未完成请稍后重试");
        }
        // 删除跟自己名称相同的团队
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserTeam::getIfHead, false).eq(UserTeam::getName, userTeam.getName());
        remove(wrapper);
        // 删除自己
        removeById(userTeam.getId());
        // 保存自己是团队头
        userTeam = UserTeam.builder().userId(userId).name(teamName).ifHead(true).build();
        save(userTeam);
    }

    @Override
    public void del(String teamName) {
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserTeam::getName, teamName);
        remove(wrapper);
    }


}
