package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SelfSettleResult extends BaseYopResponse {

    private String code;

    private String message;

    private String parentMerchantNo;

    private String merchantNo;

    private String settleRequestNo;

    private String yeepayFlowNo;


    @Override
    public boolean validate() {
        return "000000".equals(code);
    }
}
