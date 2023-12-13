package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.MerchantCategoryDao;
import com.jbp.service.service.MerchantCategoryService;
import com.jbp.service.service.MerchantService;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.merchant.MerchantCategory;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.MerchantCategoryRequest;
import com.jbp.common.request.PageParamRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreCategoryServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class MerchantCategoryServiceImpl extends ServiceImpl<MerchantCategoryDao, MerchantCategory> implements MerchantCategoryService {

    @Resource
    private MerchantCategoryDao dao;

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取商户分类分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantCategory> getAdminPage(PageParamRequest pageParamRequest) {
        Page<MerchantCategory> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantCategory::getIsDel, false);
        lqw.orderByDesc(MerchantCategory::getId);
        List<MerchantCategory> categoryList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, categoryList);
    }

    /**
     * 添加商户分类
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean add(MerchantCategoryRequest request) {
        if (checkName(request.getName())) {
            throw new CrmebException("分类名称重复");
        }
        MerchantCategory merchantCategory = new MerchantCategory();
        BeanUtils.copyProperties(request, merchantCategory);
        merchantCategory.setId(null);
        return dao.insert(merchantCategory) > 0;
    }

    /**
     * 编辑商户分类
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean edit(MerchantCategoryRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("分类ID不能为空");
        }
        MerchantCategory category = getByIdException(request.getId());
        if (!category.getName().equals(request.getName())) {
            if (checkName(request.getName())) {
                throw new CrmebException("分类名称重复");
            }
        }
        category.setName(request.getName());
        category.setHandlingFee(request.getHandlingFee());
        return dao.updateById(category) > 0;
    }

    /**
     * 删除商户分类
     * @param id 商户分类ID
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        MerchantCategory category = getByIdException(id);
        if (merchantService.isExistCategory(category.getId())) {
            throw new CrmebException("有商户使用该分类，请先修改商户");
        }
        category.setIsDel(true);
        return dao.updateById(category) > 0;
    }

    /**
     * 全部商户列表
     * @return List
     */
    @Override
    public List<MerchantCategory> allList() {
        LambdaQueryWrapper<MerchantCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantCategory::getId, MerchantCategory::getName, MerchantCategory::getHandlingFee);
        lqw.eq(MerchantCategory::getIsDel, false);
        lqw.orderByDesc(MerchantCategory::getId);
        return dao.selectList(lqw);
    }

    /**
     * 校验名称是否唯一
     * @param name 分类名称
     */
    private Boolean checkName(String name) {
        LambdaQueryWrapper<MerchantCategory> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantCategory::getId);
        lqw.eq(MerchantCategory::getName, name);
        lqw.eq(MerchantCategory::getIsDel, false);
        lqw.last(" limit 1");
        MerchantCategory merchantCategory = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantCategory);
    }

    /**
     * 通过id获取并抛出异常
     * @param id 分类ID
     * @return MerchantCategory
     */
    private MerchantCategory getByIdException(Integer id) {
        MerchantCategory category = getById(id);
        if (ObjectUtil.isNull(category) || category.getIsDel()) {
            throw new CrmebException("分类不存在");
        }
        return category;
    }
}

