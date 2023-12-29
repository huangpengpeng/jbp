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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wallet")
@ApiModel(value = "Wallet对象", description = "用户钱包")
public class Wallet extends VersionModel {



    public boolean hasError() {
        if (ArithmeticUtils.less(balance, BigDecimal.ZERO)) {
            return true;
        }
        return false;
    }

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Long uId;

    @ApiModelProperty("钱包类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("余额")
    @TableField("balance")
    private BigDecimal balance;

    @ApiModelProperty("冻结金额")
    @TableField("freeze")
    private BigDecimal freeze;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String accountNo;

}
