package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountTransferOrderQueryParams extends BaseYopRequest {

    private String merchantNo;//  商户

    private String requestNo;//  商户请求号


}
