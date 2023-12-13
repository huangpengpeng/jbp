package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.List;

/**
*  OrderDetailService 接口
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
public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 根据主订单号获取
     * @param orderNo 订单编号
     * @return List
     */
    List<OrderDetail> getByOrderNo(String orderNo);

    /**
     * 订单商品评论列表
     * @param userId 用户id
     * @param isReply 是否评价
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<OrderDetail> getReplyList(Integer userId, Boolean isReply, PageParamRequest pageRequest);

    /**
     * 订单收货
     * @param orderNo 订单号
     */
    Boolean takeDelivery(String orderNo);

    /**
     * 售后申请列表(可申请售后列表)
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<OrderDetail> findAfterSaleApplyList(Integer uid, String orderNo, PageParamRequest pageParamRequest);

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    Integer getSalesNumByDateAndProductId(String date, Integer proId);

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    BigDecimal getSalesByDateAndProductId(String date, Integer proId);

    /**
     * 订单发货获取订单详情列表
     * @param orderNo 订单号
     * @return 订单详情列表
     */
    List<OrderDetail> getShipmentByOrderNo(String orderNo);

    /**
     * 获取待评价数量
     * @return 待评价数量
     */
    Integer getAwaitReplyCount(Integer userId);
}