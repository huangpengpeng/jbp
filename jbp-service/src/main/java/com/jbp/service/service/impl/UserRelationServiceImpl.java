package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.b2b.UserRelation;
import com.jbp.service.dao.b2b.UserRelationDao;
import com.jbp.service.service.UserInvitationService;
import com.jbp.service.service.UserRelationFlowService;
import com.jbp.service.service.UserRelationService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public UserRelation getByUid(Integer uId) {
        return getOne(new LambdaQueryWrapper<UserRelation>().eq(UserRelation::getUId, uId));
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
        saveOrUpdate(userRelation);
        // 删除关系留影
        userRelationFlowService.clear(uId);
        return userRelation;
    }

    @Override
    public List<UserRelation> getNoFlowList() {
        return dao.getNoFlowList();
    }
}
