package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WithdrawCardModifyResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
