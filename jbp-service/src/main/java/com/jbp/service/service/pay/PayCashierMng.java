package com.jbp.service.service.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.model.pay.PayCashier;
import com.jbp.common.request.pay.PayCashRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PayCashierMng extends IService<PayCashier> {


    PayCashier save(PayCashRequest request);


    PayCashier getByTxnSeqno(String appKey, String txnSeqno);

    PayCashier getByToken(String token);

    List<String> getPayMethod(String token);
}
