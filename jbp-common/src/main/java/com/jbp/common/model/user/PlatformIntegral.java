package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.*;
import com.jbp.common.utils.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_platform_integral")
@ApiModel(value="PlatformIntegral对象", description="平台积分")
public class PlatformIntegral implements Serializable {

    private static final long serialVersionUID=1L;

    public PlatformIntegral(String type) {
        this.type = type;
        this.integral = BigDecimal.ZERO;
        this.createTime = DateTimeUtils.getNow();
        this.updateTime = createTime;
    }

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类型  奖励  换购  消费  购物")
    private String type;

    @ApiModelProperty(value = "积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version = 1;
}
