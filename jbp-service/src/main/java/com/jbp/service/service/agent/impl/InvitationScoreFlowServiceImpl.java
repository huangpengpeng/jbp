package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.InvitationScoreFlowVo;
import com.jbp.service.dao.agent.InvitationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class InvitationScoreFlowServiceImpl extends ServiceImpl<InvitationScoreFlowDao, InvitationScoreFlow> implements InvitationScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<InvitationScoreFlow> pageList(Integer uid, Integer orderuid, String action, String ordersSn, String dateLimit, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScoreFlow> lqw = new LambdaQueryWrapper<InvitationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), InvitationScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(orderuid), InvitationScoreFlow::getOrderUid, orderuid)
                .eq(StringUtils.isNotEmpty(action), InvitationScoreFlow::getAction, action)
                .eq(StringUtils.isNotEmpty(ordersSn), InvitationScoreFlow::getOrdersSn, ordersSn);
        getRequestTimeWhere(lqw, dateLimit);
        lqw.orderByDesc(InvitationScoreFlow::getId);
        Page<InvitationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScoreFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(InvitationScoreFlow::getUid).collect(Collectors.toList());
        List<Integer> uIdList2 = list.stream().map(InvitationScoreFlow::getOrderUid).collect(Collectors.toList());
        uIdList.addAll(uIdList2);
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            User user2 = uidMapList.get(e.getOrderUid());
            e.setAccount(user != null ? user.getAccount() : "");
            e.setOrderAccount(user2 != null ? user2.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<InvitationScoreFlowVo> excel(Integer uid, Integer orderuid, String action, String ordersSn, String dateLimit) {
        Long id = 0L;
        List<InvitationScoreFlowVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<InvitationScoreFlow> lqw = new LambdaQueryWrapper<InvitationScoreFlow>()
                    .gt(InvitationScoreFlow::getId, id)
                    .eq(!ObjectUtil.isNull(uid), InvitationScoreFlow::getUid, uid)
                    .eq(!ObjectUtil.isNull(orderuid), InvitationScoreFlow::getOrderUid, orderuid)
                    .eq(StringUtils.isNotEmpty(action), InvitationScoreFlow::getAction, action)
                    .eq(StringUtils.isNotEmpty(ordersSn), InvitationScoreFlow::getOrdersSn, ordersSn);
            getRequestTimeWhere(lqw, dateLimit);
            lqw.orderByAsc(InvitationScoreFlow::getId);
            lqw.last("LIMIT 1000");
            List<InvitationScoreFlow> fundClearingList = list(lqw);
            if (CollectionUtils.isEmpty(fundClearingList)) {
                break;
            }
            List<Integer> uIdList = fundClearingList.stream().map(InvitationScoreFlow::getUid).collect(Collectors.toList());
            List<Integer> uIdList2 = fundClearingList.stream().map(InvitationScoreFlow::getOrderUid).collect(Collectors.toList());
            uIdList.addAll(uIdList2);
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            fundClearingList.forEach(e -> {
                User user = uidMapList.get(e.getUid());
                User user2 = uidMapList.get(e.getOrderUid());
                e.setAccount(user != null ? user.getAccount() : "");
                e.setOrderAccount(user2 != null ? user2.getAccount() : "");
                InvitationScoreFlowVo invitationScoreFlowVo = new InvitationScoreFlowVo();
                BeanUtils.copyProperties(e, invitationScoreFlowVo);
                result.add(invitationScoreFlowVo);
            });
            id = fundClearingList.get(fundClearingList.size()-1).getId();
        } while (true);
        return result;
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<InvitationScoreFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), InvitationScoreFlow::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }

    @Override
    public InvitationScoreFlow add(Integer uid, Integer orderUid, BigDecimal score, String action, String operate, String ordersSn, Date payTime, List<ProductInfoDto> productInfo, String remark) {
        InvitationScoreFlow flow = new InvitationScoreFlow(uid, orderUid, score, action, operate, ordersSn, payTime, productInfo, remark);
        save(flow);
        return flow;
    }
}
