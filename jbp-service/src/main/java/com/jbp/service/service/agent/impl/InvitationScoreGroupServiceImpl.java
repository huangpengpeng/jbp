package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreGroup;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.InvitationScoreGroupDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreGroupService;
import com.jbp.service.util.StringUtils;
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
public class InvitationScoreGroupServiceImpl extends ServiceImpl<InvitationScoreGroupDao, InvitationScoreGroup> implements InvitationScoreGroupService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<InvitationScoreGroup> pageList(Integer uid, String groupName, String action, String nickname,PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScoreGroup> lqw = new LambdaQueryWrapper<InvitationScoreGroup>()
                .eq(!ObjectUtil.isNull(uid), InvitationScoreGroup::getUid, uid)
                .like(StringUtils.isNotEmpty(groupName), InvitationScoreGroup::getGroupName, groupName)
                .eq(StringUtils.isNotEmpty(action), InvitationScoreGroup::getAction, action)
                .orderByDesc(InvitationScoreGroup::getId);
        if (StrUtil.isNotBlank(nickname)){
            lqw.apply("1=1 and uid in (select id from eb_user where nickname like '%" + nickname + "%')");
        }
        Page<InvitationScoreGroup> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScoreGroup> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(InvitationScoreGroup::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setNickname(user != null ? user.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
