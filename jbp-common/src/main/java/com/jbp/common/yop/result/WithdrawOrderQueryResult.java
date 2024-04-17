package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawOrderQueryResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String requestNo; // 提现单号

    private String orderNo; // 易宝提现订单号

    private String merchantNo; // 商户编号

    private String orderAmount; // 提现金额

    private String receiveAmount;// 到账金额

    private String debitAmount; // 返回易宝账户扣账金额（包含提现金额和手续费（若有））

    private String orderTime;// 提现下单时间 示例值：2020-06-01 01:00:00

    private String finishTime;// 返回提现订单有明确结果 （如订单状态为SUCCESS/FAIL）时的时间 示例值： 2020-06-01 01:00:00

    /**
     * 提现订单状态
     * 可选项如下:
     * REQUEST_RECEIVE:请求已接收（易宝正在处理中，收到最终结果前请勿重复下单）
     * REQUEST_ACCEPT:请求已受理（易宝正在处理中，收到最终结果前请勿重复下单）
     * SUCCESS:已到账
     * FAIL:失败（该笔订单付款失败,可重新发起付款）
     * REMITING:银行处理中（银行正在处理中，收到最终结果前请勿重复下单）
     */
    private String status;

    private String failReason; //  失败原因当提现失败时，会返回失败原因

    private String feeUndertakerMerchantNo;//  手续费承担方商编  平台商承担时返回平台商商编商户承担时返回商户编号


    private String fee; // 手续费
    /**
     * 到账类型
     * 可选项如下:
     * REAL_TIME:实时
     * TWO_HOUR:2小时到账
     * NEXT_DAY:次日到账（无特殊情况资金于次日上午7点左右到提现银行账户中）
     * accountName必填
     */
    private String receiveType;

    private String accountName; //  返回商户的提现账户-开户名称

    private String accountNo; // 银行账号

    private String bankName; //  开户行名称

    private String bankCode; // 开户行编码

    private String branchBankCode;// 支行编码

    private String remark;


    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
