package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IntegralRecordVo对象", description="用户积分明细")
public class IntegralRecordVo {

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer uid;

    @ApiModelProperty(value = "账户", required = true)
    private String account;

    @ApiModelProperty(value = "积分类型", required = true)
    private String integralType;

    @ApiModelProperty(value = "外部单号", required = true)
    private String externalNo;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "积分", required = true)
    private BigDecimal integral;

    @ApiModelProperty(value = "剩余", required = true)
    private BigDecimal balance;

    @ApiModelProperty(value = "附言", required = true)
    private String postscript;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
