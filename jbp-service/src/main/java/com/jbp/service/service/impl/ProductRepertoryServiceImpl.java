package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.service.dao.ProductRepertoryDao;
import com.jbp.service.service.ProductRepertoryFlowService;
import com.jbp.service.service.ProductRepertoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductRepertoryServiceImpl extends ServiceImpl<ProductRepertoryDao, ProductRepertory> implements ProductRepertoryService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryService.class);

    @Resource
    private ProductRepertoryDao dao;
    @Resource
    private ProductRepertoryFlowService productRepertoryFlowService;


    @Override
    public ProductRepertory add(Integer productId, Integer count, Integer uId) {
        ProductRepertory productRepertory = new ProductRepertory();
        productRepertory.setProductId(productId);
        productRepertory.setCount(count);
        productRepertory.setUId(uId);
        save(productRepertory);
        return productRepertory;
    }


    @Override
    public Boolean saveToUpdate(Integer productId, Integer count, Integer uId, String description, String orderSn, String type) {

        ProductRepertory productRepertory = dao.selectOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, productId).eq(ProductRepertory::getUId, uId));
        if (productRepertory == null) {
            productRepertory = add(productId, 0, uId);
        }
        productRepertory.setCount(productRepertory.getCount() + count);
        boolean ifSuccess = updateById(productRepertory);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }

        productRepertoryFlowService.add(uId, productId, count, description, orderSn, new Date(), type);

        return ifSuccess;
    }
}

