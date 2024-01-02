package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.product.ProductComm;
import com.jbp.service.dao.ProductCommDao;
import com.jbp.service.service.ProductCommService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品佣金
 */
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
