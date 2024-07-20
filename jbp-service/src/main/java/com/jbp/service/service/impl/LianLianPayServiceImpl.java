package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.lianlian.client.LLianPayClient;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.lianlian.security.LLianPayAccpSignature;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LianLianPayServiceImpl implements LianLianPayService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public LianLianPayInfoResult get() {
        String pubKey = systemConfigService.getValueByKey("lianlian_pub_key");
        String priKey = systemConfigService.getValueByKey("lianlian_pri_key");
        String oid_partner = systemConfigService.getValueByKey("lianlian_oid_partner");
        String payee_no = systemConfigService.getValueByKey("lianlian_payee_no");
        String req_domain = systemConfigService.getValueByKey("lianlian_req_domain");
        String status = systemConfigService.getValueByKey("lianlian_pay_status");
        String notify_url = systemConfigService.getValueByKey("lianlian_notify_url");
        String return_url = systemConfigService.getValueByKey("lianlian_return_url");
        String host = systemConfigService.getValueByKey("lianlian_host");

        String lzt_oid_partner = systemConfigService.getValueByKey("lianlian_lzt_oid_partner");
        String lzt_pri_key = systemConfigService.getValueByKey("lianlian_lzt_pri_key");

        LianLianPayInfoResult result = new LianLianPayInfoResult();
        result.setPubKey(pubKey);
        result.setPriKey(priKey);
        result.setOid_partner(oid_partner);
        result.setPayee_no(payee_no);
        result.setReq_domain(req_domain);
        result.setStatus(status);
        result.setNotify_url(notify_url);
        result.setReturn_url(return_url);
        result.setHost(host);

        // 来账通产品信息
        result.setLzt_priKey(lzt_pri_key);
        result.setLzt_oid_partner(lzt_oid_partner);
        return result;
    }

    @Override
    public boolean checkSign(String souceStr, String signature) {
        LianLianPayInfoResult payInfo = get();
        return LLianPayAccpSignature.getInstance().checkSign(payInfo.getPubKey(), souceStr, signature);
    }

    @Override
    public QueryPaymentResult queryPayResult(String orderNo) {
        LianLianPayInfoResult lianLianInfo = get();
        QueryPaymentParams params = new QueryPaymentParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(lianLianInfo.getOid_partner());
        params.setTxn_seqno(orderNo);
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        // 测试环境URL
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        QueryPaymentResult queryPaymentResult = JSON.parseObject(resultJsonStr, QueryPaymentResult.class);
        return queryPaymentResult;
    }


    @Override
    public CashierPayCreateResult cashier(String account, String payCode, BigDecimal amount, String notifyUrl,
                                          String returnUrl, String goodsName, String ip) {
        LianLianPayInfoResult lianLianInfo = get();

        CashierPayCreateParams params = new CashierPayCreateParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(lianLianInfo.getOid_partner());
        // 普通消费
        params.setTxn_type("GENERAL_CONSUME");
        params.setUser_id(account);
        params.setUser_type("ANONYMOUS");
        params.setNotify_url(notifyUrl);
        params.setReturn_url(returnUrl);
        // 交易发起渠道设置
        params.setFlag_chnl("H5");
        // 测试风控参数
        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", account, "", registerTime, goodsName);
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setFrms_client_chnl("H5");
        riskItemInfo.setUser_auth_flag("1");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));

        JSONObject extendJson = new JSONObject();
        extendJson.put("req_domain", lianLianInfo.getReq_domain());
        params.setExtend(extendJson);

        // 设置商户订单信息
        CashierPayCreateOrderInfo orderInfo = new CashierPayCreateOrderInfo();
        orderInfo.setTxn_seqno(payCode);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amount.doubleValue());
        orderInfo.setGoods_name(goodsName);
        params.setOrderInfo(orderInfo);

        // 设置付款方信息
        CashierPayCreatePayerInfo payerInfo = new CashierPayCreatePayerInfo();
        payerInfo.setPayer_id(account);
        payerInfo.setPayer_type("USER");
        params.setPayerInfo(payerInfo);

        // 收款方
        CashierPayCreatePayeeInfo payeeInfo = new CashierPayCreatePayeeInfo();
        payeeInfo.setPayee_id(lianLianInfo.getPayee_no()); // 不允许改变
        payeeInfo.setPayee_type("USER");
        payeeInfo.setPayee_accttype("USEROWN");
        payeeInfo.setPayee_amount(amount.toString());
        params.setPayeeInfo(new CashierPayCreatePayeeInfo[]{payeeInfo});

        String url = "https://accpgw.lianlianpay.com/v1/cashier/paycreate";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        CashierPayCreateResult result = JSON.parseObject(resultJsonStr, CashierPayCreateResult.class);
        if (result == null || !"0000".equals(result.getRet_code())) {
            throw new RuntimeException("请求三方交易失败:" + JSONObject.toJSONString(result));
        }
        return result;
    }


    @Override
    public MorePayeeRefundResult refund(String account, String payCode, String refundNo, BigDecimal refundAmt) {
        LianLianPayInfoResult lianLianInfo = get();

        QueryPaymentResult queryPaymentResult = queryPayResult(payCode);
        if (queryPaymentResult == null || !"TRADE_SUCCESS".equals(queryPaymentResult.getTxn_status())) {
            throw new RuntimeException("订单未支付成功不允许退款:" + payCode);
        }

        QueryPaymentOrderInfo orderInfo = queryPaymentResult.getOrderInfo();
        List<QueryPaymentPayeeInfo> payeeInfo = queryPaymentResult.getPayeeInfo();
        List<QueryPaymentPayerInfo> payerInfo = queryPaymentResult.getPayerInfo();

        MorePayeeRefundParams params = new MorePayeeRefundParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(lianLianInfo.getOid_partner());
        // 原交易付款方user_id
        params.setUser_id(account);

        // 原商户订单信息
        OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
        // 原支付交易商户系统唯一交易流水号
        originalOrderInfo.setTxn_seqno(payCode);
        // 订单总金额
        originalOrderInfo.setTotal_amount(orderInfo.getTotal_amount());
        params.setOriginalOrderInfo(originalOrderInfo);

        // 退款订单信息
        RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
        // 退款订单号。标识一次退款请求，商户系统需要保证唯一
        refundOrderInfo.setRefund_seqno(refundNo);
        refundOrderInfo.setRefund_time(timestamp);
        // 退款总金额。本次需要退款的金额，不允许超过对应原收款方的收款金额
        refundOrderInfo.setRefund_amount(refundAmt.doubleValue());
        params.setRefundOrderInfo(refundOrderInfo);

        // 原收款方退款信息
        PyeeRefundInfo pyeeRefundInfo = new PyeeRefundInfo();
        // 原收款方id，本次退款需要处理的原交易收款方id
        pyeeRefundInfo.setPayee_id(payeeInfo.get(0).getPayee_id());
        /*
        原收款方类型。
        用户：USER
        平台商户：MERCHANT
         */
        pyeeRefundInfo.setPayee_type(payeeInfo.get(0).getPayee_type());
        /*
        原收款方账户类型。
        用户账户：USEROWN
        平台商户自有资金账户：MCHOWN
        平台商户担保账户：MCHASSURE
        平台商户优惠券账户：MCHCOUPON
        平台商户手续费账户：MCHFEE
         */
        pyeeRefundInfo.setPayee_accttype("MERCHANT".equals(pyeeRefundInfo.getPayee_type()) ? "MCHOWN" : "USEROWN");
        // 退款金额。本次需要退款的金额，不允许超过对应原收款方的收款金额。
        pyeeRefundInfo.setPayee_refund_amount(refundAmt.doubleValue());
        // 垫资标识。当原收款方金额不足时，是否由平台垫资的标识，默认:N
        pyeeRefundInfo.setIs_advance_pay("N");
        params.setPyeeRefundInfos(Arrays.asList(new PyeeRefundInfo[]{pyeeRefundInfo}));

        // 原付款方式退款规则信息
        RefundMethod refundMethod = new RefundMethod();
        // 付款方式
        refundMethod.setMethod(payerInfo.get(0).getMethod());
        // 退款金额
        refundMethod.setAmount(refundAmt.doubleValue());
        params.setRefundMethods(Arrays.asList(new RefundMethod[]{refundMethod}));

        String url = "https://accpapi.lianlianpay.com/v1/txn/more-payee-refund";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        MorePayeeRefundResult morePayeeRefundResult = JSON.parseObject(resultJsonStr, MorePayeeRefundResult.class);
        return morePayeeRefundResult;
    }


    @Override
    public GetRandomResult getRandom(String userId) {
        LianLianPayInfoResult lianLianInfo = get();

        GetRandomParams params = new GetRandomParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(lianLianInfo.getOid_partner());
        params.setUser_id(userId);
        params.setFlag_chnl("H5");
        // 测试环境都传test，正式环境传真实域名/包名
        params.setPkg_name(lianLianInfo.getReq_domain());
        // 测试环境都传test，正式环境传真实域名/应用名
        params.setApp_name(lianLianInfo.getReq_domain());
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        // 测试环境URL
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/get-random";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        GetRandomResult getRandomResult = JSON.parseObject(resultJsonStr, GetRandomResult.class);
        return getRandomResult;
    }

    @Override
    public JSONObject getPasswordElementToken(String userId, String payCode, String pyee_name, BigDecimal amount) {
        LianLianPayInfoResult lianLianInfo = get();

        ApplyPasswordElementParams params = new ApplyPasswordElementParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(lianLianInfo.getOid_partner());
        params.setUser_id(userId);
        params.setTxn_seqno(payCode);
        params.setPyee_name(pyee_name);
        params.setAmount(amount.doubleValue());
        params.setEncrypt_algorithm("SM2");
        /*
        密码使用场景：
        设置密码：setting_password
        修改密码：change_password
        换绑卡：bind_card_password
        提现密码：cashout_password
        支付密码：pay_password
         */
        params.setPassword_scene("cashout_password");
        params.setFlag_chnl("PCH5");
        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/apply-password-element";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        ApplyPasswordElementResult result = JSON.parseObject(resultJsonStr, ApplyPasswordElementResult.class);
        // 构建前端需要的json
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("passwordScene", params.getPassword_scene());
        jsonObject.put("oidPartner", result.getOid_partner());
        jsonObject.put("userId", params.getUser_id());
        jsonObject.put("passwordElementToken", result.getPassword_element_token());
        return jsonObject;
    }


    @Override
    public UserInfoResult queryUserInfo(String userId) {
        LianLianPayInfoResult lianLianInfo = get();
        UserInfoParams params = new UserInfoParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(lianLianInfo.getOid_partner());
        params.setUser_id(userId);
        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-userinfo";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        UserInfoResult userInfoResult = JSON.parseObject(resultJsonStr, UserInfoResult.class);
        return userInfoResult;
    }

    @Override

    public AcctInfoResult queryAcctInfo(String userId) {
        LianLianPayInfoResult lianLianInfo = get();

        AcctInfoParams params = new AcctInfoParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(lianLianInfo.getOid_partner());
        params.setUser_id(userId);
        params.setUser_type("INNERCOMPANY");

        String url = "https://accpapi.lianlianpay.com/v1/acctmgr/query-acctinfo";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        AcctInfoResult result = JSON.parseObject(resultJsonStr, AcctInfoResult.class);
        return result;
    }

    @Override
    public WithdrawalResult withdrawal(String drawNo, BigDecimal amt, String postscript, String password, String random_key) {
        LianLianPayInfoResult lianLianInfo = get();

        WithDrawalParams params = new WithDrawalParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(lianLianInfo.getOid_partner());

        // 设置商户订单信息
        WithDrawalOrderInfo orderInfo = new WithDrawalOrderInfo();
        orderInfo.setTxn_seqno(drawNo);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amt.doubleValue());
        orderInfo.setPostscript(postscript);
        params.setOrderInfo(orderInfo);

        // 设置付款方信息
        WithDrawalPayerInfo payerInfo = new WithDrawalPayerInfo();
        payerInfo.setPayer_type("USER");
        payerInfo.setPayer_id(lianLianInfo.getPayee_no());
        payerInfo.setPassword(password);
        payerInfo.setRandom_key(random_key);
        params.setPayerInfo(payerInfo);

        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", lianLianInfo.getPayee_no(), "", registerTime, "提现");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));

        String url = "https://accpapi.lianlianpay.com/v1/txn/withdrawal";
        LLianPayClient lLianPayClient = new LLianPayClient(lianLianInfo.getPriKey(), lianLianInfo.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        WithdrawalResult drawalResult = JSON.parseObject(resultJsonStr, WithdrawalResult.class);
        return drawalResult;
    }

    @Resource
    private MerchantService merchantService;

    @Override
    public LztTransferResult transferSpanPlatform(String txn_seqno, BigDecimal total_amount, String userId, String password, String random_key, String sub_acctno, String sub_acctname) {
        TransferSpanPlatformParams params = new TransferSpanPlatformParams();
        Merchant merchant = merchantService.getById(4);
        MerchantPayInfo payInfo = merchant.getPayInfo();

        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(payInfo.getOidPartner());
        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", sub_acctno, "", registerTime, "提现");
        riskItemInfo.setFrms_ip_addr("60.177.228.155");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));
        params.setOrderInfo(new TransferOrderInfo(txn_seqno, timestamp, total_amount.doubleValue(), "服务费", "平台转账"));
        params.setPayerInfo(new TransferPayerInfo("USER", userId, "USEROWN", password, random_key));
        params.setPayeeInfo(new TransferPayeeInfo2(sub_acctno, sub_acctname));


        LianLianPayInfoResult lianLianPayInfoResult = get();
        String url = "https://accpapi.lianlianpay.com/v1/txn/trader-transfer-span-platform";
        LLianPayClient lLianPayClient = new LLianPayClient(payInfo.getPriKey(), lianLianPayInfoResult.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        LztTransferResult result = JSON.parseObject(resultJsonStr, LztTransferResult.class);
        return result;
    }

    @Override
    public BindCardH5ApplyResult bindCardH5Apply(String user_id, String user_type, String bind_cardtype, String txn_seqno,
                                                 String notify_url) {
        Merchant merchant = merchantService.getById(4);
        MerchantPayInfo payInfo = merchant.getPayInfo();

        BindCardH5ApplyParams params = new BindCardH5ApplyParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(payInfo.getOidPartner());
        params.setUser_id(user_id);
        params.setUser_type(user_type);
        params.setBind_cardtype(bind_cardtype);
        params.setNotify_url(notify_url);
        params.setTxn_time(timestamp);
        params.setTxn_seqno(txn_seqno);

        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", user_id, "", registerTime, bind_cardtype);
        riskItemInfo.setFrms_ip_addr("115.196.4.80");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));


        LianLianPayInfoResult lianLianPayInfoResult = get();
        String url = "https://accpgw.lianlianpay.com/v1/acctmgr/bindcard-h5-apply";
        LLianPayClient lLianPayClient = new LLianPayClient(payInfo.getPriKey(), lianLianPayInfoResult.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        BindCardH5ApplyResult result = JSON.parseObject(resultJsonStr, BindCardH5ApplyResult.class);
        return result;
    }

    @Override
    public LztPapAgreeApplyResult papAgreeApply(String oidPartner, String priKey, String user_id, PapSignInfo papSignInfo) {
        if (papSignInfo == null) {
            papSignInfo = new PapSignInfo();
        }
        papSignInfo.setSign_start_time("20240719");
        papSignInfo.setSign_invalid_time("20250718");
        papSignInfo.setAgreement_type("WITH_HOLD");
        String timestamp = LLianPayDateUtils.getTimestamp();
        LztPapAgreeApplyParams params = new LztPapAgreeApplyParams(timestamp, oidPartner, user_id, papSignInfo);
        params.setNotify_url("https://join.jubaopeng.cc");
        String url = "https://accpgw.lianlianpay.com/v1/txn/pap-agree-apply";
        LianLianPayInfoResult lianLianInfo = get();
        LLianPayClient lLianPayClient = new LLianPayClient(priKey, lianLianInfo.getPubKey());
        String s = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        LztPapAgreeApplyResult result = JSON.parseObject(s, LztPapAgreeApplyResult.class);
        return result;
    }
}
