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

    public Boolean addrule(Integer merId, String barCode, String materialsCode) {
        LambdaQueryWrapper<ProductMaterials> lqwMerId = new LambdaQueryWrapper<ProductMaterials>();
        lqwMerId.eq(ProductMaterials::getMerId, merId);
        if (list(lqwMerId).size() > 0) {
            throw new CrmebException("商户已存在");
        }
        LambdaQueryWrapper<ProductMaterials> lqwBarCode = new LambdaQueryWrapper<ProductMaterials>();
        lqwBarCode.eq(ProductMaterials::getBarCode, barCode);
        if (list(lqwBarCode).size() > 0) {
            throw new CrmebException("商品条码已存在");
        }
        LambdaQueryWrapper<ProductMaterials> lqwMaterialsCode = new LambdaQueryWrapper<ProductMaterials>();
        lqwMaterialsCode.eq(ProductMaterials::getMaterialsCode, materialsCode);
        if (list(lqwMaterialsCode).size() > 0) {
            throw new CrmebException("物料编码已存在");
        }
        return true;
    }
}
