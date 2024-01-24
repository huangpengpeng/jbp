package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface ProductCommConfigService extends IService<ProductCommConfig> {

    ProductCommConfig add(Integer type, String name, String desc);

    ProductCommConfig getByType(Integer type);

    PageInfo<ProductCommConfig> pageList(PageParamRequest pageParamRequest);

    Boolean open(Integer type);

    Boolean close(Integer type);

    List<ProductCommConfig> getOpenList();
}
