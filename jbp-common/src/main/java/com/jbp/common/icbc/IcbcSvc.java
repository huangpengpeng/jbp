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
    protected static final String APIGW_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuCk62VzCuecgF7/g6H9v7xDM09JgvKHkMyW5ZnkCLKE3mtN+xKYSPsn3HfKlQAmEOqZoHAF5pzO0owU9KzOyi5nCN/+tlSHceX3oNDV4rEg/768RiuC2mJh+db83mWYqZzH/5g7oxWwLYmfIuzyMuW31o2rC2PgXwtnn6Zua1GrFOkz57eKJgn67Nv8NL6qM75jsOGuBAMx4dIGHYte3nR0wHhy//OAtE+rhqb2aF2nBC5O7A8pPbfnjMLEswqENhApWXyNDmj+wqFDmd4IBPnDukwgXno4MwmXnZdi/ZzGsuZNJpbRrPWn8rcicSF4B/IGbUTMXiYXBAS/oG+0MlQIDAQAB";
    // 2、密钥对认证方式，公钥在API平台登记，此处为私钥
    protected static final String MY_PRIVATE_KEY = "MIIEpAIBAAKCAQEAuCk62VzCuecgF7/g6H9v7xDM09JgvKHkMyW5ZnkCLKE3mtN+xKYSPsn3HfKlQAmEOqZoHAF5pzO0owU9KzOyi5nCN/+tlSHceX3oNDV4rEg/768RiuC2mJh+db83mWYqZzH/5g7oxWwLYmfIuzyMuW31o2rC2PgXwtnn6Zua1GrFOkz57eKJgn67Nv8NL6qM75jsOGuBAMx4dIGHYte3nR0wHhy//OAtE+rhqb2aF2nBC5O7A8pPbfnjMLEswqENhApWXyNDmj+wqFDmd4IBPnDukwgXno4MwmXnZdi/ZzGsuZNJpbRrPWn8rcicSF4B/IGbUTMXiYXBAS/oG+0MlQIDAQABAoIBAQCaV0uP0bMM9Iwr+06169/WnuDbAay7Sn6i8xHPtMjCuJaOdoP+sSQHZWJqweoGojMkqEQGfKIFJEtWeNSL+XbAkqt4HRrQKqHJXGEYKFwrHx4edT2hg7TkbKFHc2tYb4xIElph58rjciqUXWCYsyVJXsOIIriL+FiJn/BCE3wOWsTZtjA6vwzoMlqrk0tZMsyQp9YMHVA5NTyacIF/hvzzLT1dHf0GE34VlT7lTuwr3Wm8VOdwg5g1OENKfkevU4cNszctjqBnWVMe";
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

    // 270606e0ec7002eb6ec4e82547a27673

    public static void main(String[] args) {
        getKey();
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
    public void payOrder(String orderId, String payMode, String goodsName, String userIp, BigDecimal orderAmount) {
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
