package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.RelationScoreFlowVo;
import com.jbp.service.dao.agent.RelationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreFlowService;
import com.jbp.service.util.StringUtils;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreFlowServiceImpl extends ServiceImpl<RelationScoreFlowDao, RelationScoreFlow> implements RelationScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<RelationScoreFlow> pageList(Integer uid, Integer orderuid, String ordersSn, String dateLimit, Integer node, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScoreFlow> lqw = new LambdaQueryWrapper<RelationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), RelationScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(orderuid), RelationScoreFlow::getOrderUid, orderuid)
                .eq(StringUtils.isNotEmpty(ordersSn), RelationScoreFlow::getOrdersSn, ordersSn)
                .eq(ObjectUtil.isNotEmpty(node), RelationScoreFlow::getNode, node)
                .eq(StringUtils.isNotEmpty(action), RelationScoreFlow::getAction, action);
        getRequestTimeWhere(lqw, dateLimit);
        lqw.orderByDesc(RelationScoreFlow::getId);
        Page<RelationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScoreFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(RelationScoreFlow::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        List<Integer> orderUidList = list.stream().map(RelationScoreFlow::getOrderUid).collect(Collectors.toList());
        Map<Integer, User> orderIdMapList = userService.getUidMapList(orderUidList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
            User orderUser = orderIdMapList.get(e.getOrderUid());
            e.setOrderAccount(orderUser != null ? orderUser.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<RelationScoreFlowVo> excel(Integer uid, Integer orderuid, String ordersSn, String dateLimit, Integer node, String action) {
        Long id = 0L;
        List<RelationScoreFlowVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<RelationScoreFlow> lqw = new LambdaQueryWrapper<RelationScoreFlow>()
                    .eq(!ObjectUtil.isNull(uid), RelationScoreFlow::getUid, uid)
                    .eq(!ObjectUtil.isNull(orderuid), RelationScoreFlow::getOrderUid, orderuid)
                    .eq(StringUtils.isNotEmpty(ordersSn), RelationScoreFlow::getOrdersSn, ordersSn)
                    .eq(ObjectUtil.isEmpty(node), RelationScoreFlow::getNode, node)
                    .eq(StringUtils.isEmpty(action), RelationScoreFlow::getAction, action);
            getRequestTimeWhere(lqw, dateLimit);
            lqw.gt(RelationScoreFlow::getId, id).last("LIMIT 1000");
            lqw.orderByDesc(RelationScoreFlow::getId);
            List<RelationScoreFlow> fundClearingList = list(lqw);
            if (CollectionUtils.isEmpty(fundClearingList)) {
                break;
            }
            List<Integer> uIdList = fundClearingList.stream().map(RelationScoreFlow::getUid).collect(Collectors.toList());
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            List<Integer> orderUidList = fundClearingList.stream().map(RelationScoreFlow::getOrderUid).collect(Collectors.toList());
            Map<Integer, User> orderIdMapList = userService.getUidMapList(orderUidList);
            fundClearingList.forEach(e -> {
                User user = uidMapList.get(e.getUid());
                e.setAccount(user != null ? user.getAccount() : "");
                RelationScoreFlowVo relationScoreFlowVo = new RelationScoreFlowVo();
                User orderUser = orderIdMapList.get(e.getOrderUid());
                e.setOrderAccount(orderUser != null ? orderUser.getAccount() : "");
                BeanUtils.copyProperties(e, relationScoreFlowVo);
                result.add(relationScoreFlowVo);
            });
            id = fundClearingList.get(fundClearingList.size() - 1).getId();
        } while (true);
        return result;
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<RelationScoreFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), RelationScoreFlow::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }
}
