package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退款申请 请求参数
 */
@Data
@EqualsAndHashCode
public class RefundQueryParams {
    private String timestamp;
    private String oid_partner;
    private String refund_seqno;

}
