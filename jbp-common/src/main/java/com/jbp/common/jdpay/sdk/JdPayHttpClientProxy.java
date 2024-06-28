package com.jbp.common.jdpay.sdk;




import com.jbp.common.jdpay.util.JdPayApiUtil;


import java.security.SecureRandom;
import java.util.Random;

public class JdPayHttpClientProxy {

    private static final Random RANDOM = new SecureRandom();

    private JdPayConfig jdPayConfig;

    private JdPayHttpClient jdPayHttpClient;

    public JdPayHttpClientProxy(JdPayConfig jdPayConfig, JdPayHttpClient jdPayHttpClient) {
        this.jdPayConfig = jdPayConfig;
        this.jdPayHttpClient = jdPayHttpClient;
    }

    /**
     * 获取随机字符串，含数字和大小写英文字母
     */
    private static String genNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = JdPayConstant.SYMBOLS.charAt(RANDOM.nextInt(JdPayConstant.SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    public String execute(String urlSuffix, String request) throws Exception {
        long startTimestampMs = System.currentTimeMillis();
        String response;
        String httpResponse = null;
        // 接口名称
        String apiName = urlSuffix.replaceFirst(JdPayConstant.URL_PATH, "");
        // 唯一请求号
//        String reqNo = genNonceStr();
        String reqNo = "1703768114610";

        try {
            JdPayApiUtil.logger.info("1.{}接口请求参数:{}", apiName, request);
            // 请求参数加密和签名
            String httpRequest = JdPayApiUtil.encryptAndSignature(jdPayConfig, reqNo, request);
            JdPayApiUtil.logger.info("2.{}远程调用请求参数:{}", apiName, httpRequest);
            httpResponse = jdPayHttpClient.execute(jdPayConfig, urlSuffix, httpRequest);
            JdPayApiUtil.logger.info("3.{}远程调用返回参数:{}", apiName, httpResponse);
            // 验证和解析返回参数
            response = JdPayApiUtil.decryptAndVerifySign(jdPayConfig, httpResponse);
            JdPayApiUtil.logger.info("4.{}耗时:{},接口返回参数:{}", apiName, (System.currentTimeMillis() - startTimestampMs), response);
        } catch (Exception e) {
            long elapsedTimeMillis = System.currentTimeMillis() - startTimestampMs;
            JdPayApiUtil.logger.error("{}远程调用异常,接口参数:{}", apiName, request, e);
            // UnknownHostException  ConnectTimeoutException  SocketTimeoutException  HttpException ...
            String exceptionClassName = e.getClass().getName();
//            JdPayReport.getInstance(jdPayConfig).report(reqNo, urlSuffix, elapsedTimeMillis,
//                    httpResponse, JdPayException.class.getName().equals(exceptionClassName) ? e.getMessage() : exceptionClassName);
//
            throw e;
        }
        return response;
    }

}
