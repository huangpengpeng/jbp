package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.service.dao.agent.UserRelationDao;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.service.agent.UserRelationService;

import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRelationServiceImpl extends ServiceImpl<UserRelationDao, UserRelation> implements UserRelationService {

    @Resource
    private UserRelationDao dao;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserRelationFlowService userRelationFlowService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public UserRelation getByUid(Integer uId) {
        return getOne(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getUId, uId));
    }

    @Override
    public Integer getPid(Integer uId) {
        UserRelation  userRelation = getByUid(uId);
        return userRelation == null ? null : userRelation.getPId();
    }

    @Override
    public List<UserRelation> getByPid(Integer pId) {
        return list(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getPId, pId).orderByAsc(UserRelation::getNode));
    }

    @Override
    public UserRelation getByPid(Integer pId, Integer node) {
        return getOne(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getPId, pId).eq(UserRelation::getNode, node));
    }

    @Override
    public List<UserUpperDto> getAllUpper(Integer uId) {
        return dao.getAllUpper(uId);
    }

    @Override
    public Boolean hasChild(Integer uId, Integer pId) {
        List<UserUpperDto> allUpper = getAllUpper(uId);
        return !ListUtils.emptyIfNull(allUpper).stream().filter(u -> pId == u.getPId()).collect(Collectors.toList()).isEmpty();
    }

    @Override
    public void validBand(Integer uId, Integer pId, Integer operateId, Integer node) {
        UserRelation receiverUser = getByPid(pId, node);
        if (receiverUser != null) {
            throw new RuntimeException("接受人当前点位存在用户接受人:" + pId + "点位:" + node);
        }
        if (hasChild(pId, uId)) {
            throw new RuntimeException("接受人不能是被安置人的下级, 被安置人:" + uId + "接受人:" + pId);
        }
        if (operateId != null) {
            if (!userInvitationService.hasChild(pId, operateId)) {
                throw new RuntimeException("接受人不是当前操作用户的下级, 接受人:" + pId + "操作人:" + operateId);
            }
        }
        if (!userInvitationService.hasChild(uId, operateId)) {
            throw new RuntimeException("被安置人不是当前操作用户的下级, 被安置人:" + uId + "操作人:" + operateId);
        }
    }

    @Override
    public UserRelation band(Integer uId, Integer pId, Integer operateId, Integer node) {
        validBand(uId, pId, operateId, node);
        UserRelation userRelation = getByUid(uId);
        if (userRelation == null) {
            userRelation = UserRelation.builder().uId(uId).pId(pId).node(node).build();
        }
        userRelation.setNode(node);
        userRelation.setUId(uId);
        userRelation.setPId(pId);

        // 执行更新
        UserRelation finalUserRelation = userRelation;
        transactionTemplate.execute(e -> {
            saveOrUpdate(finalUserRelation);
            // 删除关系留影
            userRelationFlowService.clear(uId);
            return Boolean.TRUE;
        });
        return userRelation;
    }

    @Override
    public List<UserRelation> getNoFlowList() {
        return dao.getNoFlowList();
    }
}
