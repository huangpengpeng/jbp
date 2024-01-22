package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.jbp.common.model.BaseModel;
import com.jbp.common.model.VersionModel;
import com.jbp.common.utils.ArithmeticUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_platform_wallet")
@NoArgsConstructor
@ApiModel(value="PlatformWallet对象", description="平台钱包")
public class PlatformWallet extends VersionModel {

    public boolean hasError() {
        if (ArithmeticUtils.less(balance, BigDecimal.ZERO)) {
            return true;
        }
        return false;
    }

    public PlatformWallet(Integer type) {
        this.type = type;
        this.balance = BigDecimal.ZERO;
    }

    @ApiModelProperty("钱包类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("余额")
    @TableField("balance")
    private BigDecimal balance;
    @ApiModelProperty("钱包名称")
    @TableField(exist = false)
    private String typeName;

}
