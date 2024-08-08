package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.service.dao.ProductRepertoryFlowDao;
import com.jbp.service.service.ProductRepertoryFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ProductRepertoryFlowServiceImpl extends ServiceImpl<ProductRepertoryFlowDao, ProductRepertoryFlow> implements ProductRepertoryFlowService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryFlowService.class);

    @Resource
    private ProductRepertoryFlowDao dao;


}

