package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PapAgreeQueryParams {

    public PapAgreeQueryParams(String timestamp, String oid_partner, String user_id, String ori_txn_seqno) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
        this.ori_txn_seqno = ori_txn_seqno;
    }

    private String timestamp;
    private String oid_partner;
    private String user_id;


    private String ori_txn_seqno;



}
