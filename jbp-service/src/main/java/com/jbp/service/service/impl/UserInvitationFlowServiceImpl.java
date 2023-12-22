package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserInvitationFlow;
import com.jbp.service.dao.UserInvitationFlowMapper;
import com.jbp.service.service.UserInvitationFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInvitationFlowServiceImpl extends ServiceImpl<UserInvitationFlowMapper, UserInvitationFlow> implements UserInvitationFlowService {

    @Resource
    private UserInvitationFlowMapper mapper;


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteByUser(Long userId) {
        // 删除自己
        remove(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getUserId, userId));
        // 删除和有关系的
        List<UserInvitationFlow> list = list(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getPId, userId));
        for (UserInvitationFlow userInvitationFlow : list) {
            remove(new QueryWrapper<UserInvitationFlow>().lambda().eq(UserInvitationFlow::getUserId, userInvitationFlow.getUserId()));
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveBatch(List<UserInvitationFlow> list) {

        mapper.insertBatch(list);//批量添加没有实现
    }
}
