package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountReceiptParams extends BaseYopRequest {

    private String parentMerchantNo;

    /**
     * 交易类型
     * 可选项如下:
     * TRANSFER:企业账户转账
     * PAY:企业付款
     * RECHARGE:企业账户充值
     * WITHDRAW:企业账户提现
     * ADVANCE:记账薄收款
     * ORDER_REMIT:订单付款
     */
    private String tradeType;

    private String orderNo;

    private String requestNo;

    private String orderDate;

    private String merchantNo;

}
