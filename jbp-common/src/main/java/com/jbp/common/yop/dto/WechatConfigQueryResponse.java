package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WechatConfigQueryResponse extends BaseResponseV1 {


    /**
     *     ACCEPT:请求成功
     */
    private String status;

    /**
     *  配置结果
     * reportMerchantNo 报备商户号
     * tradeAuthDirList 支付授权目录列表
     * tradeAuthDir 支付授权目录
     * status 配置状态
     * failReason 失败原因
     * appId 支付appId
     * appSecret 支付appSecret
     * appIdType appId类型
     * subscribeAppId 推荐关注appId
     */
    private String configResult;

}
