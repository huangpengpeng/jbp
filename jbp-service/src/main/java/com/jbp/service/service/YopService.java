package com.jbp.service.service;

import com.jbp.common.yop.params.BankAccountOpenParams;
import com.jbp.common.yop.params.MerchantInfoModifyParams;
import com.jbp.common.yop.params.OnlineBankOrderParams;
import com.jbp.common.yop.params.RegisterMicroH5Params;
import com.jbp.common.yop.result.*;

public interface YopService {


    /**
     * H5注册个人入网
     *
     */
    RegisterMicroH5Result registerMicroH5(RegisterMicroH5Params params);


    /**
     * 小微入网
     * /rest/v2.0/mer/register/saas/micro
     */
    RegisterMicroResult registerMicro(String requestNo, String signName,  String id_card, String frontUrl,
                                      String backUrl, String mobile,  String province, String city, String district,
                                      String address, String bankCardNo, String bankCode, String notifyUrl);


    /**
     * 入网进度查询
     * /rest/v2.0/mer/register/query
     */
    RegisterQueryResult registerQuery(String requestNo);


    /**
     * 商户信息变更
     * /rest/v1.0/mer/merchant/info/modify
     */
    MerchantInfoModifyResult  merchantInfoModify(MerchantInfoModifyParams params);


    String upload(String url);

    /**
     * 银行开户
     *
     */
    BankAccountOpenResult bankAccountOpen(BankAccountOpenParams params);


    /**
     * 充值下单
     *
     */
    OnlineBankOrderResult onlineBankOrder(OnlineBankOrderParams params);

    /**
     * 开户进度查询
     */
    BankAccountQueryResult bankAccountQuery(String merchantNo, String requestNo);

    /**
     * 查询银行余额
     */
    BankAccountBalanceQueryResult bankAccountBalanceQuery(String merchantNo, String bankCode, String accountNo);

    /**
     * 查询资金账户余额
     */
    AccountBalanceQueryResult accountBalanceQuery(String merchantNo);


    /**
     *
     * 查询全部余额
     * /rest/v1.0/account/accountinfos/query
     */
    AllAccountBalanceQueryResult allAccountBalanceQuery(String merchantNo);


    /**
     * 提现卡绑定
     * /rest/v1.0/account/withdraw/card/bind
     */

    /**
     * 提现卡bin查询
     */
    WithdrawCardQueryResult withdrawCardQuery(String merchantNo);

    /**
     * 提现下单
     */
    WithdrawOrderResult withdrawOrder(String parentMerchantNo, String merchantNo, String requestNo, String bankCardId, String orderAmount, String notifyUrl);

    /**
     * 提现查询
     */
    WithdrawOrderQueryResult withdrawOrderQuery(String merchantNo, String requestNo);

    /**
     * 充值 --- 划拨
     */
    AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo);

    /**
     * 充值 --- 划拨 查询
     */
    AccountRechargeQueryResult accountRechargeQuery(String merchantNo, String requestNo);

    /**
     * 转账
     */
    AccountTransferOrderResult transferB2bOrder(String requestNo, String fromMerchantNo, String toMerchantNo, String orderAmount, String notifyUrl);

    /**
     * 转账查询
     */
    AccountTransferOrderQueryResult transferB2bOrderQuery(String merchantNo, String requestNo);

    /**
     * 代付
     *
     */

    AccountPayOrderResult accountPayOrder(String merchantNo, String requestNo,
                                          String orderAmount, String receiverAccountName,
                                          String receiverAccountNo, String receiverBankCode,
                                          String bankAccountType, String branchBankCode, String notifyUrl);

    /**
     * 代付查询
     *
     */
    AccountPayOrderQueryResult accountPayOrderQuery(String merchantNo, String requestNo);

    /**
     * 资金流水
     */
    FundBillFlowQueryResult fundBillFlowQuery(String startDate, String endDate, String merchantNo, Integer page, Integer size);


    /**
     * 回执下载
     */
    AccountReceiptResult accountReceiptGet(String merchantNo, String requestNo, String tradeType);




}
