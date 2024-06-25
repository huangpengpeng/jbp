package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class OnlineBankOrderResult extends BaseYopResponse {

    private String returnCode;

    private String returnMsg;

    private String orderNo;

    private String requestNo;

    private String merchantNo;

    /**
     * 可选项如下:
     * INIT:处理中
     * FAIL:失败
     */
    private String status;


    private String orderAmount;

    private String payUrl;


    @Override
    public boolean validate() {
        return "UA00001".equals(returnCode);
    }
}
