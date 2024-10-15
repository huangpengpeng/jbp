package com.jbp.common.lianlian.params;

import lombok.*;

/**
 * 支付统一创单 请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TradeCreateParams {
    private String timestamp;
    private String oid_partner;
    private String txn_type;
    private String user_id;
    private String user_type;
    private String notify_url;
    private String return_url;
    private String pay_expire;

    private TradeCreateOrderInfo orderInfo;
    private TradeCreatePayeeInfo[] payeeInfo;
}

