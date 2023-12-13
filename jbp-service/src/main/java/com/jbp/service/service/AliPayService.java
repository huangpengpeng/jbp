package com.jbp.service.service;


import com.jbp.common.model.order.Order;

import java.math.BigDecimal;

/**
 * 支付宝支付 Service
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
public interface AliPayService {


//    /**
//     * 查询支付结果
//     * @param orderNo 订单编号
//     * @return Boolean
//     */
//    Boolean queryPayResult(String orderNo);

    /**
     * 支付宝退款
     * @param outTradeNo 支付宝交易号
     * @param refundOrderNo 退款单号
     * @param refundReasonWapExplain 退款说明
     * @param refundPrice 退款金额
     */
    void refund(String outTradeNo, String refundOrderNo, String refundReasonWapExplain, BigDecimal refundPrice);

//    /**
//     * 查询退款
//     * @param orderNo 订单编号
//     */
//    Boolean queryRefund(String orderNo);
}
