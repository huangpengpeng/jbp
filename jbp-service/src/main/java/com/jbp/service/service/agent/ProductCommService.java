package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ProductComm;

import java.math.BigDecimal;
import java.util.List;

public interface ProductCommService extends IService<ProductComm> {

    ProductComm getByProduct(Integer productId, Integer type);

    List<ProductComm> getByProduct(Integer productId);

    void deleteByProduct(Integer productId);

    void deleteByProduct(Integer productId, Integer type);

    Boolean save(Integer productId, Integer type, String name, BigDecimal scale, String rule);

}
