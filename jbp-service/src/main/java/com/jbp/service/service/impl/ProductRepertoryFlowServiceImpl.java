package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.service.dao.ProductRepertoryFlowDao;
import com.jbp.service.service.ProductRepertoryFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class ProductRepertoryFlowServiceImpl extends ServiceImpl<ProductRepertoryFlowDao, ProductRepertoryFlow> implements ProductRepertoryFlowService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryFlowService.class);

    @Resource
    private ProductRepertoryFlowDao dao;


    @Override
    public void  add(Integer uId, Integer productId, Integer count, String description, String orderSn, Date time, String type) {
        ProductRepertoryFlow productRepertoryFlow = new ProductRepertoryFlow();
        productRepertoryFlow.setUId(uId);
        productRepertoryFlow.setProductId(productId);
        productRepertoryFlow.setCount(count);
        productRepertoryFlow.setDescription(description);
        productRepertoryFlow.setOrderSn(orderSn);
        productRepertoryFlow.setTime(time);
        productRepertoryFlow.setType(type);

        save(productRepertoryFlow);
    }
}

