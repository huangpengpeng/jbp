package com.jbp.service.service.impl;

import com.jbp.common.utils.JacksonTool;
import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.dto.ExtParams4BankPay;
import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class YopServiceImpl implements YopService {

    @Resource
    private YopClient yopClient;

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
    public WithdrawCardQueryResult withdrawCardQuery(String merchantNo) {
        WithdrawCardQueryParams params = new WithdrawCardQueryParams();
        params.setMerchantNo(merchantNo);
        return send("/rest/v1.0/account/withdraw/card/query", "GET", params, WithdrawCardQueryResult.class);
    }

    @Override
    public WithdrawOrderResult withdrawOrder(String merchantNo, String requestNo, String bankCardId, String orderAmount, String notifyUrl) {
        WithdrawOrderParams params = new WithdrawOrderParams();
        params.setParentMerchantNo("10089066338");
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
    public AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo, String userRequestIP) {
        AccountRechargeParams params = new AccountRechargeParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        params.setAmount(amount);
        ExtParams4BankPay ext = new ExtParams4BankPay(bankCode, userRequestIP, bankAccountNo);
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
        return send("/rest/v1.0/account/transfer/b2b/order", "GET", params, AccountTransferOrderResult.class);
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
    public FundBillFlowQueryResult fundBillFlowQuery(String startDate, String endDate, String merchantNo,
                                                     Integer page, Integer size) {
        FundBillFlowQueryParams params = new FundBillFlowQueryParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setStartDate(startDate);
        params.setEndDate(endDate);
        params.setPage(page);
        params.setSize(size);
        return send("/rest/v1.0/std/bill/fundbill/flow/query", "GET", params, FundBillFlowQueryResult.class);
    }

    @Override
    public AccountReceiptResult accountReceiptGet(String merchantNo, String requestNo, String tradeType) {
        AccountReceiptParams params = new AccountReceiptParams();
        params.setParentMerchantNo("10089066338");
        params.setMerchantNo(merchantNo);
        params.setRequestNo(requestNo);
        params.setTradeType(tradeType);
        return send("/rest/v1.0/account/receipt/get", "GET", params, AccountReceiptResult.class);
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
}
