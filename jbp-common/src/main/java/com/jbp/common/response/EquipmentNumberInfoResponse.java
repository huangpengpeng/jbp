package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EquipmentListResponse对象", description = "共享仓设备列表对象")
public class EquipmentNumberInfoResponse implements Serializable {




    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "店主id")
    private Long storeUserId;

    @ApiModelProperty(value = "类型（充值，使用）")
    private String type;

    @ApiModelProperty(value = "次数")
    private Integer number;

    @ApiModelProperty(value = "启动id")
    private Long activateId;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单号")
    private String orderSn;
    @ApiModelProperty(value = "剩余次数")
    private Integer count;

    @ApiModelProperty(value = "用户名")
    private String username;
}
