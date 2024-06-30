package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransferPayeeInfo2 implements Serializable {

    public TransferPayeeInfo2(String sub_acctno, String sub_acctname) {
        this.sub_acctno = sub_acctno;
        this.sub_acctname = sub_acctname;

    }

    private String sub_acctno;

    private String sub_acctname;
}
