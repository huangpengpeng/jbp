package com.jbp.common.yop.result;


import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.dto.FundBillFlowDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class FundBillFlowQueryResult extends BaseYopResponse {


    private String code; // 编码

    private String message; // 返回消息

    private String totalCount; // 总笔数

    private List<FundBillFlowDto> data;

    @Override
    public boolean validate() {
        return "00000".equals(code);
    }
}
