package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FindPasswordVerifyParams {

    public FindPasswordVerifyParams(String timestamp, String oid_partner, String user_id) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
    }

    private String timestamp;

    private String oid_partner;

    private String user_id;

    private String token;

    private String verify_code;

    private String random_key;

    private String password;

}
