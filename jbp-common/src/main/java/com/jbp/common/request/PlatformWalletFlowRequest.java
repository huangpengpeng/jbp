package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatformWalletFlowRequest对象", description = "平台积分详情请求对象")
public class PlatformWalletFlowRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("钱包类型")
    private Integer type;
    @ApiModelProperty("资金去向")
    private String action;
    @ApiModelProperty("操作")
    private String operate;
    @ApiModelProperty("流水单号")
    private String uniqueNo;
    @ApiModelProperty("附言")
    private String postscript;
    @ApiModelProperty("用户ID")
    private Long uid;
    @ApiModelProperty("转入资金")
    private BigDecimal transferIntegral;
}
