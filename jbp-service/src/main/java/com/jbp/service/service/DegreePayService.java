package com.jbp.service.service;

import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.agent.LztPayChannel;

import java.math.BigDecimal;
import java.util.Date;

public interface DegreePayService {

    UserInfoResult queryUserInfo( LztPayChannel lztPayChannel, LztAcctOpen lztAcctOpen);

    AcctInfoResult queryAcct(LztAcct lztAcct);

    LztQueryAcctInfoResult queryBankAcct(LztAcctApply lztAcctApply);

    AcctSerialResult queryAcctSerial(LztAcct lztAcct, String startTime, String entTime, Integer pageNo, Integer limit);

    /**
     * 划拨
     */
    LztFundTransferResult fundTransfer(LztAcct lztAcct, String txnSeqno, String bankAccountNo, String amt, String notifyUrl);

    /**
     * 划拨结果
     */
    LztQueryFundTransferResult queryFundTransfer(LztAcct lztAcct, String txnSeqno);

    /**
     * 代付
     */
    LztTransferResult transfer(LztAcct lztAcct, String txnPurpose, String txn_seqno,
                               String amt, String feeAmt, String pwd, String random_key, String payee_type,
                               String bank_acctno, String bank_code, String bank_acctname, String cnaps_code, String postscript,
                               String ip);

    /**
     * 连续代付
     */
    LztTransferResult transfer2(LztAcct lztAcct, String txnPurpose, String txn_seqno,
                               String amt, String feeAmt, String pwd, String random_key, String payee_type,
                               String bank_acctno, String bank_code, String bank_acctname, String cnaps_code, String postscript,
                               String ip);
    /**
     * 代付结果
     */
    QueryWithdrawalResult transferQuery(LztAcct lztAcct, String txnSeqno);


    /**
     * 内部代发
     */
    TransferMorepyeeResult transferMorepyee(LztAcct lztAcct, String orderNo,
                                            Double amt, BigDecimal fee, String txnPurpose, String pwd, String randomKey,
                                            String payeeId, String ip, String notify_url);
    /**
     * 内部代发结果
     */
    QueryPaymentResult queryTransferMorepyee(LztAcct lztAcct, String txnSeqno);


    /**
     * 提现
     */
    WithdrawalResult withdrawal(LztAcct lztAcct, String drawNo, BigDecimal amt, BigDecimal fee, String postscript,
                                String password, String random_key, String ip, String notifyUrl);


    /**
     * 提现结果
     */
    QueryWithdrawalResult queryWithdrawal(LztAcct lztAcct, String txnSeqno);

    ReceiptDownloadResult receiptDownload(LztAcct lztAcct, String receipt_accp_txno, String txnSeqno,
                                          String token, String tradeType, String txnTime);

}
