package com.jbp.common.lianlian.params;

import com.alibaba.fastjson.JSONObject;
import lombok.*;

/**
 * 账户+收银台 请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CashierPayCreateParams {
    /**
     * 时间戳，格式yyyyMMddHHmmss
     */
    private String timestamp;
    /**
     * 平台商户号
     */
    private String oid_partner;
    /**
     * 交易类型。
     * 用户充值：USER_TOPUP
     * 商户充值：MCH_TOPUP
     * 普通消费：GENERAL_CONSUME
     * 担保消费：SECURED_CONSUME
     */
    private String txn_type;
    /**
     * 电商平台用户唯一标识
     */
    private String user_id;

    private String user_type;
    /**
     * 通知地址
     */
    private String notify_url;
    /**
     * 支付成功跳转地址
     */
    private String return_url;

    private String pay_expire; // 默认三天

    /**
     * 交易发起渠道。
     * H5
     * PC
     */
    private String flag_chnl;

    /**
     * 风险控制参数
     *
     * @see RiskItemInfo
     */
    private String risk_item;

    private String allow_method;

    private JSONObject extend;

    // 商户订单信息
    private CashierPayCreateOrderInfo orderInfo;
    // 收款方信息
    private CashierPayCreatePayeeInfo[] payeeInfo;
    // 商户自定义样式参数
    private CashierPayCreateStyle style;
    // 付款方信息
    private CashierPayCreatePayerInfo payerInfo;
}
