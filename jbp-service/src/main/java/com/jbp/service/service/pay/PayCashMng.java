package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayCash;

import java.math.BigDecimal;
import java.util.Date;

public interface PayCashMng extends IService<PayCash> {

    PayCash save(String appKey, String txnSeqno, BigDecimal payAmt, String orderInfo, String ext, Date createTime, Date expireTime);

    PayCash getByTxnSeqno(String appKey, String txnSeqno);

    PayCash getByToken(String token);
}
