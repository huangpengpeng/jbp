package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountQueryParams extends BaseYopRequest {

    // 请求流水号
    private String requestNo;

    // 服务商商户号 // 10089066338
    private String parentMerchantNo;

    // 查询商户号  10090225827
    private String merchantNo;
}
