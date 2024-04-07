package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FindPasswordVerifyResult {

    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;



}
