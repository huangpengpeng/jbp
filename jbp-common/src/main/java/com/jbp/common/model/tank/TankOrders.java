package com.jbp.common.model.tank;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_tank_orders")
@ApiModel(value="TankOrders对象", description="共享仓订单")
public class TankOrders extends BaseModel {

    private static final long serialVersionUID = -7977840875014775897L;


    @ApiModelProperty(value = "付款人Id")
    private Long userId;

    @ApiModelProperty(value = "店主id")
    private Long storeUserId;

    @ApiModelProperty(value = "充值订单号")
    private String orderSn;

    @ApiModelProperty(value = "充值状态(已支付，未支付)")
    private String status;

    @ApiModelProperty(value = "次数")
    private Integer number;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;


}
