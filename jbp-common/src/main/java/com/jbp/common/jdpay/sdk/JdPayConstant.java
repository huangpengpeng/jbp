package com.jbp.common.jdpay.sdk;

import org.apache.http.client.HttpClient;

public class JdPayConstant {

    /**
     * 京东支付统一收单 url
     **/
    public static final String CREATE_ORDER_URL = "/api/createOrder"; // 生产
//    public static final String CREATE_ORDER_URL = "/api/createOrderYf"; // 预发

    /**
     * 三方聚合统一收单 url
     **/
    public static final String AGGREGATE_CREATE_ORDER_URL = "/api/createAggregateOrder";

    /**
     * 交易查询 url
     **/
    public static final String TRADE_QUERY_URL = "/api/queryOrder";
    /**
     * 退款申请 url
     **/
//    public static final String REFUND_URL = "/api/refund";// 生产
    public static final String REFUND_URL = "/api/refundYf"; //预发

    /**
     * 退款查询 url
     **/
    public static final String REFUND_QUERY_URL = "/api/refundQuery";
    /**
     * 代扣 url
     **/
    public static final String AGREEMENT_PAY_URL = "/api/agreementPay";
    /**
     * 签约关系查询 url
     **/
    public static final String AGREEMENT_QUERY_URL = "/api/agreementQuery";
    /**
     * 解约申请 url
     **/
    public static final String AGREEMENT_CANCEL_URL = "/api/agreementCancel";

    /**
     * 无卡号签约
     */
    public static final String AGREEMENT_NEW_SIGN_URL = "/api/agreementNewSign";

    /**
     * 账户签约
     */
    public static final String AGREEMENT_SIGN_APPLY_URL = "/api/agreementSignApply";

    /**
     * 随机字符常量
     */
    public static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 账户分佣
     */
    public static final String SEND_COMMISSION_URL = "/api/sendCommission";


    /**
     * 公共字段常量 start
     **/
    public static final String CCM = "CCM";
    public static final String MERCHANT_NO = "merchantNo";
    public static final String REQ_NO = "reqNo";
    public static final String CHARSET = "charset";
    public static final String FORMAT_TYPE = "formatType";
    public static final String SIGN_TYPE = "signType";
    public static final String ENC_TYPE = "encType";
    public static final String UTF8 = "UTF-8";
    public static final String JSON = "JSON";
    public static final String SHA256 = "SHA-256";
    public static final String AP7 = "AP7";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String SIGN_DATA = "signData";
    public static final String ENC_DATA = "encData";
    public static final String RESP_DATA = "respData";
    public static final String CODE = "code";
    public static final String DESC = "desc";
    public static final String SUCCESS_CODE = "00000";
    /* 公共字段常量 end */


    /**
     * http请求常量 start
     **/
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String UA = "User-Agent";
    public static final String PKCS12 = "PKCS12";
    public static final String TLS = "TLS";
    public static final String APPLICATION_JSON = "application/json";
    public static final String USER_AGENT = "jdPay" +
            " (" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
            ") Java/" + System.getProperty("java.version") + " HttpClient/" + HttpClient.class.getPackage().getImplementationVersion();
    /* http请求常量 end */

    public static final String URL_PATH = "/api/";

}
