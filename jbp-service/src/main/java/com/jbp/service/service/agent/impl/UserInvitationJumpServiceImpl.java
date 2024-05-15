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

import java.util.*;
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
    public LinkedList<UserInvitationJump> getFirst4OrgPid(Integer orgPid) {
        LinkedList<UserInvitationJump> result = new LinkedList<>();
        List<UserInvitationJump> list = list(new LambdaQueryWrapper<UserInvitationJump>().eq(UserInvitationJump::getOrgPid, orgPid));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Set<Integer> uidSet = list.stream().map(UserInvitationJump::getUId).collect(Collectors.toSet());
        for (Integer uid : uidSet) {
            UserInvitationJump one = getOne(new LambdaQueryWrapper<UserInvitationJump>().eq(UserInvitationJump::getUId, uid).orderByAsc(UserInvitationJump::getId).last(" limit 1"));
            if (one.getOrgPid().intValue() == orgPid.intValue()) {
                result.add(one);
            }
        }
        result = new LinkedList<>(result.stream().sorted(Comparator.comparing(UserInvitationJump::getId)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public PageInfo<UserInvitationJumpListResponse> pageList(Integer uId, Integer pId, Integer orgPid, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<UserInvitationJump> lqw = new LambdaQueryWrapper<UserInvitationJump>()
                .eq(!ObjectUtil.isNull(uId), UserInvitationJump::getUId, uId)
                .eq(!ObjectUtil.isNull(pId), UserInvitationJump::getPId, pId)
                .eq(!ObjectUtil.isNull(orgPid), UserInvitationJump::getOrgPid, orgPid)
                .orderByDesc(UserInvitationJump::getId);

        Page<UserInvitationJump> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserInvitationJump> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> uIdList = list.stream().map(UserInvitationJump::getUId).collect(Collectors.toList());
        uIdList.addAll(list.stream().map(UserInvitationJump::getPId).collect(Collectors.toList()));
        uIdList.addAll(list.stream().map(UserInvitationJump::getOrgPid).collect(Collectors.toList()));
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<UserInvitationJumpListResponse> responseList = list.stream().map(e -> {
            UserInvitationJumpListResponse response = new UserInvitationJumpListResponse();

            User user = uidMapList.get(e.getUId());
            response.setUId(e.getUId());
            response.setUaccount(user != null ? user.getAccount() : "");

            User puser = uidMapList.get(e.getPId());
            response.setPId(e.getPId());
            response.setPaccount(puser != null ? puser.getAccount() : "");

            User orgPUser = uidMapList.get(e.getOrgPid());
            response.setOrgPid(e.getOrgPid());
            response.setOaccount(orgPUser != null ? orgPUser.getAccount() : "");

            response.setGmtCreated(e.getGmtCreated());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }
}
