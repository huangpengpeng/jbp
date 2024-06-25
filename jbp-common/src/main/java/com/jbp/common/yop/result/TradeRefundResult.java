package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class TradeRefundResult extends BaseYopResponse {

    private String code;
    private String message;

    private String parentMerchantNo;
    private String merchantNo;
    //商户收款请求号
    private String orderId;
    //商户退款请求号
    private String refundRequestId;
    private String residualAmount;
    //商户退款请求对应在易宝的退款单号
    private String uniqueRefundNo;
    //退款申请金额
    private String refundAmount;
    //PROCESSING：处理中
    //SUCCESS：退款成功,首次调用时不会返回,在幂等调用返回该状态
    //FAILED：退款失败
    //CANCEL:退款关闭,商户线下通知易宝结束该笔退款后返回该状态
    private String status;
    //退款受理时间
    //示例值：2021-01-01 00:00:00
    private String refundRequestDate;
    private String description;

    @Override
    public boolean validate() {
        if (StringUtils.equals(this.getCode(), "OPR00000")) {
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }

    public boolean ifSuccess() {
        if (null == this) {
            return false;
        }
        return StringUtils.equals("SUCCESS", this.getCode());
    }

}
