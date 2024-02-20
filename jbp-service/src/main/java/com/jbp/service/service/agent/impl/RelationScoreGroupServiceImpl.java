package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScoreGroup;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.RelationScoreGroupDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RelationScoreGroupServiceImpl extends ServiceImpl<RelationScoreGroupDao, RelationScoreGroup> implements RelationScoreGroupService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<RelationScoreGroup> pageList(Integer uid, String groupName, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScoreGroup> lqw = new LambdaQueryWrapper<RelationScoreGroup>()
                .eq(!ObjectUtil.isNull(uid), RelationScoreGroup::getUid, uid)
                .like(!ObjectUtil.isNull(groupName) && !groupName.equals(""), RelationScoreGroup::getGroupName, groupName);
        Page<RelationScoreGroup> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScoreGroup> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
