package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
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
@TableName("eb_order_fill")
@ApiModel(value = "OrderFill对象", description = "订单补单表")
public class OrderFill extends BaseModel {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "过期时间")
    private Date expiredTime;

    @ApiModelProperty(value = "补单时间")
    private Date fillTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "补单用户")
    private Integer uId;

    @ApiModelProperty(value = "拒补时间")
    private Date noFillTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
