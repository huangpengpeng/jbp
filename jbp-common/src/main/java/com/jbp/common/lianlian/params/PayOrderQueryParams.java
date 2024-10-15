package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PayOrderQueryParams {

    private String  mch_id; // 商户编号，商户在平台上开设的商户号码，为18位数字，如：201304121000001004

    private String txn_seqno; // 业务单号

}
