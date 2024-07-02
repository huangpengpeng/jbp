package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductExtConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ProductExtConfigAddRequest;
import com.jbp.service.dao.ProductExtConfigDao;
import com.jbp.service.service.ProductExtConfigService;
import com.jbp.service.service.ProductService;
import com.jbp.service.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductExtConfigConfigServiceImpl extends ServiceImpl<ProductExtConfigDao, ProductExtConfig> implements ProductExtConfigService {

    @Autowired
    private ProductService productService;

    @Override
    public PageInfo<ProductExtConfig> pageList(PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ProductExtConfig> wrapper = new LambdaQueryWrapper<>();
        Page<ProductExtConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductExtConfig> list = list(wrapper);
        List<Integer> ids = list.stream().map(ProductExtConfig::getProductId).collect(Collectors.toList());
        Map<Integer, Product> mapByIdList = productService.getMapByIdList(ids);
        list.forEach(e->{
            Product product = mapByIdList.get(e.getProductId());
            e.setProductName( product== null ? "" : product.getName());
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(ProductExtConfigAddRequest request) {
        if (request.getProductId()==null || StringUtils.isEmpty(request.getContent())) {
            throw new CrmebException("条码或内容不能为空");
        }
        Product product = productService.getById(request.getProductId());
        if (product==null){
            throw new CrmebException("商品id不存在");
        }
        ProductExtConfig config=ProductExtConfig.builder().productId(product.getId()).content(request.getContent()).type(request.getType()).build();
        return save(config);
    }

    @Override
    public Boolean del(Long id) {
        ProductExtConfig config = getById(id);
        if (config == null){
            throw new CrmebException("商品扩展信息不存在");
        }
        return removeById(id);
    }
}
