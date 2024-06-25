package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.constants.YopEnums;
import com.jbp.common.yop.constants.YopProducts;
import com.jbp.common.yop.dto.ExtParams4BankPay;
import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.YopService;
import com.yeepay.yop.sdk.exception.YopClientException;
import com.yeepay.yop.sdk.security.DigestAlgEnum;
import com.yeepay.yop.sdk.security.rsa.RSA;
import com.yeepay.yop.sdk.security.rsa.RSAKeyUtils;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import com.yeepay.yop.sdk.service.common.response.YosUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class YopServiceImpl implements YopService {

    @Resource
    private YopClient yopClient;
    @Resource
    private SystemConfigService systemConfigService;

    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCccn7+/zVkSQT6Jvlt8JrB3wTuiFG8xO385NVjsPl+SHdjxjV171+AIZK+S0bm9vcFQWnVQ8o5i145oaiR2Ye7j/dC5YziweHyUuotWzCVCK6GJmxxfA8pbxA8ZhsSUec5neoTu+bNH6WAuj7zbU1TcqROytK1Ck2jLNGQiam2AjvLwo4Ee2eOOXYks3Dwm0vboWLpa9rrUZm7BAr8uydWvTrH0RPdcRBNxL75bRttP7p/8QuPoK8zS7Z8EFMemS1UPL+MmmGi9piTsQEonoFZgIStuRjyvQaaOCAz4J2PzNqWqFhox3mm8fIx4vQNmuLzxcT5z1leuXey+JOIsemdAgMBAAECggEALGPyc+EvIZ70ahRP9ulrx/HDYPlOE/1et8CoaaqICFSAh89wUN/UXbNpA1SxsAxIJ/YZFPmwsiZA3KJphcTO1iHx20nt8VNpDIyJRMhYnxkBCDBz2bmFRdDtLu4b5VVXAgq0JdhNk0tU9xMhlImhhLmFNJQpOFRNliy748Kj5HAJHxdcCXfrpPUuzFoUwJYSTwWOcorgfQDm1oinndittvBN9QOKr5LWGApJUaNkcG+UxPZNaNfGzKPsVSV3+JmLHex09IOpd3xMAB3pAWR6wpuFQ0VqjV6kIH50Ejqn/YxK7gNQo1mB8Tg6g7JmjMsmCSLJU4lQUYJT6vb4CEiXEwKBgQC/TUL8TIJpVXwMK0gWgqY+KOlcGjFjElh0Nw7gMtIYcbazoYK3sBnKIZ2RepMvA1X1iPalT6YAE6O3GCyYqmAf6rR5llsCKlPLpAKpo6wdtA6u93jWOZov4je0pqPn2mjJWrU8xjNp6+sVnF7V5iS++TTnJDNceVK2A7fmamcKHwKBgQDRW476tpp5yXcomjB6a9NosfhtkDTfHR7JfguCL+I43i7i+78CxGkH67urBaADMzlxFxFGUN5WGhrsSa/vpdoUT1HCL8/LI52YzXxPzkaXcw3uXv6oUy09VZwC4ycGLCqsiG11Quk7gXq2c3xdG18ccAM9zFJbuVwGwV1InExMwwKBgQCEdHl5+XuedTsDrgAm3eU/StJxHA2v/CbUqL6bC3UeybVn4N+CUeM5alcQQJ+iQJwG+wNT6LcWfIKxpoJSXj1aPAcj3LA86pPEf1X8oT/t/RrhmKXJJm8U0nwhj/QS983wBOdSIiW9JEVMXE3pqoUs2Z4AoLcTQ0m6jw6I8olPnwKBgB41B7xE/KT13KuPE8+WHzrL8vxcAkTu/rIz7ZUrM4jhBgLaMAVGMaFi8gELHrtXeMJIgcWThYEG4zuUpj39wCmOCE3seB7nVKXngDhDmwvfHfN24WeIGM7wu0HvZIIPfVjHloOE1AIx/HK21wrYGDESOGWCRZ/WbuDKpZsEcj3hAoGAUod2XKj+MmOg4lIiVZ4RyElis603MunPbs88KMMmfZul52h5PTHc0LRMvCQOZQxlWMml0fZ9hI+G/MSvivHlopCYJ6JOLKvKrtnLTJff/K7oIBs3q3hVpXVmHq3fdkXZDhgWA1Gsfaots+XcjdmQ66si6ixC7ymlCPqtwc3/aeI=";

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6p0XWjscY+gsyqKRhw9MeLsEmhFdBRhT2emOck/F1Omw38ZWhJxh9kDfs5HzFJMrVozgU+SJFDONxs8UB0wMILKRmqfLcfClG9MyCNuJkkfm0HFQv1hRGdOvZPXj3Bckuwa7FrEXBRYUhK7vJ40afumspthmse6bs6mZxNn/mALZ2X07uznOrrc2rk41Y2HftduxZw6T4EmtWuN2x4CZ8gwSyPAW5ZzZJLQ6tZDojBK4GZTAGhnn3bg5bBsBlw2+FLkCQBuDsJVsFPiGh/b6K/+zGTvWyUcu+LUj2MejYQELDO3i2vQXVDk7lVi2/TcUYefvIcssnzsfCfjaorxsuwIDAQAB";

    @Override
    public RegisterMicroH5Result registerMicroH5(RegisterMicroH5Params params) {
        return send("/rest/v2.0/mer/register/saas/index", "POST", params, RegisterMicroH5Result.class);
    }

    @Override
    public RegisterMicroResult registerMicro(String requestNo, String signName, String id_card, String frontUrl,
                                             String backUrl, String mobile, String province, String city, String district,
                                             String address, String bankCardNo, String bankCode, String notifyUrl, String withdrawalUndertaker) {
        RegisterMicroParams params = new RegisterMicroParams();
        params.setParentMerchantNo("10089625822");
        params.setBusinessRole("SETTLED_MERCHANT");
        params.setRequestNo(requestNo);

        // 签约信息
        JSONObject merchantSubjectInfo = new JSONObject();
        merchantSubjectInfo.put("signName", signName);
        merchantSubjectInfo.put("shortName", signName);
        params.setMerchantSubjectInfo(merchantSubjectInfo.toJSONString());

        // 实名信息
        JSONObject merchantCorporationInfo = new JSONObject();
        merchantCorporationInfo.put("legalLicenceType", "ID_CARD");
        merchantCorporationInfo.put("legalLicenceNo", id_card);
        merchantCorporationInfo.put("legalLicenceFrontUrl", frontUrl);
        merchantCorporationInfo.put("legalLicenceBackUrl", backUrl);
        merchantCorporationInfo.put("mobile", mobile);
        params.setMerchantCorporationInfo(merchantCorporationInfo.toJSONString());

        // 地址信息
        JSONObject businessAddressInfo = new JSONObject();
        businessAddressInfo.put("province", province);
        businessAddressInfo.put("city", city);
        businessAddressInfo.put("district", district);
        businessAddressInfo.put("address", address);
        params.setBusinessAddressInfo(businessAddressInfo.toJSONString());

        // 账户信息
        JSONObject accountInfo = new JSONObject();
        accountInfo.put("settlementDirection", "BANKCARD");
        accountInfo.put("bankAccountType", "DEBIT_CARD");
        accountInfo.put("bankCardNo", bankCardNo);
        accountInfo.put("bankCode", bankCode);
        params.setAccountInfo(accountInfo.toJSONString());
        // 通知开户产品
        params.setNotifyUrl(notifyUrl);
        if ("个人".equals(withdrawalUndertaker)) {
            params.setProductInfo(YopProducts.getMicroMerchant2());
        } else {
            params.setProductInfo(YopProducts.getMicroMerchant());
        }
        return send("/rest/v2.0/mer/register/saas/micro", "POST", params, RegisterMicroResult.class);
    }

    /**
     * 企业入网
     */
    @Override
    public RegisterResult register(RegisterParams params) {
        params.setParentMerchantNo("10089066338");
        params.setProductInfo(YopProducts.getMerchant());
        return send("/rest/v2.0/mer/register/saas/merchant", "POST", params, RegisterResult.class);
    }

    @Override
    public RegisterQueryResult registerQuery(String requestNo) {
        return send("/rest/v2.0/mer/register/query", "GET", new RegisterQueryParams(requestNo), RegisterQueryResult.class);
    }

    @Override
    public MerchantInfoModifyResult merchantInfoModify(MerchantInfoModifyParams params) {
        return send("/rest/v1.0/mer/merchant/info/modify", "POST", params, MerchantInfoModifyResult.class);
    }

    @Override
    public String upload(String url) {
        InputStream inputStream = null;
        try {
            if (StringUtils.isNotBlank(url)) {
                inputStream = new URL(url).openStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(url)) {
            return upload(inputStream).getMerQualUrl();
        } else {
            MerFileUploadResponse merFileUploadResponse = new MerFileUploadResponse();
            merFileUploadResponse.setMerQualUrl("");
            return "";
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            return upload(file.getInputStream()).getMerQualUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BankAccountOpenResult bankAccountOpen(BankAccountOpenParams params) {
        params.setParentMerchantNo("10089066338");
        return send2("/rest/v1.0/account/account-manage/bank-account/open", "POST", params, BankAccountOpenResult.class);
    }

    @Override
    public OnlineBankOrderResult onlineBankOrder(OnlineBankOrderParams params) {
        params.setParentMerchantNo("10089066338");
        return send("/rest/v1.0/account/recharge/onlinebank/order", "POST", params, OnlineBankOrderResult.class);
    }

    @Override
    public BankAccountQueryResult bankAccountQuery(String merchantNo, String requestNo) {
        BankAccountQueryParams params = new BankAccountQueryParams();
        params.setRequestNo(requestNo);
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        return send("/rest/v1.0/account/account-manage/bank-account/query", "GET", params, BankAccountQueryResult.class);
    }

    @Override
    public BankAccountBalanceQueryResult bankAccountBalanceQuery(String merchantNo, String bankCode, String accountNo) {
        BankAccountBalanceQueryParams params = new BankAccountBalanceQueryParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setBankCode(bankCode);
        params.setAccountNo(accountNo);
        return send("/rest/v1.0/recharge/bank-account/query", "GET", params, BankAccountBalanceQueryResult.class);
    }

    @Override
    public AccountBalanceQueryResult accountBalanceQuery(String merchantNo) {
        AccountBalanceQueryParams params = new AccountBalanceQueryParams();
        params.setMerchantNo(merchantNo);
        return send("/rest/v1.0/account/balance/query", "GET", params, AccountBalanceQueryResult.class);
    }

    @Override
    public AllAccountBalanceQueryResult allAccountBalanceQuery(String merchantNo) {
        AccountBalanceQueryParams params = new AccountBalanceQueryParams();
        params.setMerchantNo(merchantNo);
        return send3("/rest/v1.0/account/accountinfos/query", "GET", params, AllAccountBalanceQueryResult.class);
    }

    @Override
    public WithdrawCardModifyResult withdrawCardModify(WithdrawCardModifyParams params) {
        return send("/rest/v1.0/account/withdraw/card/modify", "POST", params, WithdrawCardModifyResult.class);
    }

    @Override
    public WithdrawCardBindResult withdrawCardBind(WithdrawCardBindParams params) {
        return send("/rest/v1.0/account/withdraw/card/bind", "POST", params, WithdrawCardBindResult.class);
    }

    @Override
    public WithdrawCardQueryResult withdrawCardQuery(String merchantNo) {
        WithdrawCardQueryParams params = new WithdrawCardQueryParams();
        params.setMerchantNo(merchantNo);
        return send("/rest/v1.0/account/withdraw/card/query", "GET", params, WithdrawCardQueryResult.class);
    }

    @Override
    public WithdrawOrderResult withdrawOrder(String parentMerchantNo, String merchantNo, String requestNo, String bankCardId, String orderAmount, String notifyUrl) {
        WithdrawOrderParams params = new WithdrawOrderParams();
        params.setParentMerchantNo(parentMerchantNo);
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        params.setBankCardId(bankCardId);
        params.setOrderAmount(orderAmount);
        params.setNotifyUrl(notifyUrl);
        return send("/rest/v1.0/account/withdraw/order", "POST", params, WithdrawOrderResult.class);
    }

    @Override
    public WithdrawOrderQueryResult withdrawOrderQuery(String merchantNo, String requestNo) {
        WithdrawOrderQueryParams params = new WithdrawOrderQueryParams();
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        return send("/rest/v1.0/account/withdraw/system/query", "GET", params, WithdrawOrderQueryResult.class);
    }

    @Override
    public AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo) {
        AccountRechargeParams params = new AccountRechargeParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        params.setFeeType("INTER");
        params.setAmount(amount);
        ExtParams4BankPay ext = new ExtParams4BankPay(bankCode, bankAccountNo);
        params.setRequestExtParams4BankPay(ext);
        return send2("/rest/v1.0/account/recharge", "POST", params, AccountRechargeResult.class);
    }

    @Override
    public AccountRechargeQueryResult accountRechargeQuery(String merchantNo, String requestNo) {
        AccountRechargeQueryParams params = new AccountRechargeQueryParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        return send("/rest/v1.0/account/recharge/query", "GET", params, AccountRechargeQueryResult.class);
    }

    @Override
    public AccountTransferOrderResult transferB2bOrder(String requestNo, String fromMerchantNo, String toMerchantNo, String orderAmount, String notifyUrl) {
        AccountTransferOrderParams params = new AccountTransferOrderParams();
        params.setParentMerchantNo("10089066338");
        params.setRequestNo(requestNo);
        params.setFromMerchantNo(fromMerchantNo);
        params.setToMerchantNo(toMerchantNo);
        params.setOrderAmount(orderAmount);
        params.setNotifyUrl(notifyUrl);
        return send("/rest/v1.0/account/transfer/b2b/order", "POST", params, AccountTransferOrderResult.class);
    }

    @Override
    public AccountTransferOrderQueryResult transferB2bOrderQuery(String merchantNo, String requestNo) {
        AccountTransferOrderQueryParams params = new AccountTransferOrderQueryParams();
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        return send("/rest/v1.0/account/transfer/system/query", "GET", params, AccountTransferOrderQueryResult.class);
    }

    @Override
    public AccountPayOrderResult accountPayOrder(String merchantNo, String requestNo, String orderAmount,
                                                 String receiverAccountName,
                                                 String receiverAccountNo, String receiverBankCode, String bankAccountType,
                                                 String branchBankCode, String notifyUrl) {
        AccountPayOrderParams params = new AccountPayOrderParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        params.setOrderAmount(orderAmount);
        params.setReceiverAccountName(receiverAccountName);
        params.setReceiverAccountNo(receiverAccountNo);
        params.setReceiverBankCode(receiverBankCode);
        params.setBankAccountType(bankAccountType);
        params.setBranchBankCode(branchBankCode);
        params.setNotifyUrl(notifyUrl);
        return send("/rest/v1.0/account/pay/order", "POST", params, AccountPayOrderResult.class);
    }

    @Override
    public AccountPayOrderQueryResult accountPayOrderQuery(String merchantNo, String requestNo) {
        AccountPayOrderQueryParams params = new AccountPayOrderQueryParams();
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        return send("/rest/v1.0/account/pay/system/query", "GET", params, AccountPayOrderQueryResult.class);
    }

    @Override
    public FundBillFlowQueryResult fundBillFlowQuery(String parentMerchantNo, String startDate, String endDate, String merchantNo,
                                                     Integer page, Integer size) {
        FundBillFlowQueryParams params = new FundBillFlowQueryParams();
        params.setParentMerchantNo(parentMerchantNo);
        params.setMerchantNo(merchantNo);
        params.setStartDate(startDate);
        params.setEndDate(endDate);
        params.setPage(page);
        params.setSize(size);
        return send("/rest/v1.0/std/bill/fundbill/flow/query", "GET", params, FundBillFlowQueryResult.class);
    }

    @Override
    public AccountReceiptResult accountReceiptGet(String merchantNo, String orderNo, String requestNo, String tradeType, String orderData) {
        AccountReceiptParams params = new AccountReceiptParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setOrderNo(orderNo);
        params.setTradeType(tradeType);
        params.setRequestNo(requestNo);
        params.setOrderDate(orderData);
        return send("/rest/v1.0/account/receipt/get", "GET", params, AccountReceiptResult.class);
    }

    @Override
    public TradeOrderResult tradeOrder(String merchantNo, String orderId, String orderAmount, String goodsName, String notifyUrl, String memo, String redirectUrl) {
        TradeOrderParams params = new TradeOrderParams(merchantNo, orderId, orderAmount, goodsName, notifyUrl, memo, redirectUrl);
        String valueByKey = systemConfigService.getValueByKey("yopParentMerchantNo");
        if(StringUtils.isNotEmpty(valueByKey)){
            params.setParentMerchantNo(valueByKey);
        }else{
            params.setParentMerchantNo("10089066338");
        }
        return send("/rest/v1.0/trade/order", "POST", params, TradeOrderResult.class);
    }

    @Override
    public String quickPay(String merchantNo, String userNo, String orderId, String orderAmount, String goodsName,
                           String notifyUrl, String memo, String redirectUrl) {
        TradeOrderResult tradeOrderResult = tradeOrder(merchantNo, orderId, orderAmount, goodsName, notifyUrl, memo, redirectUrl);
        if (tradeOrderResult == null || !tradeOrderResult.validate()) {
            throw new RuntimeException("调用快捷支付失败");
        }
        // 易宝收银台地址
        Map<String, String> params = new HashMap<>();
        params.put("appKey", "app_10089066338");
        params.put("merchantNo", tradeOrderResult.getParentMerchantNo());
        params.put("token", tradeOrderResult.getToken());
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("directPayType", "YJZF");
        params.put("cardType", "");
        params.put("userNo", userNo);
        params.put("userType", "USER_ID");
        params.put("ext", "");
        StringBuilder sb = new StringBuilder();
        String[] CASHIER = {"appKey", "merchantNo", "token", "timestamp", "directPayType", "cardType", "userNo", "userType", "ext"};
        for (int i = 0; i < CASHIER.length; i++) {
            String name = CASHIER[i];
            String value = params.get(name);
            if (i != 0) {
                sb.append("&");
            }
            sb.append(name).append("=").append(value);
        }
        PrivateKey privateKey = RSAKeyUtils.string2PrivateKey(PRIVATE_KEY);
        String sign = RSA.sign(sb.toString(), privateKey, DigestAlgEnum.SHA256) + "$SHA256";
        return "https://cash.yeepay.com/cashier/std" + "?sign=" + sign + "&" + sb;
    }

    @Override
    public WechatAliPayPayResult wechatAlipayPay(String merchantNo, String userNo, String orderId, String orderAmount, String goodsName,
                                                 String notifyUrl, String memo, String redirectUrl, String payWay, String channel, String appId, String openId, String ip) {
        // 交易下单
        TradeOrderResult tradeOrderResult = tradeOrder(merchantNo, orderId, orderAmount, goodsName, notifyUrl, memo, redirectUrl);
        if (tradeOrderResult == null || !tradeOrderResult.validate()) {
            throw new RuntimeException("调用快捷支付失败");
        }
        // 易宝聚合支付下单
        WechatAlipayPayParams params = new WechatAlipayPayParams(orderId, new BigDecimal(orderAmount),
                notifyUrl, payWay, channel,
                appId, openId, ip, "OFFLINE", "REAL_TIME");
        params.setUniqueOrderNo(tradeOrderResult.getUniqueOrderNo());
        params.setToken(tradeOrderResult.getToken());
        params.setMerchantNo(merchantNo);
        params.setParentMerchantNo(tradeOrderResult.getParentMerchantNo());
        return send("/rest/v1.0/aggpay/pre-pay", "POST", params, WechatAliPayPayResult.class);
    }

    @Override
    public TradeOrderQueryResult queryPayResult(String merchantNo, String orderId) {
        String parentMerchantNo = "10089066338";
        String valueByKey = systemConfigService.getValueByKey("yopParentMerchantNo");
        if(StringUtils.isNotEmpty(valueByKey)) {
            parentMerchantNo = valueByKey;
        }
        TradeOrderQueryParams params = new TradeOrderQueryParams(parentMerchantNo, merchantNo, orderId);
        return send("/rest/v1.0/trade/order/query", "GET", params, TradeOrderQueryResult.class);
    }

    @Override
    public TradeRefundResult tradeRefund(String merchantNo, String orderId, String refundOrderId, String amt) {
        TradeRefundResult tradeRefundResult = new TradeRefundResult();

        // 支付支付订单
        TradeOrderQueryResult tradeOrderQueryResult = queryPayResult(merchantNo, orderId);
        if (tradeOrderQueryResult == null || !tradeOrderQueryResult.ifSuccess()) {
            throw new RuntimeException("订单未支付成功不允许退款:" + orderId);
        }

        // 是否已经退款
        RefundQueryResult refundQueryResult = refundQuery(merchantNo, orderId, refundOrderId);
        if (refundQueryResult != null && !StringUtils.isEmpty(refundQueryResult.getCode())) {
            if (refundQueryResult.ifSuccess()) {
                tradeRefundResult.setStatus("SUCCESS");
                return tradeRefundResult;
            }
            if (!refundQueryResult.ifCanRefund()) {
                tradeRefundResult.setStatus("PROCESSING");
                return tradeRefundResult;
            }
        }

        TradeRefundParams params = new TradeRefundParams(orderId, refundOrderId, amt);
        params.setParentMerchantNo(tradeOrderQueryResult.getParentMerchantNo());
        params.setMerchantNo(merchantNo);
        params.setRefundAccountType(YopEnums.AccountTypeEnum.待结算账户.getValue());
        tradeRefundResult = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        if (!tradeRefundResult.validate()) {
            params.setRefundAccountType(YopEnums.AccountTypeEnum.商户资金账户.getValue());
            tradeRefundResult = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        }
        if (!tradeRefundResult.validate()) {
            throw new RuntimeException("调用退款异常:" + refundOrderId);
        }
        return tradeRefundResult;
    }

    @Override
    public RefundQueryResult refundQuery(String merchantNo, String orderId, String refundOrderId) {
        RefundQueryParams params = new RefundQueryParams(merchantNo, orderId, refundOrderId);
        params.setParentMerchantNo("10089066338");
        String valueByKey = systemConfigService.getValueByKey("yopParentMerchantNo");
        if(StringUtils.isNotEmpty(valueByKey)) {
            params.setParentMerchantNo(valueByKey);
        }
        return send("/rest/v1.0/trade/refund/query", "GET", params, RefundQueryResult.class);
    }

    public <T> T send3(String url, String method, BaseYopRequest parameters, Class<T> responseClass) {
        //生成易宝请求
        YopRequest request = new YopRequest(url, method);
        //设置参数
        Map<String, Object> mapObj = JacksonTool.objectToMap(parameters);
        for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
            if (entry.getValue() != null) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String requestText = JacksonTool.toJsonString(request.getParameters().asMap());
        log.info("易宝请求参数" + requestText);
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            BaseYopResponse resp = (BaseYopResponse) JacksonTool.toObject(response.getResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public <T> T send(String url, String method, BaseYopRequest parameters, Class<T> responseClass) {
        //生成易宝请求
        YopRequest request = new YopRequest(url, method);
        //设置参数
        Map<String, Object> mapObj = JacksonTool.objectToMap(parameters);
        for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
            if (entry.getValue() != null) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String requestText = JacksonTool.toJsonString(request.getParameters().asMap());
        log.info("易宝请求参数" + requestText);
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            BaseYopResponse resp = (BaseYopResponse) JacksonTool.toObject(response.getStringResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public <T> T send2(String url, String method, BaseYopRequest parameters, Class<T> responseClass) {
        //生成易宝请求
        YopRequest request = new YopRequest(url, method);
        request.setContent(JacksonTool.toJsonString(parameters));
        log.info("易宝请求参数" + JacksonTool.toJsonString(parameters));
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            BaseYopResponse resp = (BaseYopResponse) JacksonTool.toObject(response.getStringResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public MerFileUploadResponse upload(InputStream inputStream) {
        YopRequest request = new YopRequest("/yos/v1.0/sys/merchant/qual/upload", "POST");
        request.addMultiPartFile("merQual", inputStream);
        YosUploadResponse uploadResponse = makeUploadRequest(request);
        return JacksonTool.toObject(uploadResponse.getStringResult(), MerFileUploadResponse.class);
    }

    private YosUploadResponse makeUploadRequest(YopRequest request) {
        log.info("易宝资质上传参数" + JacksonTool.toJsonString(request.getParameters().asMap()));
        try {
            YosUploadResponse response = yopClient.upload(request);
            log.info("易宝资质上传返回参数" + response.getStringResult());
            return response;
        } catch (YopClientException e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
