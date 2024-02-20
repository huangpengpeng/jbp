package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.RelationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreFlowServiceImpl extends ServiceImpl<RelationScoreFlowDao, RelationScoreFlow> implements RelationScoreFlowService {
    @Resource
    private UserService userService;
    @Override
    public PageInfo<RelationScoreFlow> pageList(Integer uid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScoreFlow> lqw = new LambdaQueryWrapper<RelationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), RelationScoreFlow::getUid, uid);
        Page<RelationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScoreFlow> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
