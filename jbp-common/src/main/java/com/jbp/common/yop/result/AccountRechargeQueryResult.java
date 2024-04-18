package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountRechargeQueryResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String createTime; // 下单时间

    private String requestNo; // 商户请求号

    private String orderNo; // 充值订单号

    /**
     * 订单状态
     * 可选项如下:
     * INIT:充值处理中-等待支付结果
     * ACCOUNTING:充值处理中-入账中
     * ACCOUNTING_EXCEPTION:充值处理中-入账异常
     * FAIL:充值失败
     * SUCCESS:充值成功
     * CANCELED:订单取消
     */
    private String status;

    private String merchantNo; // 充值商户编号

    private String orderAmount; // 支付金额

    private String creditAmount; // 示例值：为保证充值正常入账，银行汇款时汇款金额需为支付金额

    private String fee; // 手续费

    /**
     * 支付方式
     * 可选项如下:
     * BANK_TRANSFER:银行汇款
     * B2C:个人网银
     * B2B:企业网银
     */
    private String payType; // 手续费

    private String remitComment; // 汇款备注码

    private String bankCode; // 银行编码 示例值：为保证充值正常入账，银行汇款时务必在附言或备注处填写此码；

    private String bankName; // 银行名称

    private String payerAccountNo; // 付款方账号

    private String payerAccountName; // 付款方开户名

    private String remark; // 备注

    private String finishTime; // 充值完成时间

    private String failReason; // 充值失败原因

    @Override
    public boolean validate() {
        return false;
    }
}
