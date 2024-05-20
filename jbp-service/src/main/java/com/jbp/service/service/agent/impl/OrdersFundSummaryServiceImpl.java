package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.OrdersFundSummary;
import com.jbp.common.model.agent.Team;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.OrdersFundSummaryDao;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.agent.OrdersFundSummaryService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrdersFundSummaryServiceImpl extends ServiceImpl<OrdersFundSummaryDao, OrdersFundSummary> implements OrdersFundSummaryService {


    @Resource
    private TeamService teamService;

    @Override
    public PageInfo<OrdersFundSummary> pageList(String ordersSn,String teamId,  PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<OrdersFundSummary> lqw = new LambdaQueryWrapper<OrdersFundSummary>()
                .like(!ObjectUtil.isNull(ordersSn), OrdersFundSummary::getOrdersSn, ordersSn);
        if (StringUtils.isNotEmpty(teamId)) {
            Team team = teamService.getOne(new QueryWrapper<Team>().lambda().eq(Team::getLeaderId, teamId));
            lqw.apply("  uid in (select uid from eb_team_user where tid = " + team.getId() + ") ");
        }



        lqw.orderByDesc(OrdersFundSummary::getId);
        Page<OrdersFundSummary> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(lqw));
    }

    @Override
    public OrdersFundSummary create(Integer ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv) {
        if (getByOrdersSn(ordersSn) != null) {
            return null;
        }
        OrdersFundSummary summary = new OrdersFundSummary(ordersId, ordersSn, payPrice, pv);
        save(summary);
        return summary;
    }

    @Override
    public OrdersFundSummary getByOrdersSn(String ordersSn) {
        return getOne(new QueryWrapper<OrdersFundSummary>().lambda().eq(OrdersFundSummary::getOrdersSn, ordersSn));
    }

    @Override
    public OrdersFundSummary increaseCommAmt(String ordersSn, BigDecimal commAmt) {
        OrdersFundSummary summary = getByOrdersSn(ordersSn);
        if (summary != null) {
            summary.setCommAmt(summary.getCommAmt().add(commAmt));
            Boolean ifSuccess = updateById(summary);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
        return summary;
    }

    @Override
    public OrdersFundSummary reduceCommAmt(String ordersSn, BigDecimal commAmt) {
        OrdersFundSummary summary = getByOrdersSn(ordersSn);
        if (summary != null) {
            summary.setCommAmt(summary.getCommAmt().subtract(commAmt));
            Boolean ifSuccess = updateById(summary);
            if (BooleanUtils.isNotTrue(ifSuccess)) {
                throw new CrmebException("当前操作人数过多");
            }
        }
        return summary;
    }
}
