package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletFlowListRequest对象", description = "用户积分详情请求对象")
public class WalletFlowListRequest extends PageParamRequest implements Serializable {

    @ApiModelProperty("钱包类型")
    private Integer type;
    @ApiModelProperty("收入，支出")
    private String  action;
}
