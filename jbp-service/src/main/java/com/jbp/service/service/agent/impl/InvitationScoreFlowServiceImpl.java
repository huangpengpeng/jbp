package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.InvitationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class InvitationScoreFlowServiceImpl extends ServiceImpl<InvitationScoreFlowDao, InvitationScoreFlow> implements InvitationScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<InvitationScoreFlow> pageList(Integer uid, Integer orderuid, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScoreFlow> lqw = new LambdaQueryWrapper<InvitationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), InvitationScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(orderuid), InvitationScoreFlow::getOrderUid, orderuid)
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), InvitationScoreFlow::getAction, action);
        Page<InvitationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScoreFlow> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
            e.setOrderAccount(userService.getById(e.getOrderUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
