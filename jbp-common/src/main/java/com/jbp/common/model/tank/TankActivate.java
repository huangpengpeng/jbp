package com.jbp.common.model.tank;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_tank_activate")
@ApiModel(value="TankActivate对象", description="共享仓活动表")
public class TankActivate extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;


    @ApiModelProperty(value = "操作订单号")
    private String operationSn;

    @ApiModelProperty(value = "设备id")
    private Long equipmentId;

    @ApiModelProperty(value = "启动用户")
    private Long activateUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;


}
