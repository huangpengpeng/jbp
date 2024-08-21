package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class TradeCreateParams implements Serializable {


    private String timestamp;

    private String oid_partner;
    /**
     * 用户充值：USER_TOPUP
     * 商户充值：MCH_TOPUP
     * 普通消费：GENERAL_CONSUME
     * 担保消费：SECURED_CONSUME
     */
    private String txn_type;

    private String user_id; // 用户在商户系统中的唯一编号。交易类型非商户充值时必传。
    /**
     * 注册用户：REGISTERED
     * 匿名用户：ANONYMOUS
     */
    private String user_type;

    private String notify_url;
    private String return_url;
    private String pay_expire; //默认3天；

    private OrderInfo orderInfo;
    private List<PayeeInfo> payeeInfo;

}
