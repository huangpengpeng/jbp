package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountPayOrderResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String requestNo;

    private String orderNo; // 易宝订单号

    /**
     * 订单状态
     * 可选项如下:
     * REQUEST_RECEIVE:请求已接收（易宝正在处理中，收到最终结果前请勿重复下单）
     * REQUEST_ACCEPT:请求已受理（易宝正在处理中，收到最终结果前请勿重复下单）
     * REMITING:银行处理中（银行正在处理中，收到最终结果前请勿重复下单）
     * FAIL:失败
     */
    private String status;

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
