package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.ProductProfitConfig;

public interface ProductProfitConfigDao extends BaseMapper<ProductProfitConfig> {

    ProductProfitConfig add();
}