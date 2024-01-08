package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderRegister;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductBuyLimitTemp;

import java.util.List;

public interface ProductBuyLimitTempService extends IService<ProductBuyLimitTemp> {

    ProductBuyLimitTemp add(String name, List<Long> capaIdList, List<Long> capaXsIdList,
                            List<Long> whiteIdList, List<Long> teamIdList,
                            Boolean hasPartner,
                            List<Long> pCapaIdList, Boolean hasRelation,
                            List<Long> pCapaXsIdList);

    void valid(Integer uId, Product product, OrderRegister orderRegister);



}
