package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.product.ProductExtConfig;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ProductExtConfigAddRequest;

public interface ProductExtConfigService extends IService<ProductExtConfig> {
    PageInfo<ProductExtConfig> pageList(PageParamRequest pageParamRequest);

    Boolean add(ProductExtConfigAddRequest request);

    Boolean del(Long id);
}
