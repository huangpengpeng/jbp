package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountReceiptResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String data;

    @Override
    public boolean validate() {
        return "UA00000".equals(returnCode);
    }
}
