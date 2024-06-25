package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountPayOrderQueryResult extends BaseYopResponse {

    private String returnCode; // UA00000

    private String returnMsg;

    private String requestNo;// 商户请求号

    private String orderNo;//易宝付款订单号

    private String merchantNo;// 商户编号

    private String orderAmount;//付款金额

    private String receiveAmount; //到账金额 返回收款银行账户入账金额

    private String debitAmount; // 返回易宝账户扣账金额（包含付款金额和手续费（若有））

    private String orderTime; // 付款下单时间 2020-06-01 00:00:00

    private String finishTime; //  返回付款订单有明确结果（如订单状态为SUCCESS/FAIL）时的时间

    private String fee; //  手续费

    private String feeUndertakerMerchantNo; // 手续费承担方商编

    /**
     * 可选项如下:
     * REQUEST_RECEIVE:请求已接收（易宝正在处理中，收到最终结果前请勿重复下单）
     * REQUEST_ACCEPT:请求已受理（易宝正在处理中，收到最终结果前请勿重复下单）
     * SUCCESS:已到账
     * REMITING:银行处理中（银行正在处理中，收到最终结果前请勿重复下单）
     * FAIL:失败（该笔订单付款失败,可重新发起付款）
     * CANCELED:订单已撤销
     */
    private String status;
    private String failReason; // 失败原因当付款失败时，会返回失败原因


    /**
     * 可选项如下:
     * REAL_TIME:实时
     * TWO_HOUR:2小时到账
     * NEXT_DAY:次日到账（无特殊情况资金于次日上午7点左右到收款银行账户中）
     */
    private String receiveType; // 到账类型

    private String receiverAccountNo; // 返回收款账户-银行账号

    private String receiverAccountName; //  收款方开户名

    private String receiverBankCode; //  收款方开户行编码

    private String branchBankCode;//   支行编码

    private String comments; // 银行附言

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
