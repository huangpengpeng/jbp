package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.UserInvitationDto;
import com.jbp.common.model.UserUpperModel;
import com.jbp.common.model.user.UserInvitation;
import com.jbp.common.model.user.UserTeam;
import com.jbp.common.utils.Number2Utils;
import com.jbp.service.dao.UserInvitationMapper;
import com.jbp.service.service.UserInvitationFlowService;
import com.jbp.service.service.UserInvitationService;
import com.jbp.service.service.UserTeamService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserInvitationServiceImpl extends ServiceImpl<UserInvitationMapper, UserInvitation> implements UserInvitationService {

    @Resource
    private UserInvitationMapper mapper;

    @Resource
    private UserTeamService userTeamService;



    @Resource
    private UserInvitationFlowService userInvitationFlowService;


    @Override
    public UserInvitation getByUser(Long userId) {
        return getOne(new QueryWrapper<UserInvitation>().lambda().eq(UserInvitation::getUserId, userId));
    }

    @Override
    public Long getParent(Long userId) {
        UserInvitation user = getByUser(userId);
        if (user == null) {
            return null;
        }
        return user.getMId() != null ? user.getMId() : user.getPId();
    }

    @Override
    public Long getParentNoMount(Long userId) {
        UserInvitation user = getByUser(userId);
        if (user == null) {
            return null;
        }
        return user.getPId();
    }

    @Override
    public List<UserInvitation> getNext(Long userId) {
        List<UserInvitation> list = list(new QueryWrapper<UserInvitation>().lambda().eq(UserInvitation::getPId, userId).isNull(UserInvitation::getMId));
        List<UserInvitation> list2 = list(new QueryWrapper<UserInvitation>().lambda().eq(UserInvitation::getMId, userId));
        list.addAll(list2);
        return list;
    }

    @Override
    public List<UserInvitation> getNextNoMount(Long userId) {
        return list(new QueryWrapper<UserInvitation>().lambda().eq(UserInvitation::getPId, userId).isNull(UserInvitation::getMId));
    }

    @Override
    public UserInvitation band(Long userId, Long pId, Boolean ifM, Boolean ifForce) {
        validBand(userId, pId);
        UserInvitation userInvitation = getByUser(userId);
        if (userInvitation == null) {
            userInvitation = new UserInvitation();
        }
        if (BooleanUtils.isTrue(ifM)) {
            userInvitation.setMId(pId);
        } else {
            userInvitation.setPId(pId);
        }
        userInvitation.setUserId(userId);
        userInvitation.setIfForce(ifForce);
        saveOrUpdate(userInvitation);

        // 刷新团队
        UserTeam userTeam = userTeamService.getByUser(userId);
        if(userTeam != null && !userTeam.getIfHead()){
            userTeamService.del(userTeam.getName());
        }
        // 删除关系留影
        userInvitationFlowService.deleteByUser(userId);
        return userInvitation;
    }

    /**
     * 不允许倒挂，原本是上级挂给下级【直接推荐上级 或者  转挂的上级都不行
     * 直推关系死循环  转挂关系死循环
     */
    @Override
    public void validBand(Long userId, Long pId) {
        if (Number2Utils.equals(userId, pId)) {

            throw new RuntimeException("自己不能绑定自己");
        }
        // 上级不能给下级
        if (hasChild(pId, userId)) {
            throw new RuntimeException("关系的上级不能绑定给自己");
        }
        // 上级不能给下级
        if (hasChildNoMount(pId, userId)) {
            throw new RuntimeException("血缘关系的上级不能绑定给自己");
        }
    }

    /**
     * 操作人 必须操作的是自己伞下用户【转挂第一优先级的的山下】
     * <p>
     * 两个用户往上都要找到操作用户
     */
    @Override
    public void validOperatePermission(Long operateUserId, Long userId, Long pId) {
        if (!hasChild(userId, operateUserId)) {
            throw new RuntimeException("用户不是当前操作用户的伞下用户");
        }
        if (!hasChild(pId, operateUserId)) {
            throw new RuntimeException("接受用户不是当前操作用户的伞下用户");
        }
    }


    @Override
    public Page<UserInvitationDto> adminPage(Page<UserInvitationDto> page, Map map) {
        return mapper.adminPage(page, map);

    }

    @Override
    public List<UserUpperModel> getAllUpper(Long userId) {
        return mapper.getAllUpper(userId);
    }

    @Override
    public Boolean hasChild(Long userId, Long pId) {
        List<UserUpperModel> list = mapper.getUnder4PId(userId, pId);
        return list != null && !list.isEmpty();
    }

    @Override
    public List<UserUpperModel> getAllUpperNoMount(Long userId) {
        return mapper.getAllUpperNoMount(userId);
    }

    @Override
    public Boolean hasChildNoMount(Long userId, Long pId) {
        List<UserUpperModel> list = mapper.getUnder4PIdNoMount(userId, pId);
        return list != null && !list.isEmpty();
    }

    @Override
    public Integer getUnderCount4Capa(Long userId, Long capaId, Integer level) {
        return mapper.getUnderCount4Capa(userId, capaId, level);
    }

    @Override
    public Integer getUnderCount4CapaXs(Long userId, Long capaId) {
        return mapper.getUnderCount4CapaXs(userId, capaId);
    }

    @Override
    public List<UserInvitation> getNoInvitationFlowList() {
        return mapper.getNoInvitationFlowList();
    }

}
