package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_comm_config", autoResultMap = true)
@ApiModel(value="ProductCommConfig对象", description="产品佣金配置")
public class ProductCommConfig extends BaseModel {

    public ProductCommConfig(Integer type, String name, Boolean ifWhole, String desc) {
        this.type = type;
        this.name = name;
        this.ifOpen = false;
        this.ifWhole = ifWhole;
        this.description = desc;
    }

    @ApiModelProperty(value = "佣金类型")
    private Integer type;

    @ApiModelProperty(value = "佣金名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private Boolean ifOpen;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "全局配置 非全局是配置在指定产品上的分钱比例")
    private Boolean ifWhole;

    @ApiModelProperty("比例json")
    private String ratioJson;



    @ApiModelProperty("系数")
    @TableField(exist = false)
    private BigDecimal scale;


    @ApiModelProperty(value = "商品是否开启")
    @TableField(exist = false)
    private Boolean status;

    @ApiModelProperty(value = "规则")
    @TableField(exist = false)
    private String rule;
}