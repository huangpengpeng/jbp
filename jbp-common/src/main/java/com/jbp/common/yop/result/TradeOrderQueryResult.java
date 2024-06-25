package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.dto.PayerInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
@NoArgsConstructor
public class TradeOrderQueryResult extends BaseYopResponse {

    private String code;
    private String message;

    private String parentMerchantNo; // 交易发起方商编

    private String merchantNo; // 收款商户编号

    private String orderId; // 商户收款请求号;

    private String uniqueOrderNo; // 易宝收款订单号

    /**
     * 订单状态
     * PROCESSING：订单待支付
     * SUCCESS：订单支付成功
     * TIME_OUT：订单已过期
     * FAIL:订单支付失败
     * CLOSE:订单关闭
     */
    private String status;

    private String orderAmount; // 订单金额.单位:元


    private String payAmount; // 用户支付金额,单位:元

    private String merchantFee;  // 商户手续费

    private String paySuccessDate; // 示例值：2021-01-01 00:00:00

    private String memo;  // 对账备注

    /**
     * USER_SCAN：用户扫码MERCHANT_SCAN：商家扫码JS_PAY：JS支付MINI_PROGRAM：小程序支付WECHAT_OFFIACCOUNT：微信公众号支付ALIPAY_LIFE：生活号支付FACE_SCAN_PAY：刷脸支付SDK_PAY：SDK支付H5_PAY：H5支付
     * ONEKEYPAY：一键支付
     * BINDCARDPAY：绑卡支付
     * E_BANK：网银支付
     * ENTERPRISE_ACCOUNT_PAY：企业账户支付
     * ACCOUNT_BOOK_PAY: 记帐簿支付
     */
    private String payWay; // 支付方式

    private String token; // 支付授权token，用于调用支付

    /**
     * 分账订单标识:
     * DELAY_SETTLE:分账
     * REAL_TIME:不分账
     */
    private String fundProcessType; // 分账都订单标识

    /**
     * 支付机构在银网联侧的单号,该单号也是支付机构在微信侧的外部商户订单号.
     * 使用场景:
     * (1) 跨境报关
     * (2) 服务商用于点金计划商户小票功能
     */
    private String bankOrderId; // 银行订单号


    /**
     * 渠道订单号
     * 该笔订单在微信、支付宝或银行侧系统生成的单号
     */
    private String channelOrderId;

    /**
     * 渠道类型:
     * WECHAT：微信
     * ALIPAY：支付宝
     * UNIONPAY：银联
     * APPLEPAY：苹果支付
     * DCEP:数字人民币
     * 仅聚合支付会返回该参数
     */
    private String channel;

    private String realPayAmount; // 用户实际支付金额

    private String unSplitAmount; // 剩余可分账金额;(用于分账的场景)

    private String failReason; // 支付失败的失败原因

    private String failCode; // 支付失败的code码

    private PayerInfoDto payerInfo;

    public boolean validate() {
        if (StringUtils.equals(this.getCode(), "OPR00000")) {
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }

    public boolean ifSuccess() {
        if (this == null) {
            return false;
        }
        if (!validate()) {
            return false;
        }
        if ("SUCCESS".equals(this.status)) {
            return true;
        }
        return false;
    }
}
