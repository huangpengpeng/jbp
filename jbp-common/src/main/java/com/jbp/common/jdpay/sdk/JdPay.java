package com.jbp.common.jdpay.sdk;




import com.jbp.common.jdpay.util.FileUtil;
import com.jbp.common.jdpay.util.GsonUtil;
import com.jbp.common.jdpay.util.JdPayApiUtil;
import com.jbp.common.jdpay.util.SignUtil;
import com.jbp.common.jdpay.vo.*;
import com.jbp.common.kqbill.utils.PropertiesLoader;
import com.jbp.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/*************************************************
 *
 * 京东支付api实现类
 *
 *************************************************/

@Component
public class JdPay {

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
        String signKey = propertiesLoader.getProperty("jd.pay.signKey");
        String priCertPwd = propertiesLoader.getProperty("jd.pay.priCertPwd");
        String priCert = propertiesLoader.getProperty("jd.pay.priCert");
        String pubCert = propertiesLoader.getProperty("jd.pay.pubCert");
        String apiDomain = propertiesLoader.getProperty("jd.pay.apiDomain");
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
        JdPayConfig myConfig = new JdPayDefaultConfig(merchantNo, signKey, privateCert, priCertPwd, publicCert, apiDomain);
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
     * 作用：三方聚合统一下单
     * 场景：三方聚合
     *
     * @param request 向jdPay post的请求数据
     * @return JdPayAggregateCreateOrderResponse 返回数据
     * @throws Exception
     */
    public JdPayAggregateCreateOrderResponse aggregateCreateOrder(JdPayAggregateCreateOrderRequest request) throws Exception {
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

    public JdPayAgreementSignResponse agreementNewSign(JdPayAgreementSignRequest request) throws Exception{
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
    public <REQ, RES> RES baseExecute(String urlSuffix, REQ request, Class<RES> clazz) throws Exception {
        String reqJson = GsonUtil.toJson(request);
        String respJson = jdPayHttpClientProxy.execute(urlSuffix, reqJson);
        return GsonUtil.fromJson(respJson, clazz);
    }

    /**
     * 账户签约
     * @param request
     * @return
     * @throws Exception
     */
    public JdPayAgreementSignApplyResponse agreementSignApply(JdPayAgreementSignApplyRequest request) throws Exception{
        return this.baseExecute(JdPayConstant.AGREEMENT_SIGN_APPLY_URL, request, JdPayAgreementSignApplyResponse.class);
    }
}