package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletWithdrawCancelRequest对象", description = "提现取消请求对象")
public class WalletWithdrawCancelRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("提现单号")
    private List<String> uniqueNos;

    @ApiModelProperty("出款备注")
    private String remark;
}
