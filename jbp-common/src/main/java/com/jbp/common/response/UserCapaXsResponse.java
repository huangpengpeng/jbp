package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaXsResponse对象", description = "升星查看响应对象对象")
public class UserCapaXsResponse {



    private Integer rankNum;

    private String rankName;

    private BigDecimal teamAmt;
}
