package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletWithdrawRequest对象", description = "提现请求对象")
public class WalletWithdrawRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("提现单号")
    private String uniqueNo;

    @ApiModelProperty("出款备注")
    private String remark;
}
