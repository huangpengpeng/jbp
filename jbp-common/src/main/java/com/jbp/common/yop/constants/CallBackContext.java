package com.jbp.common.yop.constants;

import lombok.Data;

@Data
public class CallBackContext {

    // 注册入网
    public static final String REGISTER = "register";

    // 订单支付
    public static final String ORDERS_PAY = "ordersPay";

    // 订单支付
    public static final String ORDERS_REFUND = "ordersRefund";


}
