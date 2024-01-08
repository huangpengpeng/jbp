package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.service.dao.agent.ProductProfitDao;
import com.jbp.service.product.profit.ProductProfitChain;
import com.jbp.service.service.agent.ProductProfitService;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class ProductProfitServiceImpl extends ServiceImpl<ProductProfitDao, ProductProfit> implements ProductProfitService {

    @Resource
    private ProductProfitChain productProfitChain;
    @Override
    public void add(ProductProfit productProfit) {
        if (productProfit.hasError()) {
            throw new CrmebException("请填写完整的商品收益信息");
        }
       productProfitChain.save(productProfit);
    }

    @Override
    public List<ProductProfit> getByProduct(List<Integer> productIdList) {
        if (CollectionUtils.isEmpty(productIdList)) {
            return Lists.newArrayList();
        }
        return list(new QueryWrapper<ProductProfit>().lambda().in(ProductProfit::getProductId, productIdList));
    }
}
