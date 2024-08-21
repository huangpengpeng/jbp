package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class PaymentGwResult {

    private String ret_code; // 0000 表示交易申请成功

    private String ret_msg;

    private String oid_partner;// ACCP系统分配给平台商户的唯一编号。

    private String user_id;// 商户用户唯一编号

    private BigDecimal total_amount;

    private String txn_seqno;

    private String accp_txno;

    private String token;//支付授权令牌，有效期30分钟。当交易需要二次验证的时候，需要通过token调用调用交易二次短信验证接口
    private String gateway_url;// 长度不限网银支付时返回

    private String payload; // 支付参数集合。返回外部渠道的标准支付提交参数，微信/支付宝/云闪付可参考官方文档。

    public boolean validate() {
        return "0000".equals(ret_code);
    }

}
