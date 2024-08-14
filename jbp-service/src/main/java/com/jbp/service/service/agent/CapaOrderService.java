package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.CapaOrder;

public interface CapaOrderService extends IService<CapaOrder> {

    CapaOrder getByCapaId(Integer capaId);
}
