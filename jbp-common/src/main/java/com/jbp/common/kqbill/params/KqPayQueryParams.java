package com.jbp.common.kqbill.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 快钱查询支付结果
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KqPayQueryParams {

    /**
     * 提交此查询请求的平台方的商户编号，规则：membercode加上01。
     */
    private String merchantAcctId;

    private String queryType="0";

    private String queryMode="1";

    /**
     * 订单ID
     */
    private String orderId;
}
