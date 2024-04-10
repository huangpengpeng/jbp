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
@TableName("eb_tank_equipment_number_info")
@ApiModel(value="TankEquipmentNumberInfo对象", description="共享仓店主次数明细表")
public class TankEquipmentNumberInfo extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;

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


}
