package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.VersionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_orders_success_msg", autoResultMap = true)
@ApiModel(value="OrderSuccessMsg对象", description="订单支付成功消息")
@NoArgsConstructor
public class OrderSuccessMsg extends VersionModel {

    @ApiModelProperty("订单编号")
    @TableField("orders_sn")
    private String ordersSn;

    @ApiModelProperty("是否执行")
    @TableField("exec")
    private Boolean exec;
}
