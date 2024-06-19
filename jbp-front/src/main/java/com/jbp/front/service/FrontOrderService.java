package com.jbp.front.service;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.UserAddress;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.vo.LogisticsResultVo;
import com.jbp.common.vo.PreOrderInfoVo;

import java.util.HashMap;
import java.util.List;

/**
 * 移动端端订单操作
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
public interface FrontOrderService {

    /**
     * 订单预下单V1.3
     * @param request 预下单请求参数
     *                优惠金额=商户+平台
     *                用户可以选择 平台券+商户券（多商户可以选择多个商户券）
     * @return PreOrderResponse
     */
    OrderNoResponse preOrder_V1_3(PreOrderRequest request);

    /**
     * 加载预下单信息
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    PreOrderResponse loadPreOrder(String preOrderNo);

    /**
     * 计算订单价格
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request);

    /**
     * 创建订单
     * @param orderRequest 创建订单请求参数
     * @return OrderNoResponse 订单编号
     */
    OrderNoResponse createOrder(CreateOrderRequest orderRequest);

    /**
     * 订单列表
     * @param status 订单状态|-1=全部,0=待支付,1=待发货,2=待收货
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<OrderFrontDataResponse> list(Integer status, PageParamRequest pageRequest);

    /**
     * 移动端订单详情
     * @param orderNo 订单编号
     * @return OrderFrontDetailResponse
     */
    OrderFrontDetailResponse frontDetail(String orderNo);

    /**
     * 订单商品评论列表
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<InfoReplyResponse> replyList(PageParamRequest pageRequest);

    /**
     * 评价订单商品
     * @param request 评价参数
     */
    Boolean replyProduct(OrderProductReplyRequest request);

    /**
     * 取消订单
     * @param orderNo 订单编号
     */
    Boolean cancel(String orderNo);

    /**
     * 订单收货
     * @param orderNo 订单号
     * @return Boolean
     */
    Boolean takeDelivery(String orderNo);

    /**
     * 删除订单
     * @param orderNo 订单号
     * @return Boolean
     */
    Boolean delete(String orderNo);

    /**
     * 售后申请列表(可申请售后列表)
     * @param request 搜索参数
     */
    PageInfo<OrderDetail> getAfterSaleApplyList(CommonSearchRequest request);

    /**
     * 查询退款理由
     * @return 退款理由集合
     */
    List<String> getRefundReason();

    /**
     * 订单退款申请
     * @param request 申请参数
     * @return Boolean
     */
    Boolean refundApply(OrderRefundApplyRequest request);

    /**
     * 退款订单列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<RefundOrderResponse> getRefundOrderList(OrderAfterSalesSearchRequest request);

    /**
     * 退款订单详情
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    RefundOrderInfoResponse refundOrderDetail(String refundOrderNo);

    /**
     * 订单物流详情
     */
    LogisticsResultVo getLogisticsInfo(Integer invoiceId);


    LogisticsResultVo getHistoryLogisticsInfo(String orderNo);


    /**
     * 获取发货单列表
     * @param orderNo 订单号
     * @return 发货单列表
     */
    OrderInvoiceFrontResponse getInvoiceList(String orderNo);

    /**
     * 获取个人中心订单数量
     */
    OrderCenterNumResponse userCenterNum();

    /**
     * 获取订单状态图
     */
    List<HashMap<String, Object>> getOrderStatusImage();

    /**
     * 计算订单运费
     */
    void getFreightFee(PreOrderInfoVo orderInfoVo, UserAddress userAddress);

    /**
     * 获取优惠金额
     */
    void getCouponFee(PreOrderInfoVo orderInfoVo, List<OrderMerchantRequest> orderMerchantRequestList, Integer uid);

    /**
     * 积分抵扣计算
     *
     * @param orderInfoVo  订单vo
     * @param userIntegral 用户积分
     */
    void integralDeductionComputed(PreOrderInfoVo orderInfoVo, Integer userIntegral);

    /**
     * 退款单退回商品
     */
    Boolean returningGoods(OrderRefundReturningGoodsRequest request);

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     */
    Boolean revoke(String refundOrderNo);

    /**
     * 订单列表(v1.4.0)
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<OrderFrontDataResponse> list_v1_4(OrderFrontListRequest request);

}
