package com.jbp.common.jdpay.util;

import com.google.gson.reflect.TypeToken;
import com.jbp.common.jdpay.sdk.JdPayConfig;
import com.jbp.common.jdpay.sdk.JdPayConstant;
import com.jbp.common.jdpay.sdk.JdPaySecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*************************************************
 *
 * 京东支付接口工具类
 *
 *************************************************/
public class JdPayApiUtil {
    /**
     * 日志
     */
    public static final Logger logger = LoggerFactory.getLogger("jdPaySdk");

    /**
     * 加密和签名
     */
    public static String encryptAndSignature(JdPayConfig jdPayConfig, String reqNo, String jsonParam) throws IOException {
        // 组装公共请求参数
        Map<String, String> commonParam = fillCommonParam(jdPayConfig.getMerchantNo(), reqNo);
        // 加密
        byte[] dataBytes = jsonParam.getBytes(JdPayConstant.UTF8);
        JdPaySecurity se = new JdPaySecurity();


        String encData = se.signEnvelop(jdPayConfig.getPriCert(), jdPayConfig.getPriCertPwd(), jdPayConfig.getPubCert(), dataBytes);
        commonParam.put(JdPayConstant.ENC_DATA, encData);
        // 签名
        String sign = SignUtil.sign(commonParam, JdPayConstant.SHA256, jdPayConfig.getSignKey(), JdPayConstant.UTF8);
        commonParam.put(JdPayConstant.SIGN_DATA, sign);
        return GsonUtil.toJson(commonParam);
    }

    /**
     * 解密和验签
     */
    public static String decryptAndVerifySign(JdPayConfig jdPayConfig, String respText) throws Exception {
        Map<String, String> respMap = GsonUtil.fromJson(respText, new TypeToken<Map<String, String>>() {
        });
        if (respMap == null ){
            //
        }
        String code = respMap.get(JdPayConstant.CODE);
        if (!JdPayConstant.SUCCESS_CODE.equals(code)){
            return respText;
        }
        if ( !respMap.containsKey(JdPayConstant.SIGN_DATA)) {
            throw new Exception(String.format("No sign field in response: %s", respText));
        }
        String sign = respMap.remove(JdPayConstant.SIGN_DATA);
        String signType = respMap.get(JdPayConstant.SIGN_TYPE);
        String charset = respMap.get(JdPayConstant.CHARSET);
        boolean isRespSignValid = SignUtil.verify(sign, respMap, signType, jdPayConfig.getSignKey(), charset);
        if (!isRespSignValid) {
            throw new Exception(String.format("Invalid sign value in response: %s", respText));
        }
        return SignUtil.decodeBase64(respMap.get(JdPayConstant.RESP_DATA), respMap.get(JdPayConstant.CHARSET), false, false);
    }


    public static String decryptAndVerifySignT(String respText) throws Exception {
        Map<String, String> respMap = GsonUtil.fromJson(respText, new TypeToken<Map<String, String>>() {
        });
        if (respMap == null ){
            //
        }
        String code = respMap.get(JdPayConstant.CODE);
        if (!JdPayConstant.SUCCESS_CODE.equals(code)){
            return respText;
        }
        if ( !respMap.containsKey(JdPayConstant.SIGN_DATA)) {
            throw new Exception(String.format("No sign field in response: %s", respText));
        }
        String sign = respMap.remove(JdPayConstant.SIGN_DATA);
        String signType = respMap.get(JdPayConstant.SIGN_TYPE);
        String charset = respMap.get(JdPayConstant.CHARSET);
        boolean isRespSignValid = SignUtil.verify(sign, respMap, signType, "851a729a6fe6bc43319a6dcebfa402915baf5764a442d4120b2c1719781a129b", charset);
        if (!isRespSignValid) {
            throw new Exception(String.format("Invalid sign value in response: %s", respText));
        }
        return SignUtil.decodeBase64(respMap.get(JdPayConstant.RESP_DATA), respMap.get(JdPayConstant.CHARSET), false, false);
    }




    public static void main(String[] args) throws Exception {
        String  respText = "{\"formatType\":\"JSON\",\"respData\":\"eyJ0cmFkZVR5cGUiOiJBR0dSRSIsInRyYWRlU3RhdHVzIjoiRklOSSIsInBheVRvb2wiOiJXWCIsInRyYWRlTm8iOiIyMDIzMTExNTE0MjYzNTIwMTA2MDA4NTI2NjI0OTQiLCJyZXN1bHRDb2RlIjoiMDAwMCIsImZpbmlzaERhdGUiOiIyMDIzMTExNTE0MjcwOCIsIm91dFRyYWRlTm8iOiIxODY5NDU4MTU3MDk5MTk2NDU4NDkiLCJyZXN1bHREZXNjIjoi5oiQ5YqfIiwiYmFua1N1Ym1pdE5vIjoiMjAyMzExMTUxNDkxNzEyNzAxNDkwMjU5IiwidHJhZGVBbW91bnQiOiIxIiwiY3VycmVuY3kiOiJDTlkiLCJiYW5rQ29kZSI6Ik9USEVSUyJ9\",\"desc\":\"成功\",\"encType\":\"AP7\",\"signType\":\"SHA-256\",\"charset\":\"UTF-8\",\"code\":\"00000\",\"merchantNo\":\"138436184003\",\"signData\":\"189A146A44D43F04F20C8F2142087BB8BB10F24A97F1FC7519CE9D043B2AE095\"}";
        System.out.println( decryptAndVerifySignT(respText) );
    }
    /**
     * 组装api公共参数赋值
     */
    private static Map<String, String> fillCommonParam(String merchantNo, String reqNo) {

        Map<String, String> reqMap = new HashMap<String, String>();
        // 二级商户号
        reqMap.put(JdPayConstant.MERCHANT_NO, merchantNo);
        //商户生成的唯一标识，可以与outTradeNo一致
        reqMap.put(JdPayConstant.REQ_NO, reqNo);
        //字符集
        reqMap.put(JdPayConstant.CHARSET, JdPayConstant.UTF8);
        //固定值
        reqMap.put(JdPayConstant.FORMAT_TYPE, JdPayConstant.JSON);
        //签名类型
        reqMap.put(JdPayConstant.SIGN_TYPE, JdPayConstant.SHA256);
        //固定值，证书加密
        reqMap.put(JdPayConstant.ENC_TYPE, JdPayConstant.AP7);
        return reqMap;
    }

}
