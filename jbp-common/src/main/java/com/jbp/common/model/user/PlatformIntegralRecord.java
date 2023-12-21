package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.utils.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_platform_integral_record")
@ApiModel(value="PlatformIntegralRecord对象", description="平台积分明细")
public class PlatformIntegralRecord implements Serializable {

    private static final long serialVersionUID=1L;

    public PlatformIntegralRecord(String integralType, String externalNo,
                                  Integer type, String title, BigDecimal integral, BigDecimal balance, String postscript) {
        this.integralType = integralType;
        this.externalNo = externalNo;
        this.type = type;
        this.title = title;
        this.integral = integral;
        this.balance = balance;
        this.postscript = postscript;
        this.createTime = DateTimeUtils.getNow();
        this.updateTime = createTime;
    }

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "积分类型  配置表单的积分类型")
    private String integralType;

    @ApiModelProperty(value = "外部单号")
    private String externalNo;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "剩余")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "附言")
    private String postscript;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
