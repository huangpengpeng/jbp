package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.InvitationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
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
    public PageInfo<InvitationScoreFlow> pageList(Integer uid, Integer orderuid, String action, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<InvitationScoreFlow> lqw = new LambdaQueryWrapper<InvitationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), InvitationScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(orderuid), InvitationScoreFlow::getOrderUid, orderuid)
                .eq(StringUtils.isNotEmpty(action), InvitationScoreFlow::getAction, action);
        Page<InvitationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<InvitationScoreFlow> list = list(lqw);
        if(CollectionUtils.isEmpty(list)){
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
    public InvitationScoreFlow add(Integer uid, Integer orderUid, BigDecimal score, String action, String operate, String ordersSn, Date payTime, List<ProductInfoDto> productInfo, String remark) {
        InvitationScoreFlow flow = new InvitationScoreFlow(uid, orderUid, score, action, operate, ordersSn, payTime, productInfo, remark);
        save(flow);
        return flow;
    }
}
