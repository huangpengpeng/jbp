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
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_fund_clearing_item_config", autoResultMap = true)
@ApiModel(value="FundClearingItemConfig对象", description="佣金发放配置")
public class FundClearingItemConfig extends BaseModel {

    public FundClearingItemConfig(String commName, String name, BigDecimal scale, Integer walletType) {
        this.commName = commName;
        this.name = name;
        this.scale = scale;
        this.walletType = walletType;
    }

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("出款名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("出款比例")
    @TableField("scale")
    private BigDecimal scale;

    @ApiModelProperty("钱包类型")
    private Integer walletType;
}
