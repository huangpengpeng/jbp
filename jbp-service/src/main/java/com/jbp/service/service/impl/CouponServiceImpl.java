package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.CouponDao;
import com.jbp.service.service.*;
import com.jbp.common.constants.CouponConstants;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.model.coupon.CouponProduct;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.CouponFrontResponse;
import com.jbp.common.response.CouponInfoResponse;
import com.jbp.common.response.ProductCouponUseResponse;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.CouponSimpleVo;
import com.jbp.common.vo.SimpleProductVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CouponServiceImpl 接口实现
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
public class CouponServiceImpl extends ServiceImpl<CouponDao, Coupon> implements CouponService {

    @Resource
    private CouponDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private UserService userService;


    /**
     * 保存优惠券表
     *
     * @param request CouponRequest 新增参数
     */
    @Override
    public Boolean create(CouponRequest request) {
        if (request.getIsLimited() && (ObjectUtil.isNull(request.getTotal()) || request.getTotal().equals(0))) {
            throw new CrmebException("请输入限量数量！");
        }
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT) && (StrUtil.isBlank(request.getProductIds()))) {
            throw new CrmebException("请选择商品");
        }
        if (request.getIsTimeReceive()) {
            if (ObjectUtil.isNull(request.getReceiveStartTime()) || ObjectUtil.isNull(request.getReceiveEndTime())) {
                throw new CrmebException("请选择领取时间范围！");
            }
            int compareDate = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(request.getReceiveStartTime(), DateConstants.DATE_FORMAT), CrmebDateUtil.dateToStr(request.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            if (compareDate > -1) {
                throw new CrmebException("请选择正确的领取时间范围！");
            }
        }
        if (!request.getIsFixedTime() && (ObjectUtil.isNull(request.getDay()) || request.getDay().equals(0))) {
            throw new CrmebException("请输入天数！");
        }
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(request, coupon);
        coupon.setLastTotal(coupon.getTotal());
        coupon.setPublisher(CouponConstants.COUPON_PUBLISHER_MERCHANT);
        coupon.setCouponType(CouponConstants.COUPON_TYPE_SATISFY);

        //非固定时间, 领取后多少天
        if (!request.getIsFixedTime()) {
            coupon.setUseStartTime(null);
            coupon.setUseEndTime(null);
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        coupon.setMerId(systemAdmin.getMerId());

        List<CouponProduct> cpList = new ArrayList<>();
        if (request.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            String productIds = request.getProductIds();
            List<Integer> productIdList = CrmebUtil.stringToArray(productIds);
            productIdList.forEach(pid -> {
                CouponProduct couponProduct = new CouponProduct();
                couponProduct.setPid(pid);
                cpList.add(couponProduct);
            });
        }
        return transactionTemplate.execute(e -> {
            save(coupon);
            if (CollUtil.isNotEmpty(cpList)) {
                cpList.forEach(cp -> cp.setCid(coupon.getId()));
                couponProductService.saveBatch(cpList, 100);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取详情
     * @param id Integer id
     * @return Coupon
     */
    @Override
    public Coupon getInfoException(Integer id) {
        //获取优惠券信息
        Coupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException("优惠券信息不存在！");
        }
        return coupon;
    }

    /**
     * 优惠券详情
     *
     * @param id Integer 获取可用优惠券的商品id
     * @return CouponInfoResponse
     */
    @Override
    public CouponInfoResponse info(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());

        List<SimpleProductVo> productList = null;
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<CouponProduct> cpList = couponProductService.findByCid(coupon.getId());
            List<Integer> primaryIdList = cpList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
            productList = productService.getSimpleListInIds(primaryIdList);
        }

        CouponInfoResponse infoResponse = new CouponInfoResponse();
        BeanUtils.copyProperties(coupon, infoResponse);
        if (CollUtil.isNotEmpty(productList)) {
            infoResponse.setProductList(productList);
        }
        return infoResponse;
    }

    private Coupon getByIdAndMerIdException(Integer id, Integer merId) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.eq(Coupon::getId, id);
        lqw.eq(Coupon::getMerId, merId);
        lqw.eq(Coupon::getIsDel, false);
        lqw.last(" limit 1");
        Coupon Coupon = dao.selectOne(lqw);
        if (ObjectUtil.isNull(Coupon)) {
            throw new CrmebException("优惠券信息不存在！");
        }
        return Coupon;
    }

    /**
     * 扣减数量
     * @param id 优惠券id
     * @param num 数量
     * @param isLimited 是否限量
     */
    @Override
    public Boolean deduction(Integer id, Integer num, Boolean isLimited) {
        UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
        if (isLimited) {
            updateWrapper.setSql(StrUtil.format("last_total = last_total - {}", num));
            updateWrapper.last(StrUtil.format(" and (last_total - {} >= 0)", num));
        } else {
            updateWrapper.setSql(StrUtil.format("last_total = last_total + {}", num));
        }
        updateWrapper.eq("id", id);
        return update(updateWrapper);
    }

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());
        coupon.setIsDel(true);
        return transactionTemplate.execute(e -> {
            updateById(coupon);
            couponProductService.deleteByCid(coupon.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 移动端优惠券列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return List<CouponFrontResponse>
     */
    @Override
    public PageInfo<CouponFrontResponse> getH5List(CouponFrontSearchRequest request, PageParamRequest pageParamRequest) {
        if (ObjectUtil.isNull(request.getMerId()) && ObjectUtil.isNull(request.getProductId())) {
            throw new CrmebException("商户ID与商品ID不能都为空");
        }
        // 获取优惠券列表
        PageInfo<Coupon> pageInfo = getH5ListBySearch(request.getCategory(), request.getMerId(), request.getProductId(), pageParamRequest);
        List<Coupon> list = pageInfo.getList();
        if (ObjectUtil.isNull(list)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        //获取用户当前已领取的优惠券
        HashMap<Integer, CouponUser> couponUserMap = null;
        Integer userId = userService.getUserId();
        if (userId > 0) {
            couponUserMap = couponUserService.getMapByUserId(userId);
        }
        List<CouponFrontResponse> CouponFrontResponseArrayList = new ArrayList<>();
        for (Coupon Coupon : list) {
            CouponFrontResponse response = new CouponFrontResponse();
            BeanUtils.copyProperties(Coupon, response);
            if (userId > 0) {
                if (CollUtil.isNotEmpty(couponUserMap) && couponUserMap.containsKey(Coupon.getId())) {
                    response.setIsUse(true);
                }
            }
            // 更改使用时间格式，去掉时分秒
            response.setUseStartTimeStr(CrmebDateUtil.dateToStr(Coupon.getUseStartTime(), DateConstants.DATE_FORMAT_DATE));
            response.setUseEndTimeStr(CrmebDateUtil.dateToStr(Coupon.getUseEndTime(), DateConstants.DATE_FORMAT_DATE));
            CouponFrontResponseArrayList.add(response);
        }
        return CommonPage.copyPageInfo(pageInfo, CouponFrontResponseArrayList);
    }

    /**
     * 修改优惠券状态
     *
     * @param id 优惠券id
     */
    @Override
    public Boolean updateStatus(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(id, systemAdmin.getMerId());
        Coupon tempCoupon = new Coupon();
        tempCoupon.setId(id);
        tempCoupon.setStatus(!coupon.getStatus());
        return updateById(tempCoupon);
    }

    /**
     * 商户端优惠券分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<Coupon> getMerchantPageList(CouponSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<Coupon> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 Coupon 类的多条件查询
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Coupon::getMerId, systemAdmin.getMerId());
        lqw.eq(Coupon::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getCategory())) {
            lqw.eq(Coupon::getCategory, request.getCategory());
        }
        if (ObjectUtil.isNotNull(request.getReceiveType())) {
            lqw.eq(Coupon::getReceiveType, request.getReceiveType());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(Coupon::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getName())) {
            String name = URLUtil.decode(request.getName());
            lqw.like(Coupon::getName, name);
        }
        lqw.orderByDesc(Coupon::getSort).orderByDesc(Coupon::getId);
        List<Coupon> couponList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, couponList);
    }

    /**
     * 商品可用优惠券列表（商品创建时选择使用）
     *
     * @return List
     */
    @Override
    public List<ProductCouponUseResponse> getProductUsableList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        //带 Coupon 类的多条件查询
        LambdaQueryWrapper<Coupon> lqw = new LambdaQueryWrapper<>();
        lqw.select(Coupon::getId, Coupon::getName, Coupon::getMoney, Coupon::getMinPrice, Coupon::getIsLimited,
                Coupon::getLastTotal, Coupon::getIsFixedTime, Coupon::getUseStartTime, Coupon::getUseEndTime, Coupon::getDay);
        lqw.eq(Coupon::getMerId, systemAdmin.getMerId());
        lqw.eq(Coupon::getIsDel, false);
        lqw.eq(Coupon::getReceiveType, CouponConstants.COUPON_RECEIVE_TYPE_PAY_PRODUCT);
        lqw.eq(Coupon::getStatus, 1);
        lqw.orderByDesc(Coupon::getSort).orderByDesc(Coupon::getId);
        List<Coupon> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return CollUtil.newArrayList();
        }
        return couponList.stream().map(c -> {
            ProductCouponUseResponse response = new ProductCouponUseResponse();
            BeanUtils.copyProperties(c, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商品券关联商品编辑
     * @param request 编辑对象
     * @return Boolean
     */
    @Override
    public Boolean couponProductJoinEdit(CouponProductJoinRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Coupon coupon = getByIdAndMerIdException(request.getId(), systemAdmin.getMerId());
        if (!coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            throw new CrmebException("优惠券不是商品券");
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(request.getProductIds());
        List<CouponProduct> cpList = productIdList.stream().map(pid -> {
            CouponProduct couponProduct = new CouponProduct();
            couponProduct.setPid(pid);
            couponProduct.setCid(coupon.getId());
            return couponProduct;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            couponProductService.deleteByCid(coupon.getId());
            couponProductService.saveBatch(cpList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取优惠券简单对象列表
     * @param idList id列表
     * @return List
     */
    @Override
    public List<CouponSimpleVo> findSimpleListByIdList(List<Integer> idList) {
        LambdaQueryWrapper<Coupon> lqw = Wrappers.lambdaQuery();
        lqw.select(Coupon::getId, Coupon::getName);
        lqw.in(Coupon::getId, idList);
        lqw.eq(Coupon::getIsDel, false);
        List<Coupon> couponList = dao.selectList(lqw);
        return couponList.stream().map(coupon -> {
            CouponSimpleVo simpleVo = new CouponSimpleVo();
            simpleVo.setId(coupon.getId());
            simpleVo.setName(coupon.getName());
            return simpleVo;
        }).collect(Collectors.toList());
    }

    /**
     * 用户可领取的优惠券
     * @return List<Coupon>
     */
    private PageInfo<Coupon> getH5ListBySearch(Integer category, Integer merId, Integer productId, PageParamRequest pageParamRequest) {
        Date date = CrmebDateUtil.nowDateTime();

        Map<String, Object> map = new HashMap<>();
        if (category > 0) {
            map.put("category", category);
        }
        map.put("merId", merId);
        map.put("productId", productId);
        map.put("date", date);
        Page<Coupon> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Coupon> couponList = dao.getH5ListBySearch(map);
        return CommonPage.copyPageInfo(page, couponList);
    }
}

