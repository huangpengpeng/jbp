package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.ProductBrandDao;
import com.jbp.service.service.ProductBrandCategoryService;
import com.jbp.service.service.ProductBrandService;
import com.jbp.service.service.ProductService;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.common.constants.RedisConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.ProductBrand;
import com.jbp.common.model.product.ProductBrandCategory;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.BrandCategorySearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ProductBrandRequest;
import com.jbp.common.response.ProductBrandListResponse;
import com.jbp.common.response.ProductBrandResponse;
import com.jbp.common.utils.RedisUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*  ProductBrandServiceImpl 接口实现
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
public class ProductBrandServiceImpl extends ServiceImpl<ProductBrandDao, ProductBrand> implements ProductBrandService {

    @Resource
    private ProductBrandDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductBrandCategoryService brandCategoryService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ProductService productService;

    /**
     * 品牌分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductBrandListResponse> getAdminPage(PageParamRequest pageParamRequest) {
        Page<ProductBrand> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ProductBrand> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductBrand::getIsDel, false);
        lqw.orderByDesc(ProductBrand::getSort);
        List<ProductBrand> brandList = dao.selectList(lqw);
        if (CollUtil.isEmpty(brandList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<ProductBrandListResponse> responseList = brandList.stream().map(e -> {
            ProductBrandListResponse response = new ProductBrandListResponse();
            BeanUtils.copyProperties(e, response);
            List<ProductBrandCategory> list = brandCategoryService.getListByBrandId(e.getId());
            if (CollUtil.isEmpty(list)) {
                response.setCategoryIds("");
            } else {
                List<String> cidList = list.stream().map(bc -> bc.getCid().toString()).collect(Collectors.toList());
                response.setCategoryIds(String.join(",", cidList));
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 添加品牌
     * @param request 添加参数
     * @return Boolean
     */
    @Override
    public Boolean add(ProductBrandRequest request) {
        validateName(request.getName());
        ProductBrand brand = new ProductBrand();
        BeanUtils.copyProperties(request, brand);
        brand.setId(null);
        if (StrUtil.isNotBlank(brand.getIcon())) {
            brand.setIcon(systemAttachmentService.clearPrefix(brand.getIcon()));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            save(brand);
            if (StrUtil.isNotBlank(request.getCategoryIds())) {
                List<ProductBrandCategory> initList = brandCategoryInit(brand.getId(), request.getCategoryIds());
                brandCategoryService.saveBatch(initList, 100);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.delete(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY);
        }
        return execute;
    }

    /**
     * 删除品牌
     * @param id 品牌ID
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        ProductBrand brand = getByIdException(id);
        // 判断商品是否使用品牌
        if (productService.isUseBrand(brand.getId())) {
            throw new CrmebException("该商品使用该品牌，无法删除");
        }
        brand.setIsDel(true);
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(brand);
            brandCategoryService.deleteByBid(brand.getId());
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.delete(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY);
        }
        return execute;
    }

    /**
     * 编辑品牌
     * @param request 修改参数
     * @return Boolean
     */
    @Override
    public Boolean edit(ProductBrandRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("品牌id不能为空");
        }
        ProductBrand oldBrand = getByIdException(request.getId());
        if (!oldBrand.getName().equals(request.getName())) {
            validateName(request.getName());
        }
        ProductBrand brand = new ProductBrand();
        BeanUtils.copyProperties(request, brand);
        if (StrUtil.isNotBlank(brand.getIcon())) {
            brand.setIcon(systemAttachmentService.clearPrefix(brand.getIcon()));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(brand);
            brandCategoryService.deleteByBid(brand.getId());
            if (StrUtil.isNotBlank(request.getCategoryIds())) {
                List<ProductBrandCategory> initList = brandCategoryInit(brand.getId(), request.getCategoryIds());
                brandCategoryService.saveBatch(initList, 100);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.delete(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY);
        }
        return execute;
    }

    /**
     * 修改品牌显示状态
     * @param id 品牌ID
     * @return Boolean
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        ProductBrand brand = getByIdException(id);
        brand.setIsShow(!brand.getIsShow());
        return updateById(brand);
    }

    /**
     * 品牌缓存列表(全部)
     * @return List
     */
    @Override
    public List<ProductBrandResponse> getCacheAllList() {
        if (redisUtil.exists(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY)) {
            return redisUtil.get(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY);
        }
        LambdaQueryWrapper<ProductBrand> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductBrand::getIsDel, false);
        lqw.orderByDesc(ProductBrand::getSort);
        List<ProductBrand> brandList = dao.selectList(lqw);
        if (CollUtil.isEmpty(brandList)) {
            return CollUtil.newArrayList();
        }
        List<ProductBrandResponse> responseList = brandList.stream().map(e -> {
            ProductBrandResponse brandResponse = new ProductBrandResponse();
            BeanUtils.copyProperties(e, brandResponse);
            return brandResponse;
        }).collect(Collectors.toList());
        redisUtil.set(RedisConstants.PRODUCT_ALL_BRAND_LIST_KEY, responseList);
        return responseList;
    }

    /**
     * 通过分类查询品牌分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductBrandResponse> getPageListByCategory(BrandCategorySearchRequest request, PageParamRequest pageParamRequest) {
        Page<ProductBrand> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("cid", request.getCid());
        if (StrUtil.isNotBlank(request.getBrandName())) {
            map.put("brandName", request.getBrandName());
        }
        List<ProductBrand> brandList = dao.getPageListByCategory(map);
        if (CollUtil.isEmpty(brandList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<ProductBrandResponse> responseList = brandList.stream().map(e -> {
            ProductBrandResponse brandResponse = new ProductBrandResponse();
            BeanUtils.copyProperties(e, brandResponse);
            return brandResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    private ProductBrand getByIdException(Integer id) {
        ProductBrand brand = getById(id);
        if (ObjectUtil.isNull(brand) || brand.getIsDel()) {
            throw new CrmebException("品牌不存在");
        }
        return brand;
    }

    /**
     * 初始化品牌分类关联
     * @param categoryIds 分类ids
     * @return List<ProductBrandCategory>
     */
    private List<ProductBrandCategory> brandCategoryInit(Integer bid, String categoryIds) {
        String[] split = categoryIds.split(",");
        return Arrays.stream(split).map(e -> {
            ProductBrandCategory brandCategory = new ProductBrandCategory();
            brandCategory.setBid(bid);
            brandCategory.setCid(Integer.valueOf(e));
            return brandCategory;
        }).collect(Collectors.toList());
    }

    /**
     * 校验品牌名是否重复
     * @param name 品牌名
     */
    private void validateName(String name) {
        LambdaQueryWrapper<ProductBrand> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductBrand::getIsDel, false);
        lqw.eq(ProductBrand::getName, name);
        lqw.last(" limit 1");
        ProductBrand brand = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(brand)) {
            throw new CrmebException("品牌已存在");
        }
    }
}

