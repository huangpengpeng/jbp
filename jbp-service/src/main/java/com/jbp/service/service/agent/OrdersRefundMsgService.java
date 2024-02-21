package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.OrdersRefundMsg;

import java.util.List;

public interface OrdersRefundMsgService extends IService<OrdersRefundMsg> {
    OrdersRefundMsg create(String ordersSn, String refundSn, String context);

    void read(List<Long> ids, String remark);

}
