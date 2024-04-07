package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransferPayeeInfo implements Serializable {

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
