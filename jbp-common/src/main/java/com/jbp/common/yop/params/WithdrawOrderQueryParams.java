package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawOrderQueryParams extends BaseYopRequest {

    private String requestNo; // 请求流水号

    private String merchantNo; // 提现卡bin
}
