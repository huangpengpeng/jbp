package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithDrawalCheckInfo {

    /**
     * ACCEPT 确认通过
     * CANCEL 确认不通过
     */
    private String check_result;

    /**
     * 提现理由
     */
    private String check_reason;


}
