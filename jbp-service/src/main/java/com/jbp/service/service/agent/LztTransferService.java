package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LztTransfer;

import java.math.BigDecimal;

public interface LztTransferService extends IService<LztTransfer> {


    LztTransfer add(Integer merId, String payerId, String payerName, String txnSeqno,
                    String accpTxno, BigDecimal amt, BigDecimal feeAmount, String payeeType, String bankAcctNo,
                    String bankCode, String bankAcctName, String cnapsCode);
}
