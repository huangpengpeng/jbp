package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.service.dao.agent.LztTransferDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.util.StringUtils;
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
    public LztTransfer add(Integer merId, String payerId, String payerName, String txnSeqno, BigDecimal amt,
                           String payeeType, String bankAcctNo, String bankCode, String bankAcctName, String cnapsCode) {



        if(StringUtils.isEmpty(txnSeqno)){
            txnSeqno = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通外部代发.getPrefix());
        }
        if (getByTxnSeqno(txnSeqno) != null) {
            throw new CrmebException("转账单号已经被使用");
        }

//        LztTransfer lztTransfer = new LztTransfer(merId, payerId, payerName, txnSeqno, accpTxno, amt, feeAmount, payeeType, bankAcctNo,
//                bankCode, bankAcctName, cnapsCode);
//        LztAcct lztAcct = lztAcctService.getByUserId(userId);
//        Merchant merchant = merchantService.getById(lztAcct.getMerId());
//        MerchantPayInfo payInfo = merchant.getPayInfo();
//        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + drawNo;
//
//        lztService.transfer(String oidPartner, String priKey, String payerId, String txnPurpose, String txn_seqno,
//                String amt, String feeAmt, String pwd, String random_key, String payee_type, String bank_acctno, String bank_code, String bank_acctname, String cnaps_code, String ip);
//







//        save(lztTransfer);

        return null;
    }

    @Override
    public LztTransfer getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztTransfer>().lambda().eq(LztTransfer::getTxnSeqno, txnSeqno));
    }
}
