package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawCardQueryParams extends BaseYopRequest {

    private String merchantNo;// 提现商户号

}
