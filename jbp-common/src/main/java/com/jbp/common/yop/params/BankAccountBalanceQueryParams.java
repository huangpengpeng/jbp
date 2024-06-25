package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountBalanceQueryParams extends BaseYopRequest {
    
    private String parentMerchantNo;

    /**
     * 银行编码
     * 可选项如下:
     * FJHTB
     * XIB
     * WHZBB
     * XWB
     * HXBXB
     * XWB_Z
     * SUNINGBANK
     */
    private String bankCode;

    // 银行卡号
    private String accountNo;

    // 查询商户号  10090225827
    private String merchantNo;
}
