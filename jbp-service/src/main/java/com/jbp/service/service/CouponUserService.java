package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.request.CouponUserSearchRequest;
import com.jbp.common.request.OrderUseCouponRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.CouponUserOrderResponse;
import com.jbp.common.response.CouponUserResponse;
import com.jbp.common.response.UserCouponResponse;
import com.jbp.common.vo.MyRecord;

import java.util.HashMap;
import java.util.List;

/**
 * StoreCouponUserService 接口
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
public interface CouponUserService extends IService<CouponUser> {

    /**
     * 批量使用优惠券
     * @param couponIdList 优惠券Id列表
     * @return Boolean
     */
    Boolean useBatch(List<Integer> couponIdList);

    /**
     * 优惠券发放记录
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<CouponUserResponse> getPageList(CouponUserSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<Integer, CouponUser> getMapByUserId(Integer userId);

    /**
     * 根据预下单号获取可用优惠券
     * @param request 预下单参数
     * @return 可用优惠券集合
     */
    List<CouponUserOrderResponse> getListByPreOrderNo(OrderUseCouponRequest request);

    /**
     * 优惠券过期定时任务
     */
    void overdueTask();

    /**
     * 用户领取优惠券
     * @param cid 优惠券id
     */
    Boolean receiveCoupon(Integer cid);

    /**
     * 支付成功赠送处理
     * @param couponId 优惠券编号
     * @param uid  用户uid
     * @return MyRecord
     */
    MyRecord paySuccessGiveAway(Integer couponId, Integer uid);

//    /**
//     * 根据uid获取列表
//     * @param uid uid
//     * @param pageParamRequest 分页参数
//     * @return List<StoreCouponUser>
//     */
//    List<StoreCouponUser> findListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取可用优惠券数量
     * @param uid 用户uid
     */
    Integer getUseCount(Integer uid);

    /**
     * 我的优惠券列表
     * @param type 类型，usable-可用，unusable-不可用
     * @param pageParamRequest 分页参数
     * @return PageInfo<StoreCouponUserResponse>
     */
    PageInfo<UserCouponResponse> getMyCouponList(String type, PageParamRequest pageParamRequest);

    /**
     * 回退优惠券（到未使用状态）
     * @param couponIdList 优惠券id
     * @return 回退结果
     */
    Boolean rollbackByIds(List<Integer> couponIdList);
}
