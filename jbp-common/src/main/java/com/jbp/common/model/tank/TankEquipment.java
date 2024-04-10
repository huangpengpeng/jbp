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
@TableName("eb_tank_equipment")
@ApiModel(value="TankEquipment对象", description="共享仓设备表")
public class TankEquipment extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;

    @ApiModelProperty(value = "舱订单id")
    private Long orderbondactivateId;

    @ApiModelProperty(value = "店主id")
    private Long storeUserId;

    @ApiModelProperty(value = "门店id")
    private Long storeId;

    @ApiModelProperty(value = "设备号")
    private String imei;

    @ApiModelProperty(value = "设备状态:0=正常,1=故障")
    private String status;

    @ApiModelProperty(value = "设备类型")
    private String type;

    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "到期时间")
    private Date expireTime;
    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "设备启动状态:0=空闲,1=运行中")
    private String activateStatus;

    @ApiModelProperty(value = "在线状态:0=离线,1=在线")
    private String onlineStatus;
    @ApiModelProperty(value = "激活状态:0=未激活,1=已激活")
    private String useStatus;
    @ApiModelProperty(value = "禁用状态：未禁用、已禁用")
    private String prohibitStatus;

    @ApiModelProperty(value = "设备编号")
    private String equipmentSn;



}
