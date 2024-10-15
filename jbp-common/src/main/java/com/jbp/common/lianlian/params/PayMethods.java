package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PayMethods {
    private String method; //	付款方式。详见付款方式列表
    private String amount;//付款金额。付款方式对应的金额，单位为元，精确到小数点后两位。

}
