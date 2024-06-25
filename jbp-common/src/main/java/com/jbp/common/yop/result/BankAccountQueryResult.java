package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountQueryResult extends BaseYopResponse {
    // 返回状态码AM00000 成功
    private String returnCode;

    private String returnMsg;

    // 业务主体商编
    private String merchantNo;

    private String requestNo;

    private String orderNo;

    /**
     * SUCCESS:成功
     * FAIL:失败
     * PROCESS:请求已受理
     */
    private String status;

    // 此字段为后续请求查询余额，资金划拨等服务接口时必须参数，请着重记录。
    private String bankAccountNo;

    // 此字段是银行收款所需账号，替换外部结算卡时请替换为此银行客户号，银行才能正常接收商户结算资金
    private String bankCustomerNo;

    // 开户请求时间 格式:yyyy-MM-dd HH:mm:ss
    private String openRequestTime;

    // 开户完成时间(成功时返回)格式:yyyy-MM-dd HH:mm:ss
    private String openCompleteTime;

    private String failReason;

    /**
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
