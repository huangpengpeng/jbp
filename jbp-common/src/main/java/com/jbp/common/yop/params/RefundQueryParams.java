package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @Author dengmin
 * @Created 2021/4/22 下午1:58
 */
@Getter
@Setter
@NoArgsConstructor
public class RefundQueryParams extends BaseYopRequest {

    public RefundQueryParams(String merchantNo, String orderId, String refundRequestId) {
        this.merchantNo = merchantNo;
        this.orderId = orderId;
        this.refundRequestId = refundRequestId;
    }

    private String parentMerchantNo;

    @NotBlank(message = "商户编号不能为空")
    private String merchantNo;
    @NotBlank(message = "收款交易对应的商户收款请求号不能为空")
    private String orderId;
    @NotBlank(message = "商户退款请求号不能为空")
    //商户退款请求号。可包含字母、数字、下划线；需要保证在商户端不重复。
    private String refundRequestId;

    private String uniqueOrderNo;
}
