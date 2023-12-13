package com.jbp.front.service;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.vo.LogisticsResultVo;

import java.util.HashMap;
import java.util.List;

/**
 * 移动端端订单操作
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
public interface FrontOrderService {

    /**
     * 订单预下单
     * @param request 预下单请求参数
     * @return PreOrderResponse
     */
    OrderNoResponse preOrder(PreOrderRequest request);

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
     * @param pageParamRequest 分页参数
     */
    PageInfo<OrderDetail> getAfterSaleApplyList(String orderNo, PageParamRequest pageParamRequest);

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
     * @param type 列表类型：0-处理中，9-申请记录
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<RefundOrderResponse> getRefundOrderList(Integer type, PageParamRequest pageRequest);

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
}
