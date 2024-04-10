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


    reg_phone	必传	String	11	绑定手机号。用户开户注册绑定手机号。
    reg_phone_new	必传	String	11	新绑定手机号。
    password	必传	String	12	支付密码。6-12位的字母、数字，不可以是连续或者重复的数字和字母，正则：^[a-zA-Z0-9]{6,12}$。
    通过密码控件加密成密文传输。
    random_key	必传	String	不限	密码随机因子key。随机因子获取接口返回，或弹框控件回调函数返回。
}
