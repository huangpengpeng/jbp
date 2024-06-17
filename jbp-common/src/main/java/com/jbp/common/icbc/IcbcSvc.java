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
import com.jbp.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工行api
 */
@Slf4j
@Component
public class IcbcSvc {

    // 1、网关公钥
    protected static final String APIGW_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMpjaWjngB4E3ATh+G1DVAmQnIpiPEFAEDqRfNGAVvvH35yDetqewKi0l7OEceTMN1C6NPym3zStvSoQayjYV+eIcZERkx31KhtFu9clZKgRTyPjdKMIth/wBtPKjL/5+PYalLdomM4ONthrPgnkN4x4R0+D4+EBpXo8gNiAFsNwIDAQAB";
    // 2、密钥对认证方式，公钥在API平台登记，此处为私钥
    protected static final String MY_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC3JdUM5iiHXKPRVuceWwQsxGCeWhgObm7nt9+9RHWa9BqcSF0q/OaAHFuDRPifnc6s4XGJ6KjzMTAa5RtpHIIjyJEBex5mAnMjBWl7v1G1S20r7BpRLy0aNDuQL9jRLj8L92HRuPX7PlBCmxSvONqRnm2uMYHQBFvYgtDkvs+aETlOSKGngTXqcWuM5PiIWEkDdYyLQRTM5vYKjYq0Lt06sgRt+8TUuAnvJan3xTtlt7p03+F93T93F1Wu3oRCyq5Y3rzFd9HxSU8AyNisaRJvQZcvcCCkcfyhH0VOkCl7/UvxqJTu5wf04m0HJ1uRWivNXz9xfEme3O21XAOTKAbNAgMBAAECggEADqB03WzcO8NISgW4u5HlSXX9iWgEl9J2BQeIVONMyS1JmsWkuzQrbHvXSyqM+KJvyRrQKIVuSM3a26sEf2aJdjYkbovYp6j2bQuXmwxyiBoYA/P9PhKYdYlKYH+Rv1MEMf51z3wIGSKm4krkYgIWYp7Wf5zwkx5l/2qjKEGc7t7ilFF8xRyQ8tSsKXqCLWKA34LUgWJH5Cx0OOjK6VglX+9yl4yjUTJ5/4nOcs7o0ryr/9GjEusShvGx+uLMj+t4ED+FA3uRNWX0cZTMWyuyOmPEUB7VJ6wn8ZqWHk9xIPCGX08LhS1SXRC0d2Qr6I29lhwy1mDxaLhgHaEKM56e+QKBgQDqjyQs4jA59NfhGC/wYxp0NBL8rjw2Z3F3rOYTB90akrJkHbWX0eNwh5DV3QrlI6IcNkcJjISbHctrt6SdQiJMOwXwksIP0tT1pdESQjavFW11eG761jBwzS9yFiydko5FU8JZUHajhXmwLLjkw2SiPmP42oFdolDIiN32PTqgYwKBgQDH46Bw7SmPHPFWHR+sr+tz3iuu3MjweJ17wXsQqPETvyRhqKUeWIhWZVdMl0le4j5ftllQPJBteEuTRF6c0YGMfMEpLX881pimEgegEfV5dFawild2Sda+UNRZx4Ya6KsGO8/8nFf1lYHmnlZvI5ILq0M58UfR+3tgdCOsQ5grDwKBgQCtMlT772i84tYlF78OOZ9m/qymd+FuKqPWQo0AsGXLIVcoJefY2tqeVPvVbwqEd/NT8aAypNel0jJKr6eVlyfMMikIotU46ezmFjJy0QGf9qqEexE3lsDeCiRmkYkQMQ9skZSIbqmrxPs940gDY2QDiR44ut+bTCdqa+W51SxdEQKBgQC/PUNp5y1Es88dsV11jRW6VEv8z1Ub5HnfRhwks016s/vtxzi6kL9X0Ts1luRmeBRu4/oNLvSLF3VO3zGZ3UORkmYHrHS4UyPWdxd+iNAPNEQgQSsui7R9fr27a44dPo8ptp3ls/rfhfuzFbfiOujmayM1U2eAOZcvj831aPIz9wKBgQCzHm/5enhYau6joSoBxG4irLcdB0J7Jwgy2jSkozF6iko5GxB1rpfbv+DMr856FLtGPnVzHyStaey8/a4cwDI3X77rg8aAuNgFfKRRXlNYiflrUxayyTLaOqU/cz6YdWEaDNEPorcwQ9RXsj3VUJCm+ZMnSXytU7TyeFuT+I97HA==";
    // 3、appid

    protected static final String APP_ID = "11000000000000024613";

    protected static final String HOST = "https://gw.open.icbc.com.cn";

    // 商户编号
    protected static final String MER_ID = "141080990049";

    // 收单产品协议编号 100000020802
    protected static final String MER_PRTCL_NO = "1410809900490201";

    // 微信appid
    private static final String WECHAT_APP_ID="";

    // 工行appid
    private static final String ICBC_APP_ID="11000000000000024613";

    // 270606e0ec7002eb6ec4e82547a27673

    public static void main(String[] args) {
        String orderId = "202405170000";
        payOrder( orderId, "13", "测试", "36.24.41.101", BigDecimal.valueOf(1));
    }
    /**
     * 随机生成秘钥
     */
    public static void getKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            //要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            String s = byteToHexString(b);
            System.out.println(s);
            System.out.println("十六进制密钥长度为"+s.length());
            System.out.println("二进制密钥的长度为"+s.length()*4);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("没有此算法。");
        }
    }

    /**
     * byte数组转化为16进制字符串
     * @param bytes
     * @return
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String strHex=Integer.toHexString(bytes[i]);
            if(strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if(strHex.length() < 2) {
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString();
    }




    /**
     * 线上POS聚合消费下单接口
     * payMode  支付方式，9-微信；10-支付宝；13-云闪付
     * accessType 收单接入方式，5-APP，7-微信公众号，8-支付宝生活号，9-微信小程序
     */
    public static void payOrder(String orderId, String payMode, String goodsName, String userIp, BigDecimal orderAmount) {
        //1.请求客户端
        DefaultIcbcClient client = new DefaultIcbcClient(APP_ID, IcbcConstants.SIGN_TYPE_RSA2, MY_PRIVATE_KEY, APIGW_PUBLIC_KEY);
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
        String decive_info = "100000020803";
        bizContent.setDecive_info(decive_info);
        bizContent.setBody(goodsName);
        bizContent.setFee_type("001"); // 人民币
        bizContent.setSpbill_create_ip(userIp);
        bizContent.setTotal_fee(String.valueOf(orderAmount.multiply(BigDecimal.valueOf(100)).intValue()));
        bizContent.setMer_url("https://adm.zhonghe88188.com");
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
