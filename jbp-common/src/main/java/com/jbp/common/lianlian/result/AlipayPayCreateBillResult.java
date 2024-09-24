package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收银台支付创单 响应参数
 */
@Data
@EqualsAndHashCode
public class AlipayPayCreateBillResult {
    private String ret_code;
    private String ret_msg;
    private String sign_type;
    private String sign;
    // 授权令牌。有效期为30分钟。
    private String token;
    private String no_order;
    private String dt_order;
    private String money_order;
    private String oid_paybill;
    //  清算日期。 格式：YYYYMMDD。
    private String settle_date;
    // 连连网关地址。跳转方式：商户前端form表单POST提交
    private String gateway_url;
    private String payload;

    private  String payMerchantNo; // 业务定义
}
