package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.SelfScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SelfScoreFlowServiceImpl extends ServiceImpl<SelfScoreFlowDao, SelfScoreFlow> implements SelfScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<SelfScoreFlow> pageList(Integer uid, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScoreFlow> lqw = new LambdaQueryWrapper<SelfScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), SelfScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), SelfScoreFlow::getAction, action);
        Page<SelfScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScoreFlow> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
