package com.jbp.service.service;

import com.jbp.common.lianlian.params.FindPasswordVerifyParams;
import com.jbp.common.lianlian.result.*;

import java.math.BigDecimal;

public interface LztService {

    /**
     * 创建连连用户
     */
    OpenacctApplyResult createUser(String oidPartner, String priKey, String txnSeqno,
                                   String userId, String userType, String notifyUrl, String returnUrl, String flagChnl, String businessScope);

    /**
     * 查询连连用户
     */
    UserInfoResult queryUserInfo(String oidPartner, String priKey, String userId);

    /**
     * 查询连连账户资金
     */
    AcctInfoResult queryAcct(String oidPartner, String priKey, String userId, String userType);


    /**
     * 银行账户申请
     */
    LztOpenacctApplyResult createBankUser(String oidPartner, String priKey, String userId, String txnSeqno, String shopId,
                                          String shopName, String province, String city, String area, String address, String notifyUrl, String openBank);


    /**
     * 查询银行账户资金
     */
    LztQueryAcctInfoResult queryBankAcct(String oidPartner, String priKey, String userId);

    /**
     * 来账通账户资金划拨
     */
    LztFundTransferResult fundTransfer(String oidPartner, String priKey, String txnSeqno, String userId, String bankAccountNo, String amt, String notifyUrl);

    /**
     * 来账通账户资金划拨查询
     */
    LztQueryFundTransferResult queryFundTransfer(String oidPartner, String priKey, String userId, String txnSeqno);

    /**
     * 来账通内部代发申请
     */
    TransferMorepyeeResult transferMorepyee(String oidPartner, String priKey, String payerId, String orderNo, Double amt, String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String notify_url);

    /**
     * 来账通内部代发结果查询
     */
    QueryPaymentResult queryTransferMorepyee(String oidPartner, String priKey, String txnSeqno);

    /**
     * 来账通提现
     */
    WithdrawalResult withdrawal(String oidPartner, String priKey, String payeeNo, String drawNo, BigDecimal amt,  BigDecimal fee, String postscript,
                                String password, String random_key, String ip, String notifyUrl, String linked_acctno);

    /**
     * 来账通提现
     */
    WithdrawalCheckResult withdrawalCheck(String oidPartner, String priKey, String txn_seqno, String total_amount, String check_result, String check_reason, String fee);

    /**
     * 来账通提现查询
     */
    QueryWithdrawalResult queryWithdrawal(String oidPartner, String priKey, String txnSeqno);

    /**
     * 来账通获取密码空间token
     */
    ApplyPasswordElementResult getPasswordToken(String oidPartner, String priKey, String userId, String payCode, String pyee_name, BigDecimal amount, String scan);


    /**
     * 资金流水
     */
    AcctSerialResult queryAcctSerial(String oidPartner, String priKey, String userId, String userType, String dateStart, String endStart, String flagDc, String pageNo);

    /**
     * 短验二次确认
     */
    ValidationSmsResult validationSms(String oidPartner, String priKey, String payer_id, String txn_seqno, String total_amount, String token, String verify_code);

    /**
     * 查询用户绑卡
     */
    QueryLinkedAcctResult queryLinkedAcct(String oidPartner, String priKey, String payer_id);


    /**
     * 回执申请
     */
    ReceiptProduceResult receiptProduce(String oidPartner, String priKey, String txn_seqno, String amt, String Trade_bill_type, String memo, String trade_txn_seqno);


    /**
     * 回执下载
     * https://accpapi.lianlianpay.com/v1/offlinetxn/receipt-download
     */

    ReceiptDownloadResult receiptDownload(String oidPartner, String priKey, String receipt_accp_txno, String token);


    /**
     * 找回密码申请
     */
    FindPasswordApplyResult findPasswordApply (String oidPartner, String priKey, String user_id, String linked_acctno, String ip);

    /**
     * 找回密码验证
     */
    FindPasswordVerifyResult findPasswordVerify (String oidPartner, String priKey, String user_id, String token, String verify_code, String random_key, String password);


    /**
     * 大额行号查询
     *  https://accpapi.lianlianpay.com/v1/acctmgr/query-cnapscode
     */
    QueryCnapsCodeResult queryCnapsCode(String oidPartner, String priKey, String bank_code, String brabank_name, String city_code);


    /**
     * 代付
     */
    LztTransferResult transfer(String oidPartner, String priKey, String payerId, String txnPurpose, String ip);
}


