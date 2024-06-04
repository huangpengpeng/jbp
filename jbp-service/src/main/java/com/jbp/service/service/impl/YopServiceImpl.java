package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
<<<<<<< HEAD
import com.jbp.common.yop.YopFileSdkConfigProvider;
=======
import com.jbp.common.yop.constants.YopProducts;
>>>>>>> 5eb86187ab45ee8c7701a36f3731f6997b3b5e75
import com.jbp.common.yop.dto.ExtParams4BankPay;
import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;
<<<<<<< HEAD
import com.yeepay.yop.sdk.base.config.provider.YopSdkConfigProviderRegistry;import com.yeepay.yop.sdk.config.YopSdkConfig;
import com.yeepay.yop.sdk.config.provider.YopSdkConfigProvider;
=======
import com.yeepay.yop.sdk.exception.YopClientException;
>>>>>>> 5eb86187ab45ee8c7701a36f3731f6997b3b5e75
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import com.yeepay.yop.sdk.service.common.response.YosUploadResponse;
import lombok.extern.slf4j.Slf4j;
<<<<<<< HEAD

import org.springframework.beans.factory.InitializingBean;
=======
import org.apache.commons.lang3.StringUtils;
>>>>>>> 5eb86187ab45ee8c7701a36f3731f6997b3b5e75
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Slf4j
@Service
public class YopServiceImpl implements YopService {

    @Resource
    private YopClient yopClient;

    @Override
    public RegisterMicroH5Result registerMicroH5(RegisterMicroH5Params params) {
        return send("/rest/v2.0/mer/register/saas/index", "POST", params, RegisterMicroH5Result.class);
    }

    @Override
    public RegisterMicroResult registerMicro(String requestNo, String signName, String id_card, String frontUrl,
                                             String backUrl, String mobile, String province, String city, String district,
                                             String address, String bankCardNo, String bankCode, String notifyUrl) {
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
        params.setProductInfo(YopProducts.getMicroMerchant());
        return send("/rest/v2.0/mer/register/saas/micro", "POST", params, RegisterMicroResult.class);
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
    public String upload(MultipartFile file)  {
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
        params.setAmount(amount);
        ExtParams4BankPay ext = new ExtParams4BankPay(bankCode, bankAccountNo);
        params.setRequestExtParams4BankPay(ext);
        return send("/rest/v1.0/account/recharge", "POST", params, AccountRechargeResult.class);
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
    public AccountReceiptResult accountReceiptGet(String merchantNo, String orderNo, String requestNo,  String tradeType, String orderData) {
        AccountReceiptParams params = new AccountReceiptParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setOrderNo(orderNo);
        params.setTradeType(tradeType);
        params.setRequestNo(requestNo);
        params.setOrderDate(orderData);
        return send("/rest/v1.0/account/receipt/get", "GET", params, AccountReceiptResult.class);
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
       // request.withRequestConfig(null);
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
