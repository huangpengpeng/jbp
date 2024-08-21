package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class TradeCreateResult {

    private String ret_code;

    private String ret_msg;

    private String oid_partner; // ACCP系统分配给平台商户的唯一编号

    private String user_id; // 商户用户唯一编号。用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户

    private BigDecimal total_amount; // 订单总金额，单位为元，精确到小数点后两位。

    private String txn_seqno; // 商户系统唯一交易流水号。

    private String accp_txno; // ACCP系统交易单号

    public boolean validate() {
        return "0000".equals(ret_code);
    }
}
