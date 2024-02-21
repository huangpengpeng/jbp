package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreGroup;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.InvitationScoreGroupDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class InvitationScoreGroupServiceImpl extends ServiceImpl<InvitationScoreGroupDao, InvitationScoreGroup> implements InvitationScoreGroupService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<InvitationScoreGroup> pageList(Integer uid, String groupName, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScoreGroup> lqw = new LambdaQueryWrapper<InvitationScoreGroup>()
                .eq(!ObjectUtil.isNull(uid), InvitationScoreGroup::getUid, uid)
                .like(!ObjectUtil.isNull(groupName) && !groupName.equals(""), InvitationScoreGroup::getGroupName, groupName)
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), InvitationScoreGroup::getAction, action);
        Page<InvitationScoreGroup> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScoreGroup> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
