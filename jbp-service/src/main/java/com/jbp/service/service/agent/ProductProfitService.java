package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ProductProfit;

import java.util.List;

public interface ProductProfitService extends IService<ProductProfit> {

    void edit(ProductProfit productProfit);

    List<ProductProfit> getByProduct(List<Integer> productIdList);

    List<ProductProfit> getByProduct(Integer productId);


    ProductProfit getByProductName(Integer productId,String name);
}
