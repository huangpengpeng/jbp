//package com.jbp.common.jdpay.web;
//
//
//import com.jbp.common.jdpay.enums.AgreementStatusEnum;
//import com.jbp.common.jdpay.enums.TradeStatusEnum;
//import com.jbp.common.jdpay.sdk.JdPay;
//import com.jbp.common.jdpay.util.GsonUtil;
//import com.jbp.common.jdpay.util.ToStringUtil;
//import com.jbp.common.jdpay.vo.JdPayRefundSuccessNotify;
//import com.jbp.common.jdpay.vo.JdPaySignSuccessNotify;
//import com.jbp.common.jdpay.vo.JdPayTradeSuccessNotify;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
///*************************************************
// *
// * 京东支付异步通知处理
// *
// *************************************************/
//@Controller(value = "jdPayNotify")
//@RequestMapping(value = "/jdPay")
//public class JdPayNotify {
//
//    private static final String SUCCESS = "SUCCESS";
//    private static final String ERROR = "ERROR";
//    private static Logger logger = LoggerFactory.getLogger(JdPayNotify.class);
//    @Resource
//    private JdPay jdPay;
//
//    /**
//     * @param reqText 交易通知报文，详见接口文档
//     * @return 成功返回"SUCCESS",  失败返回"ERROR",京东支付会再次发起通知，通知频次见接口文档。
//     */
//    @RequestMapping(value = "/tradeNotify")
//    @ResponseBody
//    public String tradeNotify(@RequestBody String reqText) {
//        logger.info("京东支付交易通知原始报文：" + reqText);
//        try {
//            // 验证签名与解密
//            String interData = jdPay.verifyResponse(reqText);
//            JdPayTradeSuccessNotify jdPayTradeSuccessNotify = GsonUtil.fromJson(interData, JdPayTradeSuccessNotify.class);
//            logger.info("京东支付交易通知解密报文：" + jdPayTradeSuccessNotify);
//            if (null == jdPayTradeSuccessNotify) {
//                return ERROR;
//            }
//            // 支付成功处理
//            if (isPaySuccess(jdPayTradeSuccessNotify)) {
//                return paySuccess(jdPayTradeSuccessNotify) ? SUCCESS : ERROR;
//            }
//            // 其他通知类型返回ERROR
//            logger.error("京东支付交易通知未识别,outTradeNo:{}", jdPayTradeSuccessNotify.getOutTradeNo());
//            return ERROR;
//        } catch (Exception e) {
//            // 异常处理
//            logger.info("京东支付交易通知处理异常{}", reqText, e);
//            return ERROR;
//        }
//    }
//
//    /**
//     * @param reqText 退款通知报文，详见接口文档
//     * @return 成功返回"SUCCESS",  失败返回"ERROR",退款通知会再次发起通知，通知频次见接口文档。
//     */
//    @RequestMapping(value = "/refundNotify")
//    @ResponseBody
//    public String refundNotify(@RequestBody String reqText) {
//        logger.info("京东支付退款通知原始报文：" + reqText);
//        try {
//            // 验证签名与解密
//            String interData = jdPay.verifyResponse(reqText);
//            JdPayRefundSuccessNotify jdPayRefundSuccessNotify = GsonUtil.fromJson(interData, JdPayRefundSuccessNotify.class);
//            logger.info("京东支付退款通知解密报文：" + jdPayRefundSuccessNotify);
//            if (null == jdPayRefundSuccessNotify) {
//                return ERROR;
//            }
//            // 支付成功处理
//            if (isRefundSuccess(jdPayRefundSuccessNotify)) {
//                return refundSuccess(jdPayRefundSuccessNotify) ? SUCCESS : ERROR;
//            }
//            // 其他通知类型返回ERROR
//            logger.error("京东支付退款通知未识别,outTradeNo:{}", jdPayRefundSuccessNotify.getOutTradeNo());
//            return ERROR;
//        } catch (Exception e) {
//            // 异常处理
//            logger.info("京东支付退款通知处理异常{}", reqText, e);
//            return ERROR;
//        }
//    }
//
//    /**
//     * @param reqText 签约通知报文，详见接口文档
//     * @return 成功返回"SUCCESS",  失败返回"ERROR",签约通知会再次发起通知，通知频次见接口文档。
//     */
//    @RequestMapping(value = "/signNotify")
//    @ResponseBody
//    public String signNotify(@RequestBody String reqText) {
//        logger.info("京东支付签约通知原始报文：" + reqText);
//        try {
//            // 验证签名与解密
//            String interData = jdPay.verifyResponse(reqText);
//            JdPaySignSuccessNotify jdPaySignSuccessNotify = GsonUtil.fromJson(interData, JdPaySignSuccessNotify.class);
//            logger.info("京东支付签约通知解密报文：" + jdPaySignSuccessNotify);
//            if (null == jdPaySignSuccessNotify) {
//                return ERROR;
//            }
//            // 签约成功处理
//            if (AgreementStatusEnum.FINI.getCode().equals(jdPaySignSuccessNotify.getAgreementStatus())) {
//
//            }
//            // 签约取消成功处理
//            if (AgreementStatusEnum.CLOS.getCode().equals(jdPaySignSuccessNotify.getAgreementStatus())) {
//
//            }
//            return signSuccess(jdPaySignSuccessNotify) ? SUCCESS : ERROR;
//        } catch (Exception e) {
//            // 异常处理
//            logger.info("京东支付签约通知处理异常{}", reqText, e);
//            return ERROR;
//        }
//    }
//
//    /**
//     * 接收参数outTradeNo、tradeAmount、currency、tradeDate、tradeStatus、signData
//     *
//     * @param httpServletRequest httpServletRequest
//     * @return 商户页面
//     */
//    @RequestMapping(value = "/pageCallBack")
//    @ResponseBody
//    public String pageCallBack(HttpServletRequest httpServletRequest) {
//        // 签名验证逻辑，需要支持添加通知字段不影响结果
//        Map<String, String> respMap = getAllRequestParam(httpServletRequest);
//        String params = ToStringUtil.toJson(respMap);
//        try {
//            boolean checkSignResult = jdPay.verifyPageCallBack(respMap);
//            logger.info("京东支付页面回调参数{}, 签名结果{}", params, checkSignResult);
//            if (!checkSignResult) {
//                // todo 签名异常，报错
//            }
//            if (String.valueOf(TradeStatusEnum.FINI.getCode()).equals(respMap.get("status"))) {
//                // todo 支付成功
//            } else if (String.valueOf(TradeStatusEnum.WPAR.getCode()).equals(respMap.get("status"))) {
//                // todo 支付处理中
//            } else {
//                // todo 支付失败
//            }
//            return String.format("sign result:%s , params:%s", checkSignResult, params);
//        } catch (Exception e) {
//            // todo 异常处理
//            logger.info("京东支付页面回调参数{}", params, e);
//            return "ERROR";
//        }
//    }
//
//    /**
//     * 获取客户端请求参数中所有的信息
//     */
//    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
//        Map<String, String> respMap = new HashMap<String, String>();
//        Enumeration<?> temp = request.getParameterNames();
//        if (null != temp) {
//            while (temp.hasMoreElements()) {
//                String en = (String) temp.nextElement();
//                String value = request.getParameter(en);
//                respMap.put(en, value);
//                //如果字段的值为空，判断若值为空，则删除这个字段>
//                if (null == respMap.get(en) || "".equals(respMap.get(en))) {
//                    respMap.remove(en);
//                }
//            }
//        }
//        return respMap;
//    }
//
//    /**
//     * 判断是否为支付成功通知
//     *
//     * @param jdPaySuccessNotify 京东支付异步通知vo
//     * @return 是否为支付成功通知
//     */
//    private boolean isPaySuccess(JdPayTradeSuccessNotify jdPaySuccessNotify) {
//        return String.valueOf(TradeStatusEnum.FINI.getCode()).equals(jdPaySuccessNotify.getTradeStatus());
//    }
//
//    /**
//     * 判断是否为退款成功通知
//     *
//     * @param jdPayRefundSuccessNotify 京东支付异步通知vo
//     * @return 是否为支付成功通知
//     */
//    private boolean isRefundSuccess(JdPayRefundSuccessNotify jdPayRefundSuccessNotify) {
//        return String.valueOf(TradeStatusEnum.FINI.getCode()).equals(jdPayRefundSuccessNotify.getTradeStatus());
//    }
//
//    /**
//     * 商户退款成功处理，需商户自行实现
//     *
//     * @param jdPayRefundSuccessNotify 京东支付-退款异步通知vo
//     * @return 商户退款成功处理结果
//     */
//    private boolean refundSuccess(JdPayRefundSuccessNotify jdPayRefundSuccessNotify) {
//        // todo 商户逻辑实现
//        return true;
//    }
//
//    /**
//     * 商户支付成功处理，需商户自行实现
//     *
//     * @param jdPayTradeSuccessNotify 京东支付异步通知vo
//     * @return 商户支付成功处理结果
//     */
//    private boolean paySuccess(JdPayTradeSuccessNotify jdPayTradeSuccessNotify) {
//        // todo 商户逻辑实现
//        return true;
//    }
//
//    /**
//     * 判断是否为签约成功通知
//     *
//     * @param jdPaySignSuccessNotify 签约异步通知vo
//     * @return 是否为签约成功通知
//     */
//    private boolean isSignSuccess(JdPaySignSuccessNotify jdPaySignSuccessNotify) {
//        return String.valueOf(AgreementStatusEnum.FINI.getCode()).equals(jdPaySignSuccessNotify.getAgreementStatus());
//    }
//
//
//    /**
//     * 商户签约成功处理，需商户自行实现
//     *
//     * @param jdPaySignSuccessNotify 签约异步通知vo
//     * @return 商户签约成功处理结果
//     */
//    private boolean signSuccess(JdPaySignSuccessNotify jdPaySignSuccessNotify) {
//        // todo 商户逻辑实现
//        return true;
//    }
//
//}
