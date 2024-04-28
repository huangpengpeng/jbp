package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ChangeRegPhoneVerifyParams {

    // 时间戳，格式yyyyMMddHHmmss
    private String timestamp;

    // 商户号，ACCP系统分配给平台商户的唯一编号
    private String oid_partner;

    // 用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户
    private String user_id;

    // 商户系统唯一交易流水号
    private String txn_seqno;

    // 授权令牌
    private String token;

    // 新绑定手机号短信验证码
    private String verify_code_new;
}
