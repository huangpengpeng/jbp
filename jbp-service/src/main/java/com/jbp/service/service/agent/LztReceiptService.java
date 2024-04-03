package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.lianlian.result.ReceiptDownloadResult;
import com.jbp.common.model.agent.LztReceipt;

import java.util.List;

public interface LztReceiptService extends IService<LztReceipt> {

    List<LztReceipt> getList(Integer merId, String tradeTxnSeqno, String memo);

    LztReceipt add(Integer merId, String tradeTxnSeqno, String memo, String tradeBillType, String totalAmount);

    ReceiptDownloadResult download(Long id);

}
