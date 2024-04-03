package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueryLinkedAcctParams {

    private String timestamp;

    private String oid_partner;

    private String user_id;

}
