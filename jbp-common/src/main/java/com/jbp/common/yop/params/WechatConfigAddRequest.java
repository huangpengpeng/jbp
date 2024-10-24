package com.jbp.common.yop.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WechatConfigAddRequest extends SimpleYopRequest{

    public WechatConfigAddRequest(String merchantNo) {
        this.setParentMerchantNo("10089066338");
        this.merchantNo = merchantNo;
        this.tradeAuthDirList = "[\"https://cash.yeepay.com/newwap/\",\"https://shouyin.yeepay.com/nc-cashier-wap/\"]";
        this.appIdList = "[{\"appId\":\"wx788c4a29e00f3f36\",\"appSecret\":\"f6dc4d7064e42721b9fd2f6f6114d4e4\",\"appIdType\":\"OFFICIAL_ACCOUNT\"}]";
    }

    private String merchantNo;

    private String tradeAuthDirList;

    private String appIdList;
}
