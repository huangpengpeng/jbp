package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户信息查询 响应参数
 */
@Data
@EqualsAndHashCode
public class AcctInfoResult {
    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;
    private String oid_userno;
    private String bank_account;
    private List<AcctInfo> acctinfo_list;


}
