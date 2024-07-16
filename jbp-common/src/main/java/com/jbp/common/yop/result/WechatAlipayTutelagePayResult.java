package com.jbp.common.yop.result;

import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WechatAlipayTutelagePayResult extends BaseYopResponse {

    private String code;
    private String message;

    private String orderId;

    private String uniqueOrderNo;

    private String prePayTn;

    private String appId;

    private String miniProgramPath;

    private String miniProgramOrgId;

    @Override
    public boolean validate() {
        if (StringUtils.equals(this.getCode(), "00000")) {
            return true;
        }
        this.setErrorMessage(this.getMessage());
        return false;
    }
}
