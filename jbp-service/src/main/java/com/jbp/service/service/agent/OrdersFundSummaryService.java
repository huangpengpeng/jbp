package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.OrdersFundSummary;

import java.math.BigDecimal;

public interface OrdersFundSummaryService extends IService<OrdersFundSummary> {

    OrdersFundSummary create(Long ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv);

    OrdersFundSummary getByOrdersSn(String ordersSn);
    OrdersFundSummary increaseCommAmt(String ordersSn, BigDecimal commAmt);

    OrdersFundSummary reduceCommAmt(String ordersSn, BigDecimal commAmt);
}
