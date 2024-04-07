package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.service.dao.agent.LztTransferDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.agent.LztTransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztTransferServiceImpl extends ServiceImpl<LztTransferDao, LztTransfer> implements LztTransferService {

    @Resource
    private LztService lztService;


    @Override
    public LztTransfer add(Integer merId, String payerId, String payerName, String txnSeqno, String accpTxno, BigDecimal amt,
                           BigDecimal feeAmount, String payeeType, String bankAcctNo, String bankCode, String bankAcctName, String cnapsCode) {
        LztTransfer lztTransfer = new LztTransfer(merId, payerId, payerName, txnSeqno, accpTxno, amt, feeAmount, payeeType, bankAcctNo,
                bankCode, bankAcctName, cnapsCode);




        save(lztTransfer);

        return null;
    }
}
