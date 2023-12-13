package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.merchant.MerchantType;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.merchant.MerchantTypeRequest;
import com.jbp.service.dao.MerchantTypeDao;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.MerchantTypeService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreTypeServiceImpl 接口实现
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
public class MerchantTypeServiceImpl extends ServiceImpl<MerchantTypeDao, MerchantType> implements MerchantTypeService {

    @Resource
    private MerchantTypeDao dao;

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取商户类型分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantType> getAdminPage(PageParamRequest pageParamRequest) {
        Page<MerchantType> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantType> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantType::getIsDel, false);
        lqw.orderByDesc(MerchantType::getId);
        List<MerchantType> categoryList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, categoryList);
    }

    /**
     * 添加商户类型
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean add(MerchantTypeRequest request) {
        if (checkName(request.getName())) {
            throw new CrmebException("分类名称重复");
        }
        MerchantType merchantType = new MerchantType();
        BeanUtils.copyProperties(request, merchantType);
        merchantType.setId(null);
        return dao.insert(merchantType) > 0;
    }

    /**
     * 编辑商户类型
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean edit(MerchantTypeRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("分类ID不能为空");
        }
        MerchantType merchantType = getByIdException(request.getId());
        if (!merchantType.getName().equals(request.getName())) {
            if (checkName(request.getName())) {
                throw new CrmebException("分类名称重复");
            }
        }
        merchantType.setName(request.getName());
        merchantType.setInfo(request.getInfo());
        return dao.updateById(merchantType) > 0;
    }

    /**
     * 删除商户类型
     * @param id 商户类型ID
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        MerchantType merchantType = getByIdException(id);
        if (merchantService.isExistType(merchantType.getId())) {
            throw new CrmebException("有商户使用该类型，请先修改商户");
        }
        merchantType.setIsDel(true);
        return dao.updateById(merchantType) > 0;
    }

    /**
     * 全部商户类型列表
     * @return List
     */
    @Override
    public List<MerchantType> allList() {
        LambdaQueryWrapper<MerchantType> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantType::getId, MerchantType::getName, MerchantType::getInfo);
        lqw.eq(MerchantType::getIsDel, false);
        lqw.orderByDesc(MerchantType::getId);
        return dao.selectList(lqw);
    }

    /**
     * 校验名称是否唯一
     * @param name 分类名称
     */
    private Boolean checkName(String name) {
        LambdaQueryWrapper<MerchantType> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantType::getId);
        lqw.eq(MerchantType::getName, name);
        lqw.eq(MerchantType::getIsDel, false);
        lqw.last(" limit 1");
        MerchantType merchantType = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantType);
    }

    /**
     * 通过id获取并抛出异常
     * @param id 分类ID
     * @return MerchantCategory
     */
    private MerchantType getByIdException(Integer id) {
        MerchantType merchantType = getById(id);
        if (ObjectUtil.isNull(merchantType) || merchantType.getIsDel()) {
            throw new CrmebException("分类不存在");
        }
        return merchantType;
    }
}

