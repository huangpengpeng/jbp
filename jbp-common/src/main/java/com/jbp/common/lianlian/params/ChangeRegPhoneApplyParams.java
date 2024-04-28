package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ChangeRegPhoneApplyParams {


    // 格式yyyyMMddHHmmss
    private String timestamp;

    // ACCP系统分配给平台商户的唯一编号
    private String oid_partner;

    // 用户在商户系统中的唯一编号
    private String user_id;

    // 商户系统唯一交易流水号
    private String txn_seqno;

    // 商户系统交易时间
    private String txn_time;

    // 交易结果异步通知接收地址
    private String notify_url;

    // 连连风控部门要求商户统一传入风险控制参数字段
    private String risk_item;

    private String reg_phone;

    private String reg_phone_new;

    private String password;

    private String random_key;
}
