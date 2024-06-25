package com.jbp.common.yop.result;

import com.jbp.common.yop.BaseYopResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MerchantInfoModifyResult extends BaseYopResponse {

    private String returnCode;//响应编码 示例值：NIG00000


    private String returnMsg;//响应描述 示例值：请求成功

    private String applicationNo;//工单号 示例值：SHXXBGLC20220411203516526112

    /**
     * 申请状态可选项如下:
     * REVIEWING:申请审核中
     * REVIEW_BACK:申请已驳回
     * 示例值：REVIEWING
     */
    private String applicationStatus;//工单状态


    @Override
    public boolean validate() {
        return "NIG00000".equals(returnCode);
    }
}
