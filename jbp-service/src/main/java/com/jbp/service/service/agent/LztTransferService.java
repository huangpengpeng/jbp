package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LztTransfer;

import java.math.BigDecimal;

public interface LztTransferService extends IService<LztTransfer> {


    LztTransfer add(Integer merId, String payerId, String payerName, String txnSeqno, BigDecimal amt, String payeeType, String bankAcctNo,
                    String bankCode, String bankAcctName, String cnapsCode);

    LztTransfer getByTxnSeqno(String txnSeqno);
}
