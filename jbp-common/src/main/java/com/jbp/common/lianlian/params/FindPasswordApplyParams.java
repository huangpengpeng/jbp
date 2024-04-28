package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FindPasswordApplyParams {

    public FindPasswordApplyParams(String timestamp, String oid_partner, String user_id) {
        this.timestamp = timestamp;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
    }

    private String timestamp;

    private String oid_partner;

    private String user_id;

    // 个人用户绑定的银行卡号，若未绑卡，则不传。
    private String linked_acctno;

    // 风险控制参数
    private String risk_item;
}
