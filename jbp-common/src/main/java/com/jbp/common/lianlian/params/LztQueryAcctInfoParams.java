package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztQueryAcctInfoParams {
    public LztQueryAcctInfoParams(String timestamp, String oid_partner,
                                  String user_id, String user_type, String acct_type) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
        this.user_type = user_type;
        this.acct_type = acct_type;
    }

    /**
     * 交易服务器时间戳格式：yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 外部用户号
     */
    private String user_id;

    /**
     * 用户：USER 平台商户：MERCHANT
     */
    private String user_type;

    /**
     * 用户账户：USEROWN 平台商户自有资金账户：MCHOWN
     */
    private String acct_type;

}
