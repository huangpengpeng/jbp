package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 来账通开户
 */
@Data
@EqualsAndHashCode
public class LztOpenacctApplyParams {

    public LztOpenacctApplyParams(String user_id, String txn_seqno, String notify_url, String business_type, String shop_id,
                                  String shop_name, String province, String city, String area, String address) {
        this.user_id = user_id;
        this.txn_seqno = txn_seqno;
        this.notify_url = notify_url;
        this.business_type = business_type;
        this.shop_id = shop_id;
        this.shop_name = shop_name;
        this.province = province;
        this.city = city;
        this.area = area;
        this.address = address;
    }

    private String timestamp;

    private String oid_partner;

    /**
     * 外部用户号
     */
    private String user_id;

    /**
     * 用户：USER 平台商户：MERCHANT
     */
    private String user_type;

    /**
     * 用户账户：USEROWN 平台商户自有资金账户：MCHOWN
     */
    private String acct_type;

    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;

    /**
     * 商户系统交易时间格式：yyyyMMddHHmmss
     */
    private String txn_time;

    /**
     * 商户系统交易时间格式：yyyyMMddHHmmss
     */
    private String return_url;

    /**
     * 交易结果异步通知接收地址，建议HTTPS协议
     */
    private String notify_url;

    /**
     * 业务类型:DOUYIN 抖音
     */
    private String business_type;

    /**
     * 店铺id
     */
    private String shop_id;

    /**
     * 店铺名称
     */
    private String shop_name;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String area;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String memo;

    /**
     * 仅支持单个传入，支持范围根据商户实际配置，目前支持枚举为xwbank, onebank
     */
    private String open_bank;
}
