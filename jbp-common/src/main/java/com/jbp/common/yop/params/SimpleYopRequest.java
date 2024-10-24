package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleYopRequest extends BaseYopRequest {
    private String parentMerchantNo;
}
