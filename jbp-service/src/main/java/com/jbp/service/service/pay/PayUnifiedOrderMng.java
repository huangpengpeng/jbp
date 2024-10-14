package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayUnifiedOrder;

import java.util.List;

public interface PayUnifiedOrderMng extends IService<PayUnifiedOrder> {


     PayUnifiedOrder create(String token, String method);
     PayUnifiedOrder getByTxnSeqno(Long payUserId, String txnSeqno);

}
