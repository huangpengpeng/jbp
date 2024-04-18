package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountTransferOrderResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    /**
     * 转账状态
     * 订单状态
     * 可选项如下:
     * REQUEST_RECEIVE:请求已接收(易宝正在处理中，请勿重复下单)
     * SUCCESS:转账成功
     * FAIL:失败(该笔订单转账失败,可重新发起转账)
     * WAIT_AUDIT:已受理,待复核
     */
    private String transferStatus;

    private String requestNo;

    private String orderAmount;

    private String orderNo; // 易宝订单号

    private String fee; // 手续费

    private String debitAmount; // 扣除金额

    private String receiveAmount; // 入账金额

    private String feeMerchantNo; // 手续费承担方商户编号

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
