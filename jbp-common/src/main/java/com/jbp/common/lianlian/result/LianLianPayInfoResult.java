package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LianLianPayInfoResult implements Serializable {
    private String pubKey;
    private String priKey;
    private String oid_partner;
    private String payee_no;
    private String req_domain;
    private String notify_url;
    private String return_url;
    private String status;
    private String host;

    // 来账通
    private String lzt_priKey;
    private String lzt_oid_partner;

}
