package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.product.ProductRef;
import com.jbp.common.request.agent.ProductRefRequest;

import java.util.List;

public interface ProductRefService extends IService<ProductRef> {
    Boolean add(ProductRefRequest request);

    List<ProductRef> getList(Integer productId);
}
