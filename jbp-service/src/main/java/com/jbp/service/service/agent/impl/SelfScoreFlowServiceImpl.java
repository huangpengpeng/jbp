package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.SelfScoreFlowVo;
import com.jbp.service.dao.agent.SelfScoreFlowDao;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.SelfScoreFlowService;
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
public class SelfScoreFlowServiceImpl extends ServiceImpl<SelfScoreFlowDao, SelfScoreFlow> implements SelfScoreFlowService {
    @Resource
    private UserService userService;

    @Override
    public PageInfo<SelfScoreFlow> pageList(Integer uid, String action, String ordersSn, String dateLimit, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<SelfScoreFlow> lqw = new LambdaQueryWrapper<SelfScoreFlow>()
                .eq(!ObjectUtil.isNull(uid), SelfScoreFlow::getUid, uid)
                .eq(!ObjectUtil.isNull(action) && !action.equals(""), SelfScoreFlow::getAction, action)
                .eq(StringUtils.isNotEmpty(ordersSn), SelfScoreFlow::getOrdersSn, ordersSn);
        getRequestTimeWhere(lqw, dateLimit);
        lqw.orderByDesc(SelfScoreFlow::getId);
        Page<SelfScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<SelfScoreFlow> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> uIdList = list.stream().map(SelfScoreFlow::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
        list.forEach(e -> {
            User user = uidMapList.get(e.getUid());
            e.setAccount(user != null ? user.getAccount() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public List<SelfScoreFlowVo> excel(Integer uid, String action, String ordersSn, String dateLimit) {
        Long id = 0L;
        List<SelfScoreFlowVo> result = Lists.newArrayList();
        do {
            LambdaQueryWrapper<SelfScoreFlow> lqw = new LambdaQueryWrapper<SelfScoreFlow>()
                    .gt(SelfScoreFlow::getId, id)
                    .eq(!ObjectUtil.isNull(uid), SelfScoreFlow::getUid, uid)
                    .eq(!ObjectUtil.isNull(action) && !action.equals(""), SelfScoreFlow::getAction, action)
                    .eq(StringUtils.isNotEmpty(ordersSn), SelfScoreFlow::getOrdersSn, ordersSn);
            getRequestTimeWhere(lqw, dateLimit);
            lqw.orderByAsc(SelfScoreFlow::getId);
            lqw.last("LIMIT 1000");
            List<SelfScoreFlow> fundClearingList = list(lqw);
            if (CollectionUtils.isEmpty(fundClearingList)) {
                break;
            }
            List<Integer> uIdList = fundClearingList.stream().map(SelfScoreFlow::getUid).collect(Collectors.toList());
            Map<Integer, User> uidMapList = userService.getUidMapList(uIdList);
            fundClearingList.forEach(e -> {
                User user = uidMapList.get(e.getUid());
                e.setAccount(user != null ? user.getAccount() : "");
                SelfScoreFlowVo selfScoreFlow = new SelfScoreFlowVo();
                BeanUtils.copyProperties(e, selfScoreFlow);
                result.add(selfScoreFlow);
            });
            id = fundClearingList.get(fundClearingList.size() - 1).getId();
        } while (true);
        return result;
    }

    private void getRequestTimeWhere(LambdaQueryWrapper<SelfScoreFlow> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(StringUtils.isNotEmpty(dateLimit), SelfScoreFlow::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }

    @Override
    public SelfScoreFlow add(Integer uid, BigDecimal score, String action, String operate, String ordersSn, Date payTime,
                             List<ProductInfoDto> productInfo, String remark) {
        SelfScoreFlow selfScoreFlow = new SelfScoreFlow(uid, score, action, operate, ordersSn, payTime, productInfo, remark);
        save(selfScoreFlow);
        return selfScoreFlow;
    }
}
