package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.OrdersFundSummary;
import com.jbp.service.dao.agent.OrdersFundSummaryDao;
import com.jbp.service.service.agent.OrdersFundSummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrdersFundSummaryServiceImpl extends ServiceImpl<OrdersFundSummaryDao, OrdersFundSummary> implements OrdersFundSummaryService {

    @Override
    public OrdersFundSummary create(Long ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv) {
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
        if(summary != null){
            summary.setCommAmt(summary.getCommAmt().add(commAmt));
            updateById(summary);
        }
        return summary;
    }

    @Override
    public OrdersFundSummary reduceCommAmt(String ordersSn, BigDecimal commAmt) {
        OrdersFundSummary summary = getByOrdersSn(ordersSn);
        if(summary != null){
            summary.setCommAmt(summary.getCommAmt().subtract(commAmt));
            updateById(summary);
        }
        return summary;
    }

}
