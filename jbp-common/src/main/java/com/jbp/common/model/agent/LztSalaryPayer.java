package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_salary_payer",autoResultMap = true)
@ApiModel(value="LztSalaryPayer对象", description="来账通薪资代发账户")
public class LztSalaryPayer extends BaseModel {

    public LztSalaryPayer(Integer merId, String payerId, String payerName) {
        this.merId = merId;
        this.payerId = payerId;
        this.payerName = payerName;

    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "付款人")
    private String payerId;

    @ApiModelProperty(value = "付款人")
    private String payerName;



}
