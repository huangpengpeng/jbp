package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.request.*;
import com.jbp.common.response.CouponFrontResponse;
import com.jbp.common.response.CouponInfoResponse;
import com.jbp.common.response.ProductCouponUseResponse;
import com.jbp.common.vo.CouponSimpleVo;
import com.jbp.common.vo.MyRecord;

import java.util.List;

/**
 * CouponService 接口
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
public interface CouponService extends IService<Coupon> {

    /**
     * 创建优惠券
     */
    Boolean create(CouponRequest request);

    /**
     * 优惠券详情带异常
     */
    Coupon getInfoException(Integer id);

    /**
     * 优惠券详情
     */
    CouponInfoResponse info(Integer id);

    /**
     * 扣减数量
     * @param id 优惠券id
     * @param num 数量
     * @param isLimited 是否限量
     */
    Boolean deduction(Integer id, Integer num, Boolean isLimited);

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 移动端优惠券列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return List<CouponFrontResponse>
     */
    PageInfo<CouponFrontResponse> getH5List(CouponFrontSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 修改优惠券状态
     *
     * @param id 优惠券id
     */
    Boolean updateStatus(Integer id);

    /**
     * 商户端优惠券分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<Coupon> getMerchantPageList(CouponSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商品可用优惠券列表（商品创建时选择使用）
     *
     * @return List
     */
    List<ProductCouponUseResponse> getProductUsableList();

    /**
     * 商品券关联商品编辑
     * @param request 编辑对象
     * @return Boolean
     */
    Boolean couponProductJoinEdit(CouponProductJoinRequest request);

    /**
     * 获取优惠券简单对象列表
     * @param idList id列表
     * @return List
     */
    List<CouponSimpleVo> findSimpleListByIdList(List<Integer> idList);
}
