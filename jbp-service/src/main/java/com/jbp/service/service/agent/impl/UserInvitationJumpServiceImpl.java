package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserInvitationJump;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserInvitationJumpListResponse;
import com.jbp.service.dao.agent.UserInvitationJumpDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售关系网跳转
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationJumpServiceImpl extends ServiceImpl<UserInvitationJumpDao, UserInvitationJump> implements UserInvitationJumpService {

    @Autowired
    private UserService userService;

    @Override
    public UserInvitationJump add(Integer uId, Integer pId, Integer orgPid) {
        UserInvitationJump jump = new UserInvitationJump(uId, pId, orgPid);
        save(jump);
        return jump;
    }

    @Override
    public Boolean ifJump(Integer uId) {
        return !list(new LambdaQueryWrapper<UserInvitationJump>().eq(UserInvitationJump::getUId, uId)).isEmpty();
    }

    @Override
    public PageInfo<UserInvitationJumpListResponse> pageList(Integer uid, Integer pid, Integer orgPid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitationJump> lqw = new LambdaQueryWrapper<UserInvitationJump>()
                .eq(!ObjectUtil.isNull(uid), UserInvitationJump::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), UserInvitationJump::getPId, pid)
                .eq(!ObjectUtil.isNull(orgPid), UserInvitationJump::getOrgPid, orgPid);
        Page<UserInvitationJump> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitationJump> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> collect = list.stream().map(UserInvitationJump::getUId).collect(Collectors.toList());
        List<Integer> collect2 = list.stream().map(UserInvitationJump::getOrgPid).collect(Collectors.toList());
        List<Integer> collect3 = list.stream().map(UserInvitationJump::getPId).collect(Collectors.toList());
        collect.addAll(collect2);
        collect.addAll(collect3);
        Map<Integer, User> uidMapList = userService.getUidMapList(collect);

        List<UserInvitationJumpListResponse> responseList = list.stream().map(e -> {
            UserInvitationJumpListResponse response = new UserInvitationJumpListResponse();
            User user = uidMapList.get(e.getUId());
            response.setUId(e.getUId());
            response.setUaccount(user.getAccount());
            User puser = uidMapList.get(e.getPId());
            response.setPId(e.getPId());
            response.setPaccount(puser.getAccount());
            User orgPUser = uidMapList.get(e.getOrgPid());
            response.setOrgPid(e.getOrgPid());
            response.setOaccount(orgPUser.getAccount());
            response.setGmtCreated(e.getGmtCreated());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);

    }
}
