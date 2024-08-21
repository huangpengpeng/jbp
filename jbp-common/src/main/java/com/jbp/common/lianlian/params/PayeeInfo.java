package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class PayeeInfo {

    private String payee_id; // 收款方标识，收款方为用户时，为用户user_id，收款方为平台商户时，取平台商户号。
    /**
     * 用户：USER
     * 平台商户：MERCHANT
     */
    private String payee_type;

    /**
     * 用户账户：USEROWN
     * 平台商户自有资金账户：MCHOWN
     * 平台商户优惠券账户：MCHCOUPON
     * 平台商户手续费账户：MCHFEE
     */
    private String payee_accttype;

    private BigDecimal payee_amount;// 单位：元，精确到小数点后两位。
    private String payee_memo;// 收款备注信息。
}
