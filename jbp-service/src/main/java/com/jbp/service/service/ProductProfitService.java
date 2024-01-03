package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.product.ProductProfit;

import java.util.List;

public interface ProductProfitService extends IService<ProductProfit> {

    void add(ProductProfit productProfit);

    List<ProductProfit> getByProduct(List<Integer> productIdList);


}
