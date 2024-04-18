package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountRechargeQueryParams extends BaseYopRequest {

    private String parentMerchantNo;

    private String merchantNo;

    private String requestNo;
}
