package com.jbp.common.yop.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WechatConfigQueryRequest extends SimpleYopRequest{

    public WechatConfigQueryRequest(String merchantNo, String appIdType) {
        setParentMerchantNo(merchantNo);
        this.merchantNo = merchantNo;
        this.appIdType = appIdType;
    }

    private String merchantNo;

    /**
     *             appId类型
     *     OFFICIAL_ACCOUNT:公众号
     *     MINI_PROGRAM:小程序
     */
    private String appIdType;



}
