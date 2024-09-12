package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryRecordEditRequest对象", description = "中奖记录编辑对象")
public class LotteryRecordFrontRequest implements Serializable {

    @ApiModelProperty("地址")
    private String address;
}
