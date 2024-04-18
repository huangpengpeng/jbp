package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountPayOrderQueryParams extends BaseYopRequest {

    private String merchantNo;

    private String requestNo;
}
