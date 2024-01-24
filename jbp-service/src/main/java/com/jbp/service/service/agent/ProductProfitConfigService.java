package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface ProductProfitConfigService extends IService<ProductProfitConfig> {

    ProductProfitConfig add(Integer type, String name, String description);

    ProductProfitConfig getByType(Integer type);

    PageInfo<ProductProfitConfig> pageList(PageParamRequest pageParamRequest);

    Boolean open(Integer type);

    Boolean close(Integer type);

    List<ProductProfitConfig> getOpenList();


}
