package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReceiptInfo {
    // 电子回单文件全称在电子回单集合文件内获取具体文件内容。
    private String receipt_filename;

    // 金额。该收付关系金额，单位为元，精确到小数点后两位。
    private String amount;


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

    // 付款方式。参见付款方式列表。
    private String method;


    /**
     * 收款方类型。
     * 用户：USER
     * 平台商户：MERCHANT
     */
    private String payee_type;


    /**
     * 收款方标识。用户的user_id或者平台商户商户号。
     */
    private String payee_id;


}
