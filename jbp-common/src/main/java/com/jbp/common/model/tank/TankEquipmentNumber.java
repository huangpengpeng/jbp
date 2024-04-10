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
@TableName("eb_tank_equipment_number")
@ApiModel(value="TankEquipmentNumber对象", description="共享仓店主次数表")
public class TankEquipmentNumber extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;

    @ApiModelProperty(value = "店主id")
    private Long storeUserId;

    @ApiModelProperty(value = "次数")
    private Integer number;

    @ApiModelProperty(value = "到期时间")
    private Date expireTime;



}
