package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReceiptDownloadParams {

    private String timestamp;

    private String oid_partner;

    // 商户系统唯一交易流水号。二选一，建议优先使用ACCP系统交易单号。
    private String receipt_accp_txno;

    private String token;


}
