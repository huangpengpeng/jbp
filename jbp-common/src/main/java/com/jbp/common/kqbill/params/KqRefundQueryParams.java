package com.jbp.common.kqbill.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 快钱退款查询
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KqRefundQueryParams {

    private String merchantAcctId;

    private String startDate;

    private String endDate;

    /**
     * 退款交易商家订单号
     */
    private String orderId;

    private String txnStatus="1";
}
