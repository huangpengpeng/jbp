package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LinkedAcctList implements Serializable {
    /**
     * 卡号
     */
    private String linked_acctno;

    /**
     * 银行编码
     */
    private String linked_bankcode;

    /**
     * 企业用户开户行行号
     */
    private String linked_brbankno;

    /**
     * 对公账户开户行名
     */
    private String linked_brbankname;

    /**
     * 银行预留手机号
     */
    private String linked_phone;

    /**
     * 银行预留手机号
     */
    private String linked_agrtno;
}
