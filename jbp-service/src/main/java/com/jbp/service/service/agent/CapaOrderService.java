package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaOrderRequest;


public interface CapaOrderService extends IService<CapaOrder> {

    CapaOrder getByCapaId(Integer capaId);

    PageInfo<CapaOrder> getList(PageParamRequest pageParamRequest);

    Boolean edit(CapaOrderRequest capaRequest);

    CapaOrder getCapaOrderByUser(Integer capaId);
}
