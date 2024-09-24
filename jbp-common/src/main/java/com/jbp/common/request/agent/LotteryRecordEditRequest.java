package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryRecordEditRequest对象", description = "中奖记录编辑对象")
public class LotteryRecordEditRequest implements Serializable {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer id;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货省")
    private String province;
    @ApiModelProperty(value = "收货市")
    private String city;
    @ApiModelProperty(value = "收货区")
    private String district;
    @ApiModelProperty(value = "收货街道")
    private String street;
    @ApiModelProperty("详细地址")
    private String address;

}
