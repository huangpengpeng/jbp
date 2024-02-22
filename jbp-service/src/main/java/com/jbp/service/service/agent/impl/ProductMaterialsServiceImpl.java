package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ProductMaterialsDao;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.ProductMaterialsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductMaterialsServiceImpl extends ServiceImpl<ProductMaterialsDao, ProductMaterials> implements ProductMaterialsService {
    @Resource
    private MerchantService merchantService;

    @Override
    public PageInfo<ProductMaterials> pageList(Integer merId, String materialsName, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>()
                .eq(!ObjectUtil.isNull(merId), ProductMaterials::getMerId, merId)
                .eq(!ObjectUtil.isNull(materialsName) && !materialsName.equals(""), ProductMaterials::getMaterialsName, materialsName);
        Page<ProductMaterials> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductMaterials> list = list(lqw);
        list.forEach(e -> {
            Merchant merchant = merchantService.getById(e.getMerId());
            e.setMerName(merchant.getName());
        });

        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(Integer merId, String barCode, String materialsName, Integer materialsQuantity, BigDecimal materialsPrice, String materialsCode) {
        addrule(merId, barCode, materialsCode);
        ProductMaterials productMaterials = new ProductMaterials(merId, barCode, materialsName, materialsQuantity, materialsPrice, materialsCode);
        save(productMaterials);
        return true;
    }

    @Override
    public List<ProductMaterials> getByBarCode(String barCode) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>()
                .eq(!ObjectUtil.isNull(barCode), ProductMaterials::getBarCode, barCode);
        return list(lqw);
    }

    public Boolean addrule(Integer merId, String barCode, String materialsCode) {
        LambdaQueryWrapper<ProductMaterials> lqw = new LambdaQueryWrapper<ProductMaterials>();
        lqw.eq(ProductMaterials::getMerId, merId);
        lqw.eq(ProductMaterials::getBarCode, barCode);
        lqw.eq(ProductMaterials::getMaterialsCode, materialsCode);
        if (list(lqw).size() > 0) {
            throw new CrmebException("已存在");
        }
        return true;
    }
}
