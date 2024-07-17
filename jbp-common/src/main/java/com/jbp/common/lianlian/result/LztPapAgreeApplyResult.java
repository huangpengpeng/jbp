package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztPapAgreeApplyResult {

    private String ret_code;

    private String ret_msg;

    private String  gateway_url;

    private String  oid_partner;

    private String user_id;

    private String txn_seqno;

    private String accp_txno;

}
