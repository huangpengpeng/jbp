package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.service.dao.agent.ProductCommDao;
import com.jbp.service.service.agent.ProductCommService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品佣金
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductCommServiceImpl extends ServiceImpl<ProductCommDao, ProductComm>implements ProductCommService {


    @Override
    public ProductComm getByProduct(Integer productId, Integer type) {
        return getOne(new QueryWrapper<ProductComm>().lambda().eq(ProductComm::getProductId, productId).eq(ProductComm::getType, type));
    }

    @Override
    public List<ProductComm> getByProduct(Integer productId) {
        return list(new QueryWrapper<ProductComm>().lambda().eq(ProductComm::getProductId, productId));
    }

    @Override
    public void deleteByProduct(Integer productId) {
        remove(new LambdaQueryWrapper<ProductComm>().eq(ProductComm::getProductId, productId));
    }

    @Override
    public void deleteByProduct(Integer productId, Integer type) {
        remove(new LambdaQueryWrapper<ProductComm>().eq(ProductComm::getProductId, productId).eq(ProductComm::getType, type));
    }

    @Override
    public Boolean save(Integer productId, Integer type, String name, BigDecimal scale, String rule) {



        return null;
    }
}
