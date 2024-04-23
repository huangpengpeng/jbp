package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.user.User;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.dao.agent.ClearingInvitationFlowDao;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingInvitationFlowServiceImpl extends UnifiedServiceImpl<ClearingInvitationFlowDao, ClearingInvitationFlow> implements ClearingInvitationFlowService {

    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserService userService;
    @Resource
    private ClearingInvitationFlowDao dao;

    @Override
    public Boolean create(Long clearingId) {
        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if (clearingFinal == null || !clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())) {
            throw new CrmebException("结算状态不是待结算不允许生成销售关系");
        }
        List<ClearingUser> clearingUserList = clearingUserService.getByClearing(clearingFinal.getId());

        Map<Integer, ClearingUser> clearingUserMap = FunctionUtil.keyValueMap(clearingUserList, ClearingUser::getUid);
        if (CollectionUtils.isEmpty(clearingUserList)) {
            throw new CrmebException("请先生成结算名单");
        }
        if (clearingFinal.getCommType().intValue() == ProductCommEnum.拓展佣金.getType()) {
            log.info("拓展佣金:不需要生成结算销售关系");
            return true;
        }
        if (clearingFinal.getCommType().intValue() == ProductCommEnum.平台分红.getType()) {
            log.info("平台分红:不需要生成结算销售关系");
            return true;
        }
        if (clearingFinal.getCommType().intValue() == ProductCommEnum.培育佣金.getType()) {
            // 删除上一次的结算关系网
            ClearingFinal lastOne = clearingFinalService.getLastOne(clearingId, ProductCommEnum.培育佣金.getType());
            if (lastOne != null) {
                del4Clearing(lastOne.getId());
            }
            List<User> list = userService.list();
            int i = 1;
            for (User user : list) {
                log.info("正在处理销售关系紧缩, 当前:{}, 总数:{}", i, list.size());
                i++;
                List<UserUpperDto> allUpper = invitationService.getAllUpper(user.getId());
                List<ClearingInvitationFlow> flowList = Lists.newArrayList();
                int level = 1;
                for (UserUpperDto upperDto : allUpper) {
                    if (upperDto.getPId() != null && clearingUserMap.get(upperDto.getPId()) != null) {
                        ClearingInvitationFlow flow = new ClearingInvitationFlow(clearingId, user.getId(), upperDto.getPId(), level);
                        level++;
                        flowList.add(flow);
                    }
                }
                if(!flowList.isEmpty()){
                    dao.insertBatch(flowList);
                }
            }
        }
        return true;
    }


    @Override
    public Boolean del4Clearing(Long clearingId) {
        return remove(new QueryWrapper<ClearingInvitationFlow>().lambda().eq(ClearingInvitationFlow::getClearingId, clearingId));
    }


    @Override
    public List<ClearingInvitationFlow> getByUser(Integer uid, Integer limit) {
        LambdaQueryWrapper<ClearingInvitationFlow> lqw = new QueryWrapper<ClearingInvitationFlow>().lambda().eq(ClearingInvitationFlow::getUId, uid)
                .orderByAsc(ClearingInvitationFlow::getLevel);
        if(limit != null){
            lqw.last(" limit "+ limit);
        }
        return list(lqw);
    }

    @Override
    public List<ClearingInvitationFlow> getByPUser(Integer pid) {
        return list(new QueryWrapper<ClearingInvitationFlow>().lambda().eq(ClearingInvitationFlow::getPId, pid)
                .orderByAsc(ClearingInvitationFlow::getLevel));
    }

    @Override
    public PageInfo<ClearingInvitationFlow> pageList(Integer uid, Integer pid, Integer level, Long clearingId, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ClearingInvitationFlow> lqw = new LambdaQueryWrapper<ClearingInvitationFlow>()
                .eq(!ObjectUtil.isNull(uid), ClearingInvitationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), ClearingInvitationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(level), ClearingInvitationFlow::getLevel, level)
                .eq(!ObjectUtil.isNull(clearingId), ClearingInvitationFlow::getClearingId, clearingId)
                .orderByDesc(ClearingInvitationFlow::getId);
        Page<ClearingInvitationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ClearingInvitationFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(ClearingInvitationFlow::getUId).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<Integer> pIdList = list.stream().map(ClearingInvitationFlow::getPId).collect(Collectors.toList());
        Map<Integer, User> pidMapList = userService.getUidMapList(pIdList);

        list.forEach(e -> {
            User uUser = uidMapList.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            User pUser = pidMapList.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setUNickName(pUser != null ? pUser.getNickname() : "");

        });
        return CommonPage.copyPageInfo(page, list);
    }
}
