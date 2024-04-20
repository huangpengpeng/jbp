package com.jbp.service.service;

import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;

import java.math.BigDecimal;

public interface DegreePayService {

    AcctInfoResult queryAcct(LztAcct lztAcct);

    LztQueryAcctInfoResult queryBankAcct(LztAcct lztAcct);

    AcctSerialResult queryAcctSerial(LztAcct lztAcct, String startTime, String entTime, Integer pageNo);

    LztFundTransferResult fundTransfer(LztAcct lztAcct, String txnSeqno, String bankAccountNo, String amt, String notifyUrl);

    LztQueryFundTransferResult queryFundTransfer(LztAcct lztAcct, String txnSeqno);


    ReceiptDownloadResult receiptDownload(LztAcct lztAcct, String receipt_accp_txno, String txnSeqno,  String token, String tradeType);

}
