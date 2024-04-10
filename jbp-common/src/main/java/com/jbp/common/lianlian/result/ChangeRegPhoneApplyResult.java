package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ChangeRegPhoneApplyResult {

    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;

    private String token;

    private String reg_phone;

    private String regMsg;
}
