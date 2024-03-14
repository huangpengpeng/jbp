package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.agent.RelationScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.RelationScoreFlowService;
import com.jbp.service.util.StringUtils;
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
public class RelationScoreFlowServiceImpl extends ServiceImpl<RelationScoreFlowDao, RelationScoreFlow> implements RelationScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<RelationScoreFlow> pageList(Integer uid, Integer orderuid, String ordersSn, String dateLimit, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<RelationScoreFlow> lqw = new LambdaQueryWrapper<RelationScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), RelationScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(orderuid), RelationScoreFlow::getOrderUid, orderuid)
                .eq(StringUtils.isNotEmpty(ordersSn), RelationScoreFlow::getOrdersSn, ordersSn);
        getRequestTimeWhere(lqw, dateLimit);
        lqw.orderByDesc(RelationScoreFlow::getId);
        Page<RelationScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<RelationScoreFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(RelationScoreFlow::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<RelationScoreFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), RelationScoreFlow::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }
}
