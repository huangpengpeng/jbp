package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ProductSupply;
import com.jbp.common.request.agent.ProductSupplyAddRequest;

import java.util.List;

public interface ProductSupplyService extends IService<ProductSupply> {

    Boolean add(ProductSupplyAddRequest request);


}
