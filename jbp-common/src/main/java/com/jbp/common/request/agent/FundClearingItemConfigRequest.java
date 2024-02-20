package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingItemConfigRequest对象", description = "佣金发放设置")
public class FundClearingItemConfigRequest implements Serializable {

    @ApiModelProperty("佣金名称")
    @NotEmpty(message = "佣金名称不能为空")
    private String commName;

    @ApiModelProperty("出款名称")
    @NotEmpty(message = "出款名称不能为空")
    private String name;

    @ApiModelProperty("出款比例")
    @NotNull(message = "出款比例不能为空")
    private BigDecimal scale;

    @ApiModelProperty("钱包类型")
    @NotNull(message = "钱包类型不能为空")
    private Integer walletType;
}
