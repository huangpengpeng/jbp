package com.jbp.common.jdpay.sdk;


import com.alibaba.fastjson.JSONObject;
import com.jbp.common.jdpay.util.FileUtil;
import com.jbp.common.jdpay.util.GsonUtil;
import com.jbp.common.jdpay.util.JdPayApiUtil;
import com.jbp.common.jdpay.util.SignUtil;
import com.jbp.common.jdpay.vo.*;
import com.jbp.common.kqbill.utils.PropertiesLoader;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jbp.common.utils.SignatureUtil.RANDOM;

/*************************************************
 *
 * 京东支付api实现类
 *
 *************************************************/

@Component
public class JdPay {

    private static final String APP_KEY="E587420511102134670FC83264105546";

    private static final String APP_SECRET="e5407d44b5c54908bf88f6afbaac9ecd";

    @Resource
    private Environment environment;

    private static Logger logger = LoggerFactory.getLogger(JdPay.class);

    private static PropertiesLoader propertiesLoader;

    private JdPayConfig jdPayConfig;
    private JdPayHttpClientProxy jdPayHttpClientProxy;
    private String respJson;


    @PostConstruct
    public void init() {
        logger.info("正在初始化京东支付核心类.......");
        String property = environment.getProperty("jdpay.config");
        if (StringUtils.isEmpty(property)) {
            logger.info("未启动京东支付初始化退出.......");
            return;
        }
        propertiesLoader = new PropertiesLoader("classpath:" + property);
        String merchantNo = propertiesLoader.getProperty("jd.pay.merchantNo");
        String merchantNo2 = propertiesLoader.getProperty("jd.pay.divisionSub.merchantNo1");
        String merchantNo3 = propertiesLoader.getProperty("jd.pay.divisionSub.merchantNo2");
        String signKey = propertiesLoader.getProperty("jd.pay.signKey");
        String priCertPwd = propertiesLoader.getProperty("jd.pay.priCertPwd");
        String priCert = propertiesLoader.getProperty("jd.pay.priCert");
        String pubCert = propertiesLoader.getProperty("jd.pay.pubCert");
        String apiDomain = propertiesLoader.getProperty("jd.pay.apiDomain");
        String notifyUrl = propertiesLoader.getProperty("jd.pay.notifyUrl");
        String returnUrl = propertiesLoader.getProperty("jd.pay.returnUrl");
        // 加载商户私钥证书
        byte[] privateCert = FileUtil.readFile(priCert);
        // 加载商户公钥证书
        byte[] publicCert = FileUtil.readFile(pubCert);
        // 检查商户证书
        if (privateCert == null) {
            throw new RuntimeException("读取京东支付商户私钥证书为空");
        }
        if (publicCert == null) {
            throw new RuntimeException("读取京东支付商户公钥证书为空");
        }
        // 初始化京东支付配置对象
        JdPayConfig myConfig = new JdPayDefaultConfig(merchantNo, merchantNo2, merchantNo3, signKey, privateCert, priCertPwd, publicCert, apiDomain, notifyUrl, returnUrl);
        this.jdPayConfig = myConfig;
        this.jdPayHttpClientProxy = new JdPayHttpClientProxy(jdPayConfig, new JdPayHttpClient());
    }

    /**
     * 作用：统一下单
     * 场景：京东支付
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayCreateOrderResponse 返回数据
     * @throws Exception
     */
    public JdPayCreateOrderResponse createOrder(JdPayCreateOrderRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.CREATE_ORDER_URL, request, JdPayCreateOrderResponse.class);
    }

    /**
     * 分发佣金
     */
    public JdPaySendCommissionResponse sendCommission(JdPaySendCommissionRequest request, String amt) throws Exception {
        request.setPlatNo(jdPayConfig.getMerchantNo());
        request.setMerchantNo(jdPayConfig.getMerchantNo2());
        JdPayCommissionInfo commissionInfo = new JdPayCommissionInfo();
        commissionInfo.setCommNo(StringUtils.N_TO_10("C_"));
        commissionInfo.setAmount(amt);
        commissionInfo.setReceiveMerchantNo(jdPayConfig.getMerchantNo3());
        List<JdPayCommissionInfo> list = new ArrayList<>();
        list.add(commissionInfo);
        request.setCommissionInfos(list);
        return this.baseExecute(JdPayConstant.SEND_COMMISSION_URL, request, JdPaySendCommissionResponse.class);
    }

    public JdPayToPersonalWalletResponse payToPersonalWallet(JdPayToPersonalWalletRequest request) throws Exception {
//        request.setMerchantNo(jdPayConfig.getMerchantNo());
        request.setMerchantNo(jdPayConfig.getMerchantNo3());
        request.setReqNo(StringUtils.N_TO_10("Q_"));
        request.setAppKey(APP_KEY);
        request.setMerchantChannelCode("waichangShanghu");
        return this.baseExecute(JdPayConstant.PAY_TO_PERSONAL_WALLET, request, JdPayToPersonalWalletResponse.class, request.getReqNo(), request.getMerchantNo());
    }

    /**
     * 作用：三方聚合统一下单
     * 场景：三方聚合
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayAggregateCreateOrderResponse 返回数据
     * @throws Exception
     */
    public JdPayAggregateCreateOrderResponse aggregateCreateOrder(JdPayAggregateCreateOrderRequest request) throws Exception {
        request.setNotifyUrl(jdPayConfig.getNotifyUrl() + "/" + request.getOutTradeNo());
        request.setPageBackUrl(jdPayConfig.getReturnUrl());
        request.setDivisionAccount(GsonUtil.toJson(getJdPayDivisionAccount(request.getOutTradeNo(), new BigDecimal(request.getTradeAmount()))));
        JSONObject json = new JSONObject();
        json.put("teamName", request.getTradeRemark());
        request.setExtendParam(json.toJSONString());
        return this.baseExecute(JdPayConstant.AGGREGATE_CREATE_ORDER_URL, request, JdPayAggregateCreateOrderResponse.class);
    }

    /**
     * 作用：订单查询
     * 场景：查询订单信息 -  包括首次支付订单与代扣订单
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayQueryOrderResponse 返回数据
     * @throws Exception
     */
    public JdPayQueryOrderResponse queryOrder(JdPayQueryOrderRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.TRADE_QUERY_URL, request, JdPayQueryOrderResponse.class);
    }

    /**
     * 作用：代扣
     * 场景：代扣交易场景
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayAgreementPayResponse 返回数据
     * @throws Exception
     */
    public JdPayAgreementPayResponse agreementPay(JdPayAgreementPayRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.AGREEMENT_PAY_URL, request, JdPayAgreementPayResponse.class);
    }

    /**
     * 作用：申请退款
     * 场景：退款
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayRefundResponse 返回数据
     * @throws Exception
     */
    public JdPayRefundResponse refund(JdPayRefundRequest request) throws Exception {
        request.setDivisionAccountRefund(GsonUtil.toJson(getJdPayDivisionAccountRefund(request.getOriginalOutTradeNo(), new BigDecimal(request.getTradeAmount()))));
        return this.baseExecute(JdPayConstant.REFUND_URL, request, JdPayRefundResponse.class);
    }

    /**
     * 作用：退款查询
     * 场景：查询退款信息
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayQueryOrderResponse 返回数据
     * @throws Exception
     */
    public JdPayRefundQueryResponse refundQuery(JdPayRefundQueryRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.REFUND_QUERY_URL, request, JdPayRefundQueryResponse.class);
    }

    /**
     * 作用：解约
     * 场景：接触签约关系
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayAgreementCancelRequest 返回数据
     * @throws Exception
     */
    public JdPayAgreementCancelResponse agreementCancel(JdPayAgreementCancelRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.AGREEMENT_CANCEL_URL, request, JdPayAgreementCancelResponse.class);
    }


    /**
     * 作用：解约查询
     * 场景：查询签约关系
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayAgreementCancelRequest 返回数据
     * @throws Exception
     */
    public JdPayAgreementQueryResponse agreementQuery(JdPayAgreementQueryRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.AGREEMENT_QUERY_URL, request, JdPayAgreementQueryResponse.class);
    }

    public JdPayAgreementSignResponse agreementNewSign(JdPayAgreementSignRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.AGREEMENT_NEW_SIGN_URL, request, JdPayAgreementSignResponse.class);
    }

    /**
     * 验证接口参数签名
     * 场景:api接口返回参数，异步回调请求参数
     */
    public String verifyResponse(String respText) throws Exception {
        JdPayApiUtil.logger.info("京东支付异步通知-原始数据:{}", respText);
        String interData = JdPayApiUtil.decryptAndVerifySign(jdPayConfig, respText);
        JdPayApiUtil.logger.info("京东支付异步通知-解析数据:{}", interData);
        return interData;
    }

    /**
     * 验证页面回调参数
     *
     * @param respMap 页面回调参数
     * @return 验证结果
     * @throws Exception 异常
     */
    public boolean verifyPageCallBack(Map<String, String> respMap) throws Exception {
        return SignUtil.verifyPageCallBackSign(respMap, jdPayConfig.getSignKey());
    }

    /**
     * 执行接口调用
     *
     * @param request 请求对象
     * @param clazz   返回对象类型
     * @return 返回对象
     * @throws Exception 异常
     */
    public <REQ, RES> RES baseExecute(String urlSuffix, REQ request, Class<RES> clazz, String reqNo, String merchantNo) throws Exception {
        String reqJson = GsonUtil.toJson(request);
        String respJson = jdPayHttpClientProxy.execute(urlSuffix, reqJson, reqNo, merchantNo);
        return GsonUtil.fromJson(respJson, clazz);
    }

    public <REQ, RES> RES baseExecute(String urlSuffix, REQ request, Class<RES> clazz) throws Exception {
        String reqJson = GsonUtil.toJson(request);
        String respJson = jdPayHttpClientProxy.execute(urlSuffix, reqJson, genNonceStr(), "");
        return GsonUtil.fromJson(respJson, clazz);
    }

    private static String genNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = JdPayConstant.SYMBOLS.charAt(RANDOM.nextInt(JdPayConstant.SYMBOLS.length()));
        }
        return new String(nonceChars);
    }
    /**
     * 账户签约
     *
     * @param request
     * @return
     * @throws Exception
     */
    public JdPayAgreementSignApplyResponse agreementSignApply(JdPayAgreementSignApplyRequest request) throws Exception {
        return this.baseExecute(JdPayConstant.AGREEMENT_SIGN_APPLY_URL, request, JdPayAgreementSignApplyResponse.class);
    }

    public JdPayOauth2Response oauth2(String code) {
        String url = "https://open-oauth.jd.com/oauth2/access_token?app_key=%s&app_secret=%s&grant_type=authorization_code&code=%s";
        url = String.format(url, APP_KEY, APP_SECRET, code);
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = getHttpClientByBasicConnectionManager();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String string = EntityUtils.toString(httpEntity, JdPayConstant.UTF8);
            return GsonUtil.fromJson(string, JdPayOauth2Response.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JdPayDivisionAccount getJdPayDivisionAccount(String payCode, BigDecimal amt) {
        BigDecimal oneAmt = amt.multiply(BigDecimal.valueOf(1));
        JdPayDivisionAccount divisionAccount = new JdPayDivisionAccount();
        List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList = new ArrayList<JdPayDivisionAccountTradeInfo>();
        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoOne = new JdPayDivisionAccountTradeInfo();
        divisionAccountTradeInfoOne.setMerchantNo(jdPayConfig.getMerchantNo2());
        divisionAccountTradeInfoOne.setOutTradeNo(payCode);
        divisionAccountTradeInfoOne.setTradeAmount(oneAmt.stripTrailingZeros().toPlainString());
        divisionAccountTradeInfoList.add(divisionAccountTradeInfoOne);
        divisionAccount.setVersion("V2");
        divisionAccount.setDivisionAccountTradeInfoList(divisionAccountTradeInfoList);
        return divisionAccount;
    }

    public JdPayDivisionAccountRefund getJdPayDivisionAccountRefund(String payCode, BigDecimal refundAmt) {
        JdPayDivisionAccountRefund divisionAccountRefund = new JdPayDivisionAccountRefund();
        String now = DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        BigDecimal oneAmt = refundAmt.multiply(BigDecimal.valueOf(1));
        List<JdPayDivisionAccountRefundInfo> divisionAccountRefundInfoList = new ArrayList<JdPayDivisionAccountRefundInfo>();
        JdPayDivisionAccountRefundInfo divisionAccountRefundInfoOne = new JdPayDivisionAccountRefundInfo();
        divisionAccountRefundInfoOne.setMerchantNo(jdPayConfig.getMerchantNo2());
        divisionAccountRefundInfoOne.setOutTradeNo("R_" + payCode + "_" + now + "_1");
        divisionAccountRefundInfoOne.setTradeAmount(oneAmt.stripTrailingZeros().toPlainString());
        divisionAccountRefundInfoOne.setOriginalOutTradeNo(payCode);
        divisionAccountRefundInfoList.add(divisionAccountRefundInfoOne);
        divisionAccountRefund.setVersion("V2");
        divisionAccountRefund.setDivisionAccountRefundInfoList(divisionAccountRefundInfoList);
        return divisionAccountRefund;
    }

    private static HttpClient getHttpClientByBasicConnectionManager() {
        HttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register(JdPayConstant.HTTP, PlainConnectionSocketFactory.getSocketFactory())
                        .register(JdPayConstant.HTTPS, SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );
        return HttpClientBuilder.create()
                // 连接池
                .setConnectionManager(connManager)
                // 重试策略
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }
}
