package com.jbp.service.service;

import com.jbp.common.lianlian.result.CashierPayCreateResult;
import com.jbp.common.model.order.Order;
import com.jbp.common.request.OrderPayRequest;
import com.jbp.common.response.CashierInfoResponse;
import com.jbp.common.response.OrderPayResultResponse;
import com.jbp.common.response.PayConfigResponse;

/**
 * PayService 接口
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
public interface PayService {

    /**
     * 获取支付配置
     */
    PayConfigResponse getPayConfig(Integer payGateway);

    /**
     * 订单支付
     * @param orderPayRequest 订单支付参数
     * @return OrderPayResultResponse
     */
    OrderPayResultResponse payment(OrderPayRequest orderPayRequest);

    /**
     * 充值共享舱订单支付
     * @param orderPayRequest 订单支付参数
     * @return OrderPayResultResponse
     */
    OrderPayResultResponse gxcPayment(OrderPayRequest orderPayRequest);

    /**
     * 查询订单微信支付结果
     * @param orderNo 订单编号
     */
    Boolean queryWechatPayResult(String orderNo);

    /**
     * 查询订单支付宝支付结果
     * @param orderNo 订单编号
     */
    Boolean queryAliPayResult(String orderNo);

    /**
     * 支付成功后置处理
     * @param orderNo 订单编号
     */
    Boolean payAfterProcessing(String orderNo);

    /**
     * 支付成功后置处理(临时)
     * @param orderNo 订单编号
     */
    Boolean payAfterProcessingTemp(String orderNo);

    /**
     * 获取收银台信息
     *
     * @param orderNo 订单号
     */
    CashierInfoResponse getCashierIno(String orderNo);


    Boolean zeroPay(Order order);

    Boolean walletPay(Order order);

    Boolean confirmPay(Order order);


    /**
     * 订单支付
     * @param orderPayRequest 订单支付参数
     * @return OrderPayResultResponse
     */
    CashierPayCreateResult checkstand(OrderPayRequest orderPayRequest);
}
