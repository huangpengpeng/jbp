package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
@NoArgsConstructor
public class WechatAliPayPayResult extends BaseYopResponse {

    private String code;
    private String message;

    private String orderId;
    private String uniqueOrderNo;
    private String bankOrderId;
    //预支付标识信息
    private String prePayTn;

    @Override
    public boolean validate() {
        if(StringUtils.equals(this.getCode(), "00000")){
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }
}
