package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.OrderRegisterHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_ext", autoResultMap = true)
@ApiModel(value="OrderExt对象", description="订单扩展信息")
public class OrderExt extends BaseModel {

    @ApiModelProperty(value = "订单ID")
    private Integer orderId;

    @ApiModelProperty(value = "付款用户")
    private Integer payUid;

    @ApiModelProperty(value = "分享用户")
    private Integer shardUid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "下单前等级")
    private Long capaId;

    @ApiModelProperty(value = "=下单前星级")
    private Long capaXsId;

    @ApiModelProperty(value = "成功后等级")
    private Long successCapaId;

    @ApiModelProperty(value = "成功后星级")
    private Long successCapaXsId;

    @ApiModelProperty(value = "下单注册账号注册信息")
    @TableField(value = "order_register", typeHandler = OrderRegisterHandler.class)
    private OrderRegister orderRegister;
}