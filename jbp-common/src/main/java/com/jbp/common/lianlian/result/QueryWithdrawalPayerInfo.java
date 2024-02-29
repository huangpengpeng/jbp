package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueryWithdrawalPayerInfo {

    /**
     * 付款方类型。
     * 用户：USER
     * 平台商户：MERCHANT
     */
    private String payer_type;

    /**
     * 付款方标识。
     * 付款方为用户时设置user_id
     * 付款方为商户时设置平台商户号。
     */
    private String payer_id;

}
