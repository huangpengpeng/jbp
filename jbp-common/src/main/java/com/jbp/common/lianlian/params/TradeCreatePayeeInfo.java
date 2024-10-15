package com.jbp.common.lianlian.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TradeCreatePayeeInfo {

    public TradeCreatePayeeInfo(String payee_id, String payee_type, String payee_accttype, String payee_amount) {
        this.payee_id = payee_id;
        this.payee_type = payee_type;
        this.payee_accttype = payee_accttype;
        this.payee_amount = payee_amount;
    }

    private String payee_id;
    private String payee_type;
    private String payee_accttype;
    private String payee_amount;
    private String payee_memo;
}
