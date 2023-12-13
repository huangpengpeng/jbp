package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.ProductBrandCategoryDao;
import com.jbp.service.service.ProductBrandCategoryService;
import com.jbp.common.model.product.ProductBrandCategory;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  ProductBrandCategoryServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class ProductBrandCategoryServiceImpl extends ServiceImpl<ProductBrandCategoryDao, ProductBrandCategory> implements ProductBrandCategoryService {

    @Resource
    private ProductBrandCategoryDao dao;

    /**
     * 通过品牌id删除
     * @param bid 品牌id
     * @return Boolean
     */
    @Override
    public Boolean deleteByBid(Integer bid) {
        LambdaUpdateWrapper<ProductBrandCategory> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ProductBrandCategory::getBid, bid);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 获取关联的分类id
     * @param brandId 品牌id
     * @return List
     */
    @Override
    public List<ProductBrandCategory> getListByBrandId(Integer brandId) {
        LambdaQueryWrapper<ProductBrandCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductBrandCategory::getBid, brandId);
        return dao.selectList(lqw);
    }

    /**
     * 是否存在分类
     * @param categoryId 分类id
     * @return Boolean
     */
    @Override
    public Boolean isExistCategory(Integer categoryId) {
        LambdaQueryWrapper<ProductBrandCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductBrandCategory::getCid, categoryId);
        lqw.last(" limit 1");
        ProductBrandCategory productBrandCategory = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(productBrandCategory);
    }
}

