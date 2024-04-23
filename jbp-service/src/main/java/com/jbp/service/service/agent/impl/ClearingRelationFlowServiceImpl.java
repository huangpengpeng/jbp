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
import com.jbp.service.dao.agent.ClearingRelationFlowDao;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingRelationFlowService;
import com.jbp.service.service.agent.ClearingUserService;
import com.jbp.service.service.agent.UserRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingRelationFlowServiceImpl extends UnifiedServiceImpl<ClearingRelationFlowDao, ClearingRelationFlow> implements ClearingRelationFlowService {

    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private UserRelationService relationService;
    @Resource
    private UserService userService;
    @Resource
    private ClearingRelationFlowDao dao;

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
            List<UserRelation> relationList = relationService.list();
            Map<Integer, UserRelation> relationMap = FunctionUtil.keyValueMap(relationList, UserRelation::getUId);

            List<ClearingRelationFlow> flowList = Lists.newArrayList();
            int i = 1;
            for (User user : list) {
                log.info("正在处理销售关系紧缩, 当前:{}, 总数:{}", i, list.size());
                i++;
                List<UserUpperDto> allUpper = getAllUpper(user.getId(), relationMap);

                int level = 1;
                for (UserUpperDto upperDto : allUpper) {
                    if (upperDto.getPId() != null && clearingUserMap.get(upperDto.getPId()) != null) {
                        ClearingRelationFlow flow = new ClearingRelationFlow(clearingId, user.getId(), upperDto.getPId(), level, upperDto.getNode());
                        level++;
                        flowList.add(flow);
                    }
                }
            }
            List<List<ClearingRelationFlow>> partition = Lists.partition(flowList, 5000);
            for (List<ClearingRelationFlow> clearingRelationFlows : partition) {
                dao.insertBatch(clearingRelationFlows);
            }
        }
        return true;
    }


    private List<UserUpperDto> getAllUpper(Integer uid, Map<Integer, UserRelation> relationMap) {
        Integer self = uid;
        LinkedList<UserUpperDto> list = new LinkedList<>();
        int level = 1;
        do {
            UserRelation p = relationMap.get(uid);
            if (p != null) {
                UserUpperDto dto = new UserUpperDto();
                dto.setUId(self);
                dto.setPId(p.getPId());
                dto.setNode(p.getNode());
                dto.setLevel(level);
                level++;
                list.add(dto);
            }
            if (p == null) {
                break;
            }
            uid = p.getPId();
        } while (true);
        return list;
    }

    @Override
    public Boolean del4Clearing(Long clearingId) {
        return remove(new QueryWrapper<ClearingRelationFlow>().lambda().eq(ClearingRelationFlow::getClearingId, clearingId));
    }

    @Override
    public List<ClearingRelationFlow> getByUser(Integer uid, Integer limit) {
        LambdaQueryWrapper<ClearingRelationFlow> lqw = new QueryWrapper<ClearingRelationFlow>().lambda().eq(ClearingRelationFlow::getUId, uid)
                .orderByAsc(ClearingRelationFlow::getLevel);
        if(limit != null){
            lqw.last(" limit "+ limit);
        }
        return list(lqw);
    }

    @Override
    public List<ClearingRelationFlow> getByPUser(Integer pid) {
        return list(new QueryWrapper<ClearingRelationFlow>().lambda().eq(ClearingRelationFlow::getPId, pid)
                .orderByAsc(ClearingRelationFlow::getLevel));
    }

    @Override
    public PageInfo<ClearingRelationFlow> pageList(Integer uid, Integer pid, Long clearingId, Integer level, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ClearingRelationFlow> lqw = new LambdaQueryWrapper<ClearingRelationFlow>()
                .eq(!ObjectUtil.isNull(uid), ClearingRelationFlow::getUId, uid)
                .eq(!ObjectUtil.isNull(pid), ClearingRelationFlow::getPId, pid)
                .eq(!ObjectUtil.isNull(clearingId), ClearingRelationFlow::getClearingId, clearingId)
                .eq(!ObjectUtil.isNull(level), ClearingRelationFlow::getLevel, level)
                .orderByDesc(ClearingRelationFlow::getId)
                .orderByAsc(ClearingRelationFlow::getNode);
        Page<UserRelationFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ClearingRelationFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(ClearingRelationFlow::getUId).collect(Collectors.toList());
        List<Integer> pIdList = list.stream().map(ClearingRelationFlow::getPId).collect(Collectors.toList());
        uIdList.addAll(pIdList);
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);

        list.forEach(e -> {
            User uUser = uidMapList.get(e.getUId());
            e.setUAccount(uUser != null ? uUser.getAccount() : "");
            e.setUNickName(uUser != null ? uUser.getNickname() : "");
            User pUser = uidMapList.get(e.getPId());
            e.setPAccount(pUser != null ? pUser.getAccount() : "");
            e.setPNickName(pUser != null ? pUser.getNickname() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }
}
