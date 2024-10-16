package com.jbp.service.service.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayCashier;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PayCashierMng extends IService<PayCashier> {

    PayCashier save(String appKey, String txnSeqno, BigDecimal payAmt, List<OrderInfoDTO> orderInfo, String ext, Date createTime, Date expireTime);

    PayCashier getByTxnSeqno(String appKey, String txnSeqno);

    PayCashier getByToken(String token);

    List<String> getPayMethod(String token);
}
