package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AcctInfoParams {
    private String timestamp;
    private String oid_partner;
    private String user_id;

    /**
     * 用户类型。
     * INNERMERCHANT:商户
     * INNERUSER：个人用户
     * INNERCOMPANY：企业用户
     */
    private String user_type;

    /**
     * 	账务日期，格式yyyyMMdd。若传入，则表示查日终余额；若不传，则表示查当前实时余额。
     */
    private String date_acct;

}
