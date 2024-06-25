package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class TradeRefundParams extends BaseYopRequest {

    public TradeRefundParams(String orderId, String refundRequestId, String refundAmount) {
        this.orderId = orderId;
        this.refundRequestId = refundRequestId;
        this.refundAmount = refundAmount;
        this.refundAccountType = "FUND_ACCOUNT";
    }

    private String parentMerchantNo;

    @NotBlank(message = "商户编号")
    private String merchantNo;
    @NotBlank(message = "收款交易对应的商户收款请求号")
    private String orderId;
    @NotBlank(message = "商户退款请求号")
    private String refundRequestId;

    //收款交易对应的易宝收款订单号。
    private String uniqueOrderNo;

    @NotBlank(message = "退款金额不能为空")
    private String refundAmount;

    private String description;
    private String memo;
    private String refundAccountType;
    private String notifyUrl;
}
