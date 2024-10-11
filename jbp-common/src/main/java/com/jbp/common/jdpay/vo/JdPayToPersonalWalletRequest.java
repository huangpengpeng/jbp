package com.jbp.common.jdpay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class JdPayToPersonalWalletRequest implements Serializable {

    public JdPayToPersonalWalletRequest(String merchantTradeNo,  String xid, String tradeAmount, String merchantTradeDesc) {
        this.merchantTradeNo = merchantTradeNo;
        this.xid = xid;
        this.tradeAmount = tradeAmount;
        this.merchantTradeDesc = merchantTradeDesc;
    }

    private String merchantNo;    // 商户号（12位数字，由京东侧分配）
    private String reqNo;    // 	商户侧生成唯一请求号
    private String merchantTradeNo;    // 商户保证全局唯一，交易唯一请求号
    private String appKey;    // 	宙斯平台注册的appKey
    private String xid;    // 	宙斯授权登录获取的xid
    private String tradeAmount;    // 	单位分
    private String merchantChannelCode = "waichangShanghu";
    private String merchantTradeDesc;    // 交易描述
}
