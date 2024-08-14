package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.common.request.PageParamRequest;

import java.util.Date;

public interface ProductRepertoryFlowService extends IService<ProductRepertoryFlow> {


    void add(Integer uId, Integer productId, Integer count, String description, String orderSn, Date time,String type);

    PageInfo<ProductRepertoryFlow> getList(Integer uid, String nickname, PageParamRequest pageParamRequest);
}
