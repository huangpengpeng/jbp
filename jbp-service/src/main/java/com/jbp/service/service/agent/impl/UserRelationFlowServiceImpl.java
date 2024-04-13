package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.model.agent.UserRelationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.UserRelationFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.service.agent.UserRelationService;
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
public class UserRelationFlowServiceImpl extends ServiceImpl<UserRelationFlowDao, UserRelationFlow> implements UserRelationFlowService {

    @Resource
    private UserRelationService userRelationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserCapaService userCapaService;

    @Override
    public void clear(Integer uid) {
        List<UserRelationFlow> list = list(new QueryWrapper<UserRelationFlow>().lambda().eq(UserRelationFlow::getPId, uid));
        List<Integer> userIdList = list.stream().map(UserRelationFlow::getUId).collect(Collectors.toList());
        userIdList.add(uid);
        remove(new LambdaQueryWrapper<UserRelationFlow>().in(UserRelationFlow::getUId, userIdList));
    }

    @Override
    public void refresh(Integer uId) {
        // 查询出所有的上级
        List<UserUpperDto> upperList = userRelationService.getAllUpper(uId);
        // 如果上级没有就直接返回
        if (CollectionUtils.isEmpty(upperList)) {
            return;
        }
        // 获取所有的上级添加关系
        List<UserRelationFlow> list = Lists.newArrayList();
        for (UserUpperDto upper : upperList) {
            if (upper.getPId() != null && upper.getPId() > 0) {
                UserRelationFlow flow = new UserRelationFlow(uId, upper.getPId(), upper.getLevel(), upper.getNode());
                list.add(flow);
            }
        }
        // 保存 list空 mybatis自带剔除
        saveBatch(list);
    }

    @Override
    public PageInfo<UserRelationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest) {
        z
    }
}
