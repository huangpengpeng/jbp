package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SelfSettleParams extends BaseYopRequest {

    private String parentMerchantNo;
    private String merchantNo;
    private String settleRequestNo;
    private String operatePeriod;
    private String endTime;
    private String notifyUrl;
    private String bankRemark;

}
