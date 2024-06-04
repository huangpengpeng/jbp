package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RegisterMicroResult extends BaseYopResponse {

    private String returnCode;//响应编码 示例值：NIG00000

    private String returnMsg;//响应描述 示例值：请求成功

    private String requestNo;//入网请求号 示例值：1231231123

    private String applicationNo;//申请单编号 示例值：TYSHRW20200713999999

    /**
     * REVIEWING:申请审核中
     * REVIEW_BACK:申请已驳回
     * BUSINESS_OPENING:业务开通中
     * COMPLETED:申请已完成
     * 示例值：REVIEWING
     */
    private String applicationStatus;//申请状态

    private String merchantNo;//商户编号

    @Override
    public boolean validate() {
        return "NIG00000".equals(returnCode);
    }
}
