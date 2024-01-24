package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 购买商品收益
 * type
 * 1 等级
 * 2.星级
 * 3.白名单
 * 4.积分
 * 5.课程
 * 6.门票
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_profit_config", autoResultMap = true)
@ApiModel(value="ProductProfitConfig对象", description="购买商品收益配置")
public class ProductProfitConfig extends BaseModel {

    private static final long serialVersionUID = 1L;
    public ProductProfitConfig(Integer type, String name, String description) {
        this.type = type;
        this.name = name;
        this.ifOpen = false;
        this.description = description;
    }

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private Boolean ifOpen;

    @ApiModelProperty(value = "说明")
    private String description;
}
