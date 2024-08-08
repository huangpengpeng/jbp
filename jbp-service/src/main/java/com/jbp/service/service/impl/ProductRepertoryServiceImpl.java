package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.service.dao.ProductRepertoryDao;
import com.jbp.service.service.ProductRepertoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ProductRepertoryServiceImpl extends ServiceImpl<ProductRepertoryDao, ProductRepertory> implements ProductRepertoryService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryService.class);

    @Resource
    private ProductRepertoryDao dao;


}

