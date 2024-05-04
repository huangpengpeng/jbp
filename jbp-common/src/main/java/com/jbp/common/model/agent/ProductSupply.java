package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;
import java.io.Serializable;

/**
 * 供应商表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_supply", autoResultMap = true)
@ApiModel(value="ProductSupply对象", description="供应商")
public class ProductSupply  extends BaseModel {

    private static final long serialVersionUID = -3068573610140753926L;

    @ApiModelProperty("供应商名称")
    @TableField("name")
    private String name;

}

