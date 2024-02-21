package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.SelfScoreGroup;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.SelfScoreGroupDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class SelfScoreGroupServiceImpl extends ServiceImpl<SelfScoreGroupDao, SelfScoreGroup> implements SelfScoreGroupService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<SelfScoreGroup> pageList(Integer uid,String groupName, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScoreGroup> lqw = new LambdaQueryWrapper<SelfScoreGroup>()
                .eq(!ObjectUtil.isNull(uid), SelfScoreGroup::getUid, uid)
                .like(!ObjectUtil.isNull(groupName)&&!groupName.equals(""),SelfScoreGroup::getGroupName,groupName)
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), SelfScoreGroup::getAction, action);
        Page<SelfScoreGroup> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScoreGroup> list = list(lqw);
        list.forEach(e -> {
            e.setAccount(userService.getById(e.getUid()).getAccount());
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
