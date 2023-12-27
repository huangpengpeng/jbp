package com.jbp.common.model.b2b;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.utils.ArithmeticUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@TableName("Wallet")
@NoArgsConstructor
public class Wallet extends BaseModel {

    public Wallet(Long userId, String type) {
        this.userId = userId;
        this.type = type;
        this.balance = BigDecimal.ZERO;
        this.freeze = BigDecimal.ZERO;
    }

    public boolean hasError() {
        if (ArithmeticUtils.less(balance, BigDecimal.ZERO)) {
            return true;
        }
        return false;
    }

    @ApiModelProperty("用户ID")
    @TableField("userId")
    private Long userId;

    @ApiModelProperty("钱包类型")
    @TableField("type")
    private String type;

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
