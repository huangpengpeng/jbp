package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class TradeOrderResult extends BaseYopResponse {

    private String code;
    private String message;

    //发起方商编
    private String parentMerchantNo;
    //商户编号
    private String merchantNo;
    //商户收款请求号
    private String orderId;
    //易宝收款订单号
    private String uniqueOrderNo;

    private String token;
    //订单金额
    private String orderAmount;
    //子单域信息
    private SubOrderInfo[] subOrderInfoList;

    private String bizSystemNo;

    @Setter
    @Getter
    @NoArgsConstructor
    static class SubOrderInfo implements Serializable {
        //商户编号
        private String merchantNo;
        //商户收款请求号
        private String orderId;
        //易宝收款订单号
        private String uniqueOrderNo;
        //订单金额
        private String orderAmount;
    }

    @Override
    public boolean validate() {
        if (StringUtils.equals(this.getCode(), "OPR00000")) {
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }
}
