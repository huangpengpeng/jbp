package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.excel.ProductRepertoryExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ProductRepertoryRequest;
import com.jbp.common.vo.FileResultVo;
import com.jbp.common.vo.ProductRepertoryVo;
import com.jbp.service.dao.ProductRepertoryDao;
import com.jbp.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ProductRepertoryServiceImpl extends ServiceImpl<ProductRepertoryDao, ProductRepertory> implements ProductRepertoryService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepertoryService.class);

    @Resource
    private ProductRepertoryDao dao;
    @Resource
    private ProductRepertoryFlowService productRepertoryFlowService;
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private UploadService uploadService;

    @Override
    public ProductRepertory add(Integer productId, Integer count, Integer uId) {
        ProductRepertory productRepertory = new ProductRepertory();
        productRepertory.setProductId(productId);
        productRepertory.setCount(count);
        productRepertory.setUid(uId);
        save(productRepertory);
        return productRepertory;
    }

    @Override
    public Boolean reduce(Integer productId, Integer count, Integer uId, String description, String orderSn, String type) {
        ProductRepertory productRepertory = dao.selectOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, productId).eq(ProductRepertory::getUid, uId));

        if (productRepertory.getCount() - count < 0) {
            throw new CrmebException("库存不足，无法扣减");
        }

        productRepertory.setCount(productRepertory.getCount() - count);
        boolean ifSuccess = updateById(productRepertory);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }

        productRepertoryFlowService.add(uId, productId, -count, description, orderSn, new Date(), type, productRepertory.getCount());

        return ifSuccess;
    }

    @Override
    public Boolean increase(Integer productId, Integer count, Integer uId, String description, String orderSn, String type) {

        ProductRepertory productRepertory = dao.selectOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, productId).eq(ProductRepertory::getUid, uId));
        if (productRepertory == null) {
            productRepertory = add(productId, 0, uId);
        }
        productRepertory.setCount(productRepertory.getCount() + count);
        boolean ifSuccess = updateById(productRepertory);
        if (BooleanUtils.isNotTrue(ifSuccess)) {
            throw new CrmebException("当前操作人数过多");
        }

        productRepertoryFlowService.add(uId, productId, count, description, orderSn, new Date(), type, productRepertory.getCount());

        return ifSuccess;
    }

    @Override
    public PageInfo<ProductRepertory> getList(Integer uid, String nickname, String productNameOrCode, PageParamRequest pageParamRequest) {
        Page<ProductRepertory> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ProductRepertory> list = dao.getList(uid, nickname, productNameOrCode);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean allot(Integer fUid, Integer tUid, Integer productId, Integer count) {
        Product product = productService.getById(productId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在！");
        }
        if (count < 0) {
            throw new CrmebException("数量输入有误！");
        }
        User fUser = userService.getById(fUid);
        User tUser = userService.getById(tUid);
        String description = fUser.getNickname() + "调拨给" + tUser.getNickname();
        reduce(productId, count, fUid, description, "", "供货");
        increase(productId, count, tUid, description, "", "订货");
        return true;
    }

    @Override
    public String export(Integer uid, String nickname, String productNameOrCode) {
        List<ProductRepertory> list = dao.getList(uid, nickname, productNameOrCode);
        if (CollUtil.isEmpty(list)) {
            throw new CrmebException("未查询到库存管理数据！");
        }
        List<ProductRepertoryExcel> result = new LinkedList<>();
        list.forEach(e -> {
            ProductRepertoryExcel vo = new ProductRepertoryExcel();
            BeanUtils.copyProperties(e, vo);
            result.add(vo);
        });
        FileResultVo fileResultVo = uploadService.excelLocalUpload(result, ProductRepertoryExcel.class);
        log.info("库存管理导出下载地址:" + fileResultVo.getUrl());
        return fileResultVo.getUrl();
    }

    @Override
    public List<ProductRepertory> getUserRepertory(Integer uid) {
        if (ObjectUtil.isNull(uid)) {
            throw new CrmebException("用户账号不能为空");
        }
        return dao.getList(uid, "", "");
    }

    @Override
    public List<ProductRepertoryVo> getProductList(Integer uid) {
        List<ProductRepertoryVo> list = new ArrayList<>();
        List<ProductRepertory> productRepertoryList = dao.selectList(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getUid, uid));

        for (ProductRepertory productRepertory : productRepertoryList) {
            ProductRepertoryVo productRepertoryVo = new ProductRepertoryVo();
            Product product = productService.getById(productRepertory.getProductId());
            productRepertoryVo.setName(product.getName());
            productRepertoryVo.setCount(productRepertory.getCount());
            productRepertoryVo.setPicUrl(product.getImage());
            productRepertoryVo.setId(product.getId());
            list.add(productRepertoryVo);
        }

        return list;
    }

    @Override
    public void allot(List<ProductRepertoryRequest> request) {

        for (ProductRepertoryRequest productRepertoryRequest : request) {
            List<User> users = userService.getByPhone(productRepertoryRequest.getPhone());
            if (users.isEmpty()) {
                throw new CrmebException("用户手机号不存在");
            }
            reduce(productRepertoryRequest.getProductId(), productRepertoryRequest.getCount(), userService.getUserId(), "调拨给" + users.get(0).getAccount(), "", "调拨");
            increase(productRepertoryRequest.getProductId(), productRepertoryRequest.getCount(), users.get(0).getId(), userService.getAccount() + "调拨接收", "", "调拨");
        }
    }

}

