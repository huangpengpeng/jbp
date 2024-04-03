package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawalCheckParams {

    private String timestamp;
    private String oid_partner;
    private WithDrawalOrderInfo orderInfo;
    private WithDrawalCheckInfo checkInfo;
}
