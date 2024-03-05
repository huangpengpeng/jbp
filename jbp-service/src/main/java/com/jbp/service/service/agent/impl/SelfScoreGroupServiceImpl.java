package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.agent.SelfScoreGroup;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.SelfScoreGroupDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreGroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), SelfScoreGroup::getAction, action)
                .orderByDesc(SelfScoreGroup::getId);
        Page<SelfScoreGroup> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScoreGroup> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(SelfScoreGroup::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
