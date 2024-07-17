package com.jbp.common.lianlian.params;

import com.jbp.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztPapAgreeApplyParams {


    public LztPapAgreeApplyParams(String timestamp, String oid_partner, String user_id, PapSignInfo papSignInfo) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.txn_seqno = StringUtils.N_TO_10("LZT_PA_");
        this.txn_time = timestamp;
        this.user_id = user_id;
        this.flag_chnl = "H5";
        this.papSignInfo = papSignInfo;
        System.out.println("连连请求流水号：" + txn_seqno);
    }

    private String timestamp;
    private String oid_partner;
    private String txn_seqno;
    private String txn_time;
    private String user_id;
    private String flag_chnl;
    private String notify_url;

    private PapSignInfo papSignInfo;

}
