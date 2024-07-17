package com.jbp.common.kqbill.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 快钱收银台
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KqCashierParams {

    //人民币网关账号，该账号为11位人民币网关商户编号+01,该参数必填。
    private String merchantAcctId;

    //编码方式，1代表 UTF-8; 2 代表 GBK; 3代表 GB2312 默认为1,该参数必填。
    private String inputCharset = "1";

    //returnUrl 支付成功跳转地址
    private String pageUrl;

    //returnUrl 支付结果同志地址
    private String bgUrl;

    //网关版本，固定值：mobile1.0,该参数必填。
    private String version = "mobile1.0";

    //移动网关版本 phone代表手机版移动网关，pad代表平板移动网关，默认为phone
    private String mobileGateway = "phone";

    //语言种类，1代表中文显示，2代表英文显示。默认为1,该参数必填。
    private String language = "1";

    //签名类型,该值为4，代表PKI加密方式,该参数必填。
    private String signType = "4";

    //付款人标识，可以为空  系统账户
    private String payerId;

    //付款人IP，可以为空
    private String payerIP;

    //商家的终端ip，支持Ipv4和Ipv6 不参与签名
    private String terminalIp;

    //网络交易平台简称  后台配置
    private String tdpformName;

    //商户订单号
    private String orderId;

    //订单金额，金额以“分”为单位
    private String orderAmount;

    //订单提交时间，格式：yyyyMMddHHmmss
    private String orderTime;

    //快钱时间戳，格式：yyyyMMddHHmmss
    private String orderTimestamp;
    ;

    //商品名称，不可为空。
    private String productName;

    private String ext1;

    private String ext2;

    //支付方式，一般为00
    private String payType = "00";

    // 同一订单禁止重复提交标志，实物购物车填1，虚拟产品用0。1代表只能提交一次，
    // 0代表在支付不成功情况下可以再提交。可为空。
    private String redoFlag = "0";

    private String signMsg;

}
