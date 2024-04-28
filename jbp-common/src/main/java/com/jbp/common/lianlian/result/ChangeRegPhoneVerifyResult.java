package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ChangeRegPhoneVerifyResult {

    private String ret_code;

    private String ret_msg;

    private String oid_partner;

    private String user_id;

    // 商户系统唯一交易流水号。
    private String txn_seqno;

    // ACCP系统交易单号
    private String accp_txno;



}
