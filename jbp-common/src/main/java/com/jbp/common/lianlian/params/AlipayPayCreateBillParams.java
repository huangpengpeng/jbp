package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收银台支付创单 请求参数
 */
@Data
@EqualsAndHashCode
public class AlipayPayCreateBillParams {
    private String oid_partner;
    private String sign;
    private String sign_type;
    private String user_id;
    /*
    虚拟商品销售：101001。
    实物商品销售：109001。当busi_partner与您的商户号的业务属性不相符时， 该次请求将返回请求无效
     */
    private String busi_partner;
    private String no_order;
    private String dt_order;
    private String name_goods;
    private String info_order;
    private String money_order;
    private String notify_url;
    private String risk_item;
    private String pay_type;
}
