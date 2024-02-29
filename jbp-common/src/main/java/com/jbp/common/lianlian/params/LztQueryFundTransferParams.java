package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztQueryFundTransferParams {


    /**
     * 交易服务器时间戳格式：yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 外部用户号
     */
    private String user_id;

    /**
     * 建议优先使用ACCP系统交易单号 二选一，
     */
    private String txn_seqno;

    /**
     * ACCP系统交易单号 二选一，
     */
    private String accp_txno;
}
