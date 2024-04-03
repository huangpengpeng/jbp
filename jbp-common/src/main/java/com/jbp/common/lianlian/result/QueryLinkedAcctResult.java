package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class QueryLinkedAcctResult {

    private String oid_partner;

    private String ret_code;

    private String ret_msg;

    private String user_id;

    private List<LinkedAcctList> linked_acctlist;
}
