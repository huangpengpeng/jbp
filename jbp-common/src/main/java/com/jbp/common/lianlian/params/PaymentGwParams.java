package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode
public class PaymentGwParams {

    private String timestamp;
    private String oid_partner;
    private String txn_seqno;
    private BigDecimal total_amount;
    private RiskItemInfo risk_item;
    private String appid;
    private String openid;


    private String client_ip;

    private String extend_params;

    private PayerInfo payerInfo;

    private List<PayMethods> payMethods;
}
