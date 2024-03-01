package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class OpenacctApplyBusinessInfo {
    private String scale;
    private String industry_code;
    private String user_name;
    private String registered_capital;
    private String business_scope;
}
