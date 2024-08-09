package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.product.ProductRepertory;

public interface ProductRepertoryService extends IService<ProductRepertory> {


    public Boolean increase(Integer productId,Integer count,Integer uId,String description ,String orderSn,String type);

    public ProductRepertory add(Integer productId, Integer count, Integer uId);

    public Boolean reduce(Integer productId,Integer count,Integer uId,String description ,String orderSn,String type);

}
