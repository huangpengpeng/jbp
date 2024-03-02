package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.product.ProductDescription;
import com.jbp.service.dao.ProductDescriptionDao;
import com.jbp.service.service.ProductDescriptionService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ProductDescriptionServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ProductDescriptionServiceImpl extends ServiceImpl<ProductDescriptionDao, ProductDescription> implements ProductDescriptionService {

    @Resource
    private ProductDescriptionDao dao;

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    @Override
    public void deleteByProductId(int productId,int type) {
        LambdaQueryWrapper<ProductDescription> lmq = Wrappers.lambdaQuery();
        lmq.eq(ProductDescription::getProductId, productId).eq(ProductDescription::getType,type);
        dao.delete(lmq);
    }

    /**
     * 获取详情
     * @param productId 商品id
     * @param type 商品类型
     * @return ProductDescription
     */
    @Override
    public ProductDescription getByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<ProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductDescription::getProductId, productId);
        lqw.eq(ProductDescription::getType,type);
        return dao.selectOne(lqw);
    }

    @Override
    public ProductDescription getByProductId(Integer productId) {
        LambdaQueryWrapper<ProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductDescription::getProductId, productId);
        return dao.selectOne(lqw);
    }
}

