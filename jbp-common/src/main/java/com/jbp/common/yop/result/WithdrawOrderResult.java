package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawOrderResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    /**
     * REQUEST_RECEIVE:请求已接收（易宝正在处理中，收到最终结果前请勿重复下单）
     * REQUEST_ACCEPT:请求已受理（易宝正在处理中，收到最终结果前请勿重复下单）
     * FAIL:失败
     * REMITING:（银行正在处理中，收到最终结果前请勿重复下单）
     */
    private String status;

    // 业务主体商编
    private String orderNo;


    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
