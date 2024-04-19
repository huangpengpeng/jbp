package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AcctSerialDetailParams {

    private String timestamp;    //	时间戳，格式yyyyMMddHHmmss HH以24小时为准，如20170309143712。timestamp 与连连服务器的时间(北京时间)之间的误差不能超过30分钟。
    private String oid_partner;    // 商户号，ACCP系统分配给平台商户的唯一编号。测试环境商户号
    private String user_id;    //	用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户。由商户自定义。

    /**
     * INNERMERCHANT:商户
     * INNERUSER：个人用户
     * INNERCOMPANY：企业用户
     */
    private String user_type;

    private String jno_acct;// 资金流水号。ACCP账务系统资金流水唯一标识。
}
