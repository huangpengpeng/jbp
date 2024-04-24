package com.jbp.common.icbc;

import com.alibaba.fastjson.JSON;
import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.IcbcApiException;
import com.icbc.api.IcbcConstants;
import com.icbc.api.request.CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1;
import com.icbc.api.request.CardbusinessAggregatepayB2cOnlineMerrefundRequestV1;
import com.icbc.api.request.CardbusinessAggregatepayB2cOnlineOrderqryRequestV1;
import com.icbc.api.request.CardbusinessAggregatepayB2cOnlineRefundqryRequestV1;
import com.icbc.api.response.CardbusinessAggregatepayB2cOnlineConsumepurchaseResponseV1;
import com.icbc.api.response.CardbusinessAggregatepayB2cOnlineMerrefundResponseV1;
import com.icbc.api.response.CardbusinessAggregatepayB2cOnlineOrderqryResponseV1;
import com.icbc.api.response.CardbusinessAggregatepayB2cOnlineRefundqryResponseV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工行api
 */
@Slf4j
@Component
public class IcbcSvc {

    // 1、网关公钥
    protected static final String APIGW_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwFgHD4kzEVPdOj03ctKM7KV+16bWZ5BMNgvEeuEQwfQYkRVwI9HFOGkwNTMn5hiJXHnlXYCX+zp5r6R52MY0O7BsTCLT7aHaxsANsvI9ABGx3OaTVlPB59M6GPbJh0uXvio0m1r/lTW3Z60RU6Q3oid/rNhP3CiNgg0W6O3AGqwIDAQAB";
    // 2、密钥对认证方式，公钥在API平台登记，此处为私钥
    protected static final String MY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDfV8piJL/5Pc/ZdCMBcX1mVrLQo6rRBdqLOnYkU9xnVp1EdhMkx1wcP1BDWTo0SqupMFwZlsTH5t6ywdlD4sXvkyfCkuSSShg+ZGGLRKmDPoLOEP1KZ/SFwnIGb6hj30OXyhsmArVYfYr7qAlo0GqfQrDUAC2ZbasESLHlVvqMy48ssp2QcOPtZoWW/diQY1HJR+RMS9Kjg9oZr/dU5UloZQsDQVXmYBuAqU4RcBfEqV56dFu/suJkGrw0LenDPxI2QLJ5c51rkhx9lC7xBZigfWOnDLH6xoT1ChdcY9bP5xt/GjP5NYuI8xI2sGGTUPoUKxdrH6aiI9jp728+K1y5AgMBAAECggEAbJQsktwU7GHti2UXo5r+AOPDWQVIhQfYgHlyeCTA8Qg9usvAcM/u6tio96UIU+W9YKpfDB2tGxYVTEhLjOJRojAjU0fAkZIuCR8aAO/njSO1yeKekS7KxMCMWK6t6afgH4ok+qy0ZwnZqJC/ylIQk86DUv2nLYEQdCu3OKy5b/qZ1qA7yaCiG/D4HBThgiOifV2Yq1TCtvC2iv5mcuhH4iTXexeOQcbZepZlQnyXiVAlTYRAeo+ng8ub01NJZ4njPe9boKeqrpmMAOLN/gRTjh6yZ+90+hniXLgznOVPg+GxUbff8pVDd01POGVsid0f5Gr/TvEnixJV9nM70SCp0QKBgQD1C/oCC0mC9T8yZrrzkKZ5gsWpxElGYFvU/S1LdDsfGioRLLBJ8k4PvQcJN+pB1Ea2b8s01HQKWarXGYKQmu+dGsULbv9UpaVwH3Of+gt35Wo2+Fuh0bhcS58Ct41IGQh5leI5ckNq9iB9/x6zWQFeAEpUnXqIwFYkNCZIPbgmRQKBgQDpU3YAvKXDCesL9W4JhoWhyGBJ94frOq8hiH3vbr1xUpqDkJ9aovMDWy5f77E5Vuva/mEDxIpQrFTSA4clKj6T8E6CBiEMStP2DWLQsyC3AxDKv3g5lXab3IH4KtxjNCwadp+TRmRHWG09FLdt14AeS4El14xdhlGx6FsYncst5QKBgFBGHR9gTTOeXZaIOsQhZbe2lEQZ7hsk49BxI85tBBUbQB6iMhn3S4UyWkS10YLBJG0NUFc9JcpiN2oBjFkMuGQR6ezl7rTvErQZSYploi4jtFjPoUzwY+GwUCXWtWyh7rnN1O8WtGksudYspgUAqkb991uivwpfX5i6kLPnrBS1AoGALe8WXhLFd14ufc41eX6YND9kZWtrwK1u6OUcFdTxSqv+a0Q/evJ1cQW0XYKsmyM3j4dgxgMdT8B9elLjejeU1j8K1aIrQ2Y/0ELWX0vEdwMNfTywiHWaQhjpJVgaxxTwUc1koPPMrhcEem/npKI2QMCQjkifA5J75tBdjr0R0NkCgYEA1eUVZW1zEXB79xf2GREbPi1UeQVfIvTqOQK8fa3O0Xdrdd//BFHy44eqSrg5eG0t78wbFtkwYHUIbQZOd0L9qp6yPIk2bqldKoqUxiXPjGX4QR1XgenbWjc+cLr//EN2zRqTLrd3K2e0V/Hx+6cL14/0DB73Ma7oyZ6rMKR2JYU=";
    // 3、appid
    protected static final String APP_ID = "xxxxxxxxx";

    protected static final String HOST = "";

    // 商户编号
    protected static final String MER_ID = "";

    // 收单产品协议编号
    protected static final String MER_PRTCL_NO = "";

    // 微信appid
    private static final String WECHAT_APP_ID="";

    // 工行appid
    private static final String ICBC_APP_ID="";


    /**
     * 线上POS聚合消费下单接口
     * payMode  支付方式，9-微信；10-支付宝；13-云闪付
     * accessType 收单接入方式，5-APP，7-微信公众号，8-支付宝生活号，9-微信小程序
     */
    public void payOrder(String orderId, String payMode, String goodsName, String userIp, BigDecimal orderAmount) {
        //1.请求客户端
        DefaultIcbcClient client = new DefaultIcbcClient(APP_ID, IcbcConstants.SIGN_TYPE_RSA, MY_PRIVATE_KEY, APIGW_PUBLIC_KEY);
        // 2.返回结果
        CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1 request = new CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1();
        // 3.请求url
        request.setServiceUrl(HOST + "/api/cardbusiness/aggregatepay/b2c/online/consumepurchase/V1");
        CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1.CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1Biz bizContent = new CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1.CardbusinessAggregatepayB2cOnlineConsumepurchaseRequestV1Biz();
        // 4.业务参数
        request.setBizContent(bizContent);

        bizContent.setMer_id(MER_ID); // 商户编号
        bizContent.setOut_trade_no(orderId);
        bizContent.setPay_mode(payMode);
        bizContent.setAccess_type("5");
        bizContent.setMer_prtcl_no(MER_PRTCL_NO);
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = df.format(now);
        bizContent.setOrig_date_time(time);
        String decive_info = "设备号";
        bizContent.setDecive_info("xxxx");
        bizContent.setBody(goodsName);
        bizContent.setFee_type("001"); // 人民币
        bizContent.setSpbill_create_ip(userIp);
        bizContent.setTotal_fee(String.valueOf(orderAmount.multiply(BigDecimal.valueOf(100)).intValue()));
        bizContent.setMer_url("www.baidu.com");
        if ("9".equals(payMode)) {
            bizContent.setShop_appid(WECHAT_APP_ID);
        }
        bizContent.setIcbc_appid(ICBC_APP_ID);
        bizContent.setExpire_time("36000");
        bizContent.setNotify_type("AG"); // 不通知  HS 主动通知
        bizContent.setOrder_apd_inf("在线支付"); // 订单附加信息
        CardbusinessAggregatepayB2cOnlineConsumepurchaseResponseV1 response;
        try {
            response = client.execute(request, System.currentTimeMillis() + ""); //msgId消息通讯唯一编号，要求每次调用独立生成，APP级唯一
            if (response.getReturnCode() == 0) {
                // 6、业务成功处理，请根据接口文档用response.getxxx()获取同步返回的业务数据
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + JSON.toJSONString(response));
            } else {
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + JSON.toJSONString(response));
                log.info("ReturnMsg:" + response.getReturnMsg());
            }
        } catch (IcbcApiException e) {
            e.printStackTrace();
        }
    }


    public void payOrderQuery(String orderId) {
        //1.请求客户端
        DefaultIcbcClient client = new DefaultIcbcClient(APP_ID, IcbcConstants.SIGN_TYPE_RSA, MY_PRIVATE_KEY, APIGW_PUBLIC_KEY);
        // 2.返回结果
        CardbusinessAggregatepayB2cOnlineOrderqryRequestV1 request = new CardbusinessAggregatepayB2cOnlineOrderqryRequestV1();
        // 3.请求url
        request.setServiceUrl(HOST+"/api/cardbusiness/aggregatepay/b2c/online/orderqry/V1");
        CardbusinessAggregatepayB2cOnlineOrderqryRequestV1.CardbusinessAggregatepayB2cOnlineOrderqryRequestV1Biz bizContent = new CardbusinessAggregatepayB2cOnlineOrderqryRequestV1.CardbusinessAggregatepayB2cOnlineOrderqryRequestV1Biz();
        request.setBizContent(bizContent);
        // 4.业务参数
        bizContent.setOut_trade_no(orderId);
        bizContent.setDeal_flag("0");
        bizContent.setIcbc_appid(ICBC_APP_ID);
        bizContent.setMer_id(MER_ID);
        bizContent.setMer_prtcl_no(MER_PRTCL_NO);

        CardbusinessAggregatepayB2cOnlineOrderqryResponseV1 response;
        try {
            response = client.execute(request, System.currentTimeMillis() + "");//msgId消息通讯唯一编号，要求每次调用独立生成，APP级唯一
            if (response.getReturnCode() == 0) {
                // 6、业务成功处理，请根据接口文档用response.getxxx()获取同步返回的业务数据
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
            } else {
                // 失败
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
                log.info("ReturnMsg:" + response.getReturnMsg());
            }
        } catch (IcbcApiException e) {
            e.printStackTrace();
        }
    }


    public void refundOrder(String orderId, String refundId, BigDecimal refundAmt) {

        // 1、请求客户端
        DefaultIcbcClient client = new DefaultIcbcClient(APP_ID, IcbcConstants.SIGN_TYPE_RSA, MY_PRIVATE_KEY,
                APIGW_PUBLIC_KEY);
        // 2.返回结果
        CardbusinessAggregatepayB2cOnlineMerrefundRequestV1 request = new CardbusinessAggregatepayB2cOnlineMerrefundRequestV1();
        // 3.请求url
        String host= "";
        request.setServiceUrl(host+"/api/cardbusiness/aggregatepay/b2c/online/merrefund/V1");
        CardbusinessAggregatepayB2cOnlineMerrefundRequestV1.CardbusinessAggregatepayB2cOnlineMerrefundRequestV1Biz bizContent = new CardbusinessAggregatepayB2cOnlineMerrefundRequestV1.CardbusinessAggregatepayB2cOnlineMerrefundRequestV1Biz();
        // 4、业务参数
        request.setBizContent(bizContent);
        bizContent.setMer_id(MER_ID);//商户编号
        bizContent.setIcbc_appid(ICBC_APP_ID);
        bizContent.setOut_trade_no(orderId);//商户订单号
        bizContent.setOuttrx_serial_no(refundId);//外部退货流水号
        bizContent.setRet_total_amt(String.valueOf(refundAmt.multiply(BigDecimal.valueOf(100)).intValue()));//退货总金额
        bizContent.setTrnsc_ccy("001");//交易币种
        CardbusinessAggregatepayB2cOnlineMerrefundResponseV1 response;
        try {
            response = client.execute(request, System.currentTimeMillis() + "");// msgId消息通讯唯一编号，要求每次调用独立生成，APP级唯一
            if (response.getReturnCode() == 0) {
                // 6、业务成功处理，请根据接口文档用response.getxxx()获取同步返回的业务数据
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
            } else {
                // 失败
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("ReturnMsg:" + response.getReturnMsg());
            }
        } catch (IcbcApiException e) {
            e.printStackTrace();
        }
    }

    public void refundOrderQuery(String orderId, String refundId) {
        // 1、请求客户端
        DefaultIcbcClient client = new DefaultIcbcClient(APP_ID, IcbcConstants.SIGN_TYPE_RSA, MY_PRIVATE_KEY,
                APIGW_PUBLIC_KEY);
        // 2.返回结果
        CardbusinessAggregatepayB2cOnlineRefundqryRequestV1 request = new CardbusinessAggregatepayB2cOnlineRefundqryRequestV1();
        // 3.请求url
        request.setServiceUrl("http://ip:port/api/cardbusiness/aggregatepay/b2c/online/refundqry/V1");
        CardbusinessAggregatepayB2cOnlineRefundqryRequestV1.CardbusinessAggregatepayB2cOnlineRefundqryRequestV1Biz bizContent = new CardbusinessAggregatepayB2cOnlineRefundqryRequestV1.CardbusinessAggregatepayB2cOnlineRefundqryRequestV1Biz();
        // 4、业务参数
        request.setBizContent(bizContent);
        bizContent.setMer_id(MER_ID);
        bizContent.setOut_trade_no(orderId);
        bizContent.setOuttrx_serial_no(refundId);
        bizContent.setMer_prtcl_no(MER_PRTCL_NO);
        CardbusinessAggregatepayB2cOnlineRefundqryResponseV1 response;
        try {
            boolean testFlag = true;
            response = client.execute(request, System.currentTimeMillis() + "");// msgId消息通讯唯一编号，要求每次调用独立生成，APP级唯一
            if (response.getReturnCode() == 0) {
                // 6、业务成功处理，请根据接口文档用response.getxxx()获取同步返回的业务数据
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
            } else {
                // 失败
                testFlag = false;
                log.info("response:" + com.icbc.api.internal.util.internal.util.fastjson.JSON.toJSONString(response));
                log.info("ReturnCode:" + response.getReturnCode());
                log.info("ReturnMsg:" + response.getReturnMsg());
            }
        } catch (IcbcApiException e) {
            e.printStackTrace();
        }
    }


}
