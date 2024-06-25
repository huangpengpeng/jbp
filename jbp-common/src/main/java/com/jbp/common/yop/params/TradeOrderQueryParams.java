package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeOrderQueryParams extends BaseYopRequest {

    public TradeOrderQueryParams(String parentMerchantNo, String merchantNo, String orderId) {
        this.parentMerchantNo = parentMerchantNo;
        this.merchantNo = merchantNo;
        this.orderId = orderId;
    }

    private String parentMerchantNo;

    private String merchantNo;

    private String orderId;

}
