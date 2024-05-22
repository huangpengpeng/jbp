package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountOpenResult extends BaseYopResponse {

    private String returnCode; // AM00000 成功，其他请参考错误码

    private String returnMsg; // 返回描述信息

    private String requestNo; // 商户订单号


    private String orderNo; // 易宝唯一订单号


    /**
     * 开户状态
     * 可选项如下:
     * PROCESS:请求已受理
     * FAIL:失败
     */
    private String status;

    private String signUrl; // 签约地址


    /**
     * 额外认证类型
     * 可选项如下:
     * NO_AUTH:无须认证
     * SHORT_MSG_AUTH:短信认证
     */
    private String authType;


    @Override
    public boolean validate() {
        return "AM00000".equals(returnCode);
    }
}
