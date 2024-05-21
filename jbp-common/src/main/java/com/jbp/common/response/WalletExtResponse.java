package com.jbp.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.model.agent.WalletFlow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WalletExtResponse对象", description="用户钱包")
public class WalletExtResponse  {

    @ApiModelProperty("用户ID")
    private Integer uId;

    @ApiModelProperty("钱包类型")
    private Integer type;

    @ApiModelProperty("余额")
    private BigDecimal balance;

    @ApiModelProperty("冻结金额")
    private BigDecimal freeze;

    @ApiModelProperty("账户")
    private String account;
    @ApiModelProperty("钱包名称")
    private String typeName;

    @ApiModelProperty(value = "团队")
    private String name;

}
