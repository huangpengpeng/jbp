package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ProductMaterialsDao;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.ProductMaterialsService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductMaterialsServiceImpl extends ServiceImpl<ProductMaterialsDao, ProductMaterials> implements ProductMaterialsService {
    @Resource
    private MerchantService merchantService;

    @Override
    public PageInfo<ProductMaterials> pageList(Integer merId, String materialsName, String barCode, String supplyName, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>()
                .eq(!ObjectUtil.isNull(merId), ProductMaterials::getMerId, merId)
                .like(StringUtils.isNotEmpty(materialsName), ProductMaterials::getMaterialsName, materialsName)
                .like(StringUtils.isNotEmpty(barCode), ProductMaterials::getBarCode, barCode)
                .eq(StringUtils.isNotEmpty(supplyName), ProductMaterials::getSupplyName, supplyName)
                .orderByDesc(ProductMaterials::getId);
        Page<ProductMaterials> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductMaterials> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        list.forEach(e -> {
            Merchant merchant = merchantService.getById(e.getMerId());
            e.setMerName(merchant != null ? merchant.getName() : "");
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(Integer merId, String barCode, String materialsName, Integer materialsQuantity, BigDecimal materialsPrice, String materialsCode, String supplyName) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductMaterials::getMerId, merId);
        lqw.eq(ProductMaterials::getBarCode, barCode);
        lqw.eq(ProductMaterials::getMaterialsCode, materialsCode);
        if (list(lqw).size() > 0) {
            throw new CrmebException("已存在");
        }
        ProductMaterials productMaterials = new ProductMaterials(merId, barCode, materialsName, materialsQuantity, materialsPrice, materialsCode, supplyName);
        save(productMaterials);
        return true;
    }

    @Override
    public List<ProductMaterials> getByBarCode(Integer merId, String barCode) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>()
                .eq(ProductMaterials::getMerId, merId)
                .eq(ProductMaterials::getBarCode, barCode);
        return list(lqw);
    }

    @Override
    public List<String> getBarCodeList4Supply(String supplyName) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>()
                .eq(ProductMaterials::getSupplyName, supplyName);
        List<ProductMaterials> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(ProductMaterials::getBarCode).collect(Collectors.toSet()).stream().collect(Collectors.toList());
    }

    @Override
    public Boolean edit(Integer merId, String barCode, String materialsName, Integer materialsQuantity, BigDecimal materialsPrice, String materialsCode, String supplyName, Long id) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("id不能为空");
        }
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductMaterials::getMerId, merId);
        lqw.eq(ProductMaterials::getBarCode, barCode);
        lqw.eq(ProductMaterials::getMaterialsCode, materialsCode);
        lqw.ne(ProductMaterials::getId, id);
        if (!list(lqw).isEmpty()) {
            throw new CrmebException("已存在");
        }
        ProductMaterials productMaterials = getById(id);
        if (productMaterials == null) {
            throw new CrmebException("该物料不存在");
        }
        productMaterials.setMerId(merId);
        productMaterials.setBarCode(barCode);
        productMaterials.setMerName(materialsName);
        productMaterials.setMaterialsQuantity(materialsQuantity);
        productMaterials.setMaterialsPrice(materialsPrice);
        productMaterials.setMaterialsCode(materialsCode);
        productMaterials.setSupplyName(supplyName);
        return updateById(productMaterials);
    }
}
