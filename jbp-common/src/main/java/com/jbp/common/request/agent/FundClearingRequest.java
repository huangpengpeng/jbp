package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingRequest对象", description = "佣金发放记录请求对象")
public class FundClearingRequest {
    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("开始结算时间")
    private Date startClearingTime;

    @ApiModelProperty("结束结算时间")
    private Date endClearingTime;

    @ApiModelProperty("开始创建时间")
    private Date startCreateTime;

    @ApiModelProperty("结束创建时间")
    private Date endCreateTime;

    @ApiModelProperty("结算状态  已创建  待审核  待出款  已出款  已取消  已拦截")
    private String status;
}
