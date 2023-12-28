package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.utils.ArithmeticUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_platform_wallet")
@ApiModel(value="PlatformWallet对象", description="平台钱包")
public class PlatformWallet extends BaseModel {

    public boolean hasError() {
        if (ArithmeticUtils.less(balance, BigDecimal.ZERO)) {
            return true;
        }
        return false;
    }

    @ApiModelProperty("钱包类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("余额")
    @TableField("balance")
    private BigDecimal balance;
}
