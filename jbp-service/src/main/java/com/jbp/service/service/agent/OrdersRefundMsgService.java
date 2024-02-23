package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LimitTemp;
import com.jbp.common.model.agent.OrdersRefundMsg;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface OrdersRefundMsgService extends IService<OrdersRefundMsg> {
    OrdersRefundMsg create(String ordersSn, String refundSn, String context);

    void read(List<Long> ids, String remark);

    PageInfo<OrdersRefundMsg> pageList(String ordersSn, String refundSn, Boolean ifRead, PageParamRequest pageParamRequest);
}
