package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class ReceiptDownloadResult {

    private String ret_code;
    private String ret_msg;
    private String oid_partner;

    // 原交易商户系统唯一交易流水号。
    private String trade_txn_seqno;

    // 原交易ACCP系统单号。
    private String trade_accp_txno;

    private String total_amount;


    // ACCP系统唯一定位一笔单子的电子回单。
    private String receipt_accp_txno;

    // 电子回单生成状态。
    private String receipt_status;


    // 电子回单生集合文件 压缩文件做Base64编码后传输，字符编码UTF-8
    private String receipt_sum_file;


    // 电子回单信息集合receiptInfo
    private List<ReceiptInfo> receiptInfo;
}
