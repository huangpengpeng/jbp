package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.OrdersFundSummary;
import com.jbp.common.model.agent.OrdersRefundMsg;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrdersFundSummaryRequest;
import com.jbp.common.response.OrdersFundSummaryExtResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrdersFundSummaryService extends IService<OrdersFundSummary> {

    OrdersFundSummary create(Integer ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv);

    OrdersFundSummary getByOrdersSn(String ordersSn);
    OrdersFundSummary increaseCommAmt(String ordersSn, BigDecimal commAmt);

    OrdersFundSummary reduceCommAmt(String ordersSn, BigDecimal commAmt);

    PageInfo<OrdersFundSummaryExtResponse> pageList(String ordersSn, String teamId, String startPayTime, String endPayTime, PageParamRequest pageParamRequest);

    String export(OrdersFundSummaryRequest request);
}
