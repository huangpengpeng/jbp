package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.vo.LogisticsResultVo;

import java.math.BigDecimal;
import java.util.List;

/**
*  OrderService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface OrderService extends IService<Order> {

    /**
     * 根据订单编号获取订单
     * @param orderNo 订单编号
     */
    Order getByOrderNo(String orderNo);

    /**
     * 更新支付结果
     * @param orderNo 订单编号
     */
    Boolean updatePaid(String orderNo);

    /**
     * 获取订单
     * @param outTradeNo 商户系统内部的订单号
     */
    Order getByOutTradeNo(String outTradeNo);

    /**
     * 获取用户订单列表
     * @param userId 用户id
     * @param status 订单状态|-1：全部，0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<Order> getUserOrderList(Integer userId, Integer status, PageParamRequest pageRequest);

    /**
     * 获取用户订单列表V1.4
     * @param userId 用户id
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<Order> getUserOrderList_v1_4(Integer userId, OrderFrontListRequest request);

    /**
     * 取消订单
     * @param orderNo 订单编号
     * @param isUser 是否用户取消
     * @return Boolean
     */
    Boolean cancel(String orderNo, Boolean isUser);

    /**
     * 商户端后台分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantOrderPageResponse> getMerchantAdminPage(OrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取商户端订单各状态数量
     * @param dateLimit 时间参数
     */
    OrderCountItemResponse getMerchantOrderStatusNum(String dateLimit);

    /**
     * 订单详情（PC）
     * @param orderNo 订单编号
     * @return OrderAdminDetailResponse
     */
    OrderAdminDetailResponse adminDetail(String orderNo);

    /**
     * 发货
     * @param request 发货参数
     * @return Boolean
     */
    Boolean send(OrderSendRequest request);

    /**
     * 批量发货
     * @param sendRequestList
     * @return
     */
    Boolean batchSend(List<OrderSendRequest> sendRequestList);

    /**
     * 小票打印
     * @param orderNo 订单编号
     * @return 打印结果
     */
    void printReceipt(String orderNo);

    /**
     * 商户删除订单
     * @param orderNo 订单编号
     * @return Boolean
     */
    Boolean merchantDeleteByOrderNo(String orderNo);

    /**
     * 商户备注订单
     * @param request 备注参数
     * @return Boolean
     */
    Boolean merchantMark(OrderRemarkRequest request);

    /**
     * 订单收货
     * @param orderNo 订单号
     */
    Boolean takeDelivery(String orderNo);

    /**
     * 平台端后台分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PlatformOrderPageResponse> getPlatformAdminPage(OrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取平台端订单各状态数量
     * @param dateLimit 时间参数
     */
    OrderCountItemResponse getPlatformOrderStatusNum(String dateLimit);

    /**
     * 订单详情（平台）
     * @param orderNo 订单编号
     * @return PlatformOrderAdminDetailResponse
     */
    PlatformOrderAdminDetailResponse platformInfo(String orderNo);

    /**
     * 获取订单快递信息(商户端)
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    LogisticsResultVo getLogisticsInfoByMerchant(Integer invoiceId);

    /**
     * 获取订单快递信息
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    LogisticsResultVo getLogisticsInfo(Integer invoiceId);

    /**
     * 核销码核销订单
     * @param verifyCode 核销码
     * @return 核销结果
     */
    Boolean verificationOrderByCode(String verifyCode);

    /**
     * 通过日期获取商品交易件数
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderProductNumByDate(String date);

    /**
     * 通过日期获取商品交易成功件数
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderSuccessProductNumByDate(String date);

    /**
     * 通过日期获取订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderNumByDate(Integer merId, String date);

    /**
     * 通过日期获取支付订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getPayOrderNumByDate(String date);

    /**
     * 通过日期获取支付订单金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    BigDecimal getPayOrderAmountByDate(Integer merId, String date);

    /**
     * 订单细节详情列表
     * @param orderNo 订单号
     * @return 订单细节详情列表
     */
    List<OrderDetail> getDetailList(String orderNo);

    /**
     * 获取订单发货单列表(商户端)
     * @param orderNo 订单号
     * @return 发货单列表
     */
    List<OrderInvoiceResponse> getInvoiceListByMerchant(String orderNo);

    /**
     * 获取订单发货单列表
     * @param orderNo 订单号
     * @return 发货单列表
     */
    List<OrderInvoiceResponse> getInvoiceList(String orderNo);

    /**
     * 获取可以自动完成的订单
     * @param autoCompleteDay 自动完成订单天数
     * @return 可以自动完成的订单列表
     */
    List<Order> findCanCompleteOrder(Integer autoCompleteDay);

    /**
     * 按订单号批量完成订单
     * @param orderNoList 订单号列表
     * @return Boolean
     */
    Boolean batchCompleteByOrderNo(List<String> orderNoList);

    /**
     * 获取订单数量（订单状态， 用户id）
     * @param status 订单状态
     * @param userId 用户ID
     * @return 订单数量
     */
    Integer getCountByStatusAndUid(Integer status, Integer userId);

    /**
     * 获取推广订单总金额
     * @param orderNoList 订单编号列表
     * @return BigDecimal
     */
    BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList);

    /**
     * 订单拆单删除
     * @param orderNo 订单号
     */
    Boolean paySplitDelete(String orderNo);

    /**
     * 通过原始单号获取订单列表
     * @param orderNo 原始单号
     * @return 订单列表
     */
    List<Order> getByPlatOrderNo(String orderNo);

    /**
     * 判断用户是否存在待处理订单
     * 待发货、部分发货、待核销
     * @param uid 用户id
     */
    Boolean isExistPendingOrderByUid(Integer uid);

    /**
     * 获取待收货订单
     * @param sendTime 发货时间
     * @return List
     */
    List<Order> findAwaitTakeDeliveryOrderList(String sendTime);

    /**
     * 获取待发货订单数量
     * @return Integer
     */
    Integer getNotShippingNum(Integer merId);

    /**
     * 获取待核销订单数量
     * @return Integer
     */
    Integer getAwaitVerificationNum(Integer merId);

    /**
     * 获取用户购买的商品数量
     * @param uid 用户ID
     * @param proId 商品ID
     * @param productType 商品类型
     */
    Integer getProductNumCount(Integer uid, Integer proId, Integer productType);

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    List<Order> findPayByDate(Integer merId, String date);

    /**
     * 获取导出订单列表
     * @param request 请求参数
     * @return
     */
    List<Order> findExportList(OrderSearchRequest request,Integer id);
}
