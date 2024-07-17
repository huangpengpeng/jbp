package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PapSignInfo {

    private String sign_start_time;//授权生效日期：yyyyMMdd。

    private String sign_invalid_time;//授权结束时间。授权结束日期：yyyyMMdd。

    /**
     * WITH_HOLD：免密代扣  转账
     * WITH_WITHDRAW：免密提现  代发
     */
    private String agreement_type;

}
