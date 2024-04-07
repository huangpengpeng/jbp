package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransferPayeeInfo implements Serializable {

    public TransferPayeeInfo(String payee_type, String bank_acctno, String bank_code, String bank_acctname, String cnaps_code) {
        this.payee_type = payee_type;
        this.bank_acctno = bank_acctno;
        this.bank_code = bank_code;
        this.bank_acctname = bank_acctname;
        this.cnaps_code = cnaps_code;
    }

    /**
     * 收款方类型。
     * 对私银行账户：BANKACCT_PRI
     * 对公银行账户：BANKACCT_PUB
     */
    private String payee_type;
    /**
     * 银行账号
     */
    private String bank_acctno;
    /**
     * 银行编码。收款方类型为对公银行账户必须
     */
    private String bank_code;
    /**
     * 户名。
     */
    private String bank_acctname;

    /**
     * 大额行号。收款方类型为对公银行账户必须。
     */
    private String cnaps_code;


}
