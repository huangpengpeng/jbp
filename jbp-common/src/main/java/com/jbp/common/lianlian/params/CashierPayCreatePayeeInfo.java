package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CashierPayCreatePayeeInfo {
    /**
     * 收款方标识，收款方为用户时，为用户user_id，收款方为平台商户时，取平台商户号
     */
    private String payee_id;
    /**
     * 收款方类型。
     * 用户：USER
     * 平台商户：MERCHANT
     */
    private String payee_type;
    /**
     * 收款方账户类型。交易类型为商户充值时必须指定充值入账账户类型：
     * 用户账户：USEROWN
     * 平台商户自有资金账户：MCHOWN
     * 平台商户优惠券账户：MCHCOUPON
     * 平台商户手续费账户：MCHFEE
     */
    private String payee_accttype;
    /**
     * 收款金额。单位：元，精确到小数点后两位。
     */
    private String payee_amount;
    /**
     * 收款备注
     */
    private String payee_memo;
}
