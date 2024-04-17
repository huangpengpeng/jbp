package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountRechargeResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String requestNo;

    private String orderNo;

    private String merchantNo;

    /**
     * 可选项如下:
     * INIT:已受理
     * PAY_SUCCESS:支付成功
     * FAIL:失败
     * SUCCESS: 充值成功
     */
    private String status;

    private String orderAmount;


    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
