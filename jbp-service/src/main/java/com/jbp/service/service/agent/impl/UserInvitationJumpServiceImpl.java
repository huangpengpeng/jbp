package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.agent.UserInvitationJump;
import com.jbp.service.dao.agent.UserInvitationFlowDao;
import com.jbp.service.dao.agent.UserInvitationJumpDao;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 销售关系网跳转
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationJumpServiceImpl extends ServiceImpl<UserInvitationJumpDao, UserInvitationJump> implements UserInvitationJumpService {

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
}
