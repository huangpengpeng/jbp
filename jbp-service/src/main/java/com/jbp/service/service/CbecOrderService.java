package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.CbecOrderSyncDTO;
import com.jbp.common.model.order.CbecOrder;
import com.jbp.common.request.agent.CbecOrderSyncRequest;


public interface CbecOrderService extends IService<CbecOrder> {

    void orderSync(CbecOrderSyncDTO dto);

    Long save(CbecOrderSyncRequest request);
}
