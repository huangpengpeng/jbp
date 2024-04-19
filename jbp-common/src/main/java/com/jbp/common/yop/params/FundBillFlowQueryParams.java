package com.jbp.common.yop.params;


import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FundBillFlowQueryParams extends BaseYopRequest {

    private String startDate; // yyyy-MM-dd

    private String endDate;

    private String parentMerchantNo;

    private String merchantNo;

    private Integer page;

    private Integer size;
}
