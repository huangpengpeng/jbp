package com.jbp.common.jdpay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class JdPayToPersonalWalletResponse implements Serializable {

    private String code;//	00000表示响应成功，其他请参考返回业务响应编码返回业务响应编码

    private String info;//		根据响应码具体的响应描述

    private String tradeNo;//		打款交易唯一编号
    /**
     * TRADE_PRO	打款中
     * TRADE_SUCC	打款成功
     * TRADE_FAIL	打款失败
     */
    private String merchantTradeStatus;//		状态详见数据字典

    private String resultCode;//	字段识别失败原因。

    public boolean ifSuccess() {
        if (this == null) {
            return false;
        }
        if (!"TRADE_FAIL".equals(this.merchantTradeStatus)) {
            return true;
        }
        return false;
    }
}
