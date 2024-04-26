package com.jbp.common.response;

import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.CapaXs;
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
@ApiModel(value = "RelationScoreResponse对象", description = "用户左右区业绩响应对象")
public class RelationScoreResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "一区总业绩")
    private BigDecimal totalScore = BigDecimal.ZERO;
    @ApiModelProperty(value = "一区业绩")
    private BigDecimal usableScore  = BigDecimal.ZERO;

    @ApiModelProperty(value = "二区总业绩")
    private BigDecimal totalScore2  = BigDecimal.ZERO;
    @ApiModelProperty(value = "二区业绩")
    private BigDecimal usableScore2   = BigDecimal.ZERO;


    @ApiModelProperty(value = "一区用户名称")
    private String nickname;
    @ApiModelProperty(value = "一区账号")
    private String account;
    @ApiModelProperty(value = "二区用户名称")
    private String nickname2;
    @ApiModelProperty(value = "二区账号")
    private String account2;

    @ApiModelProperty(value = "一区头像")
    private String userUrl;
    @ApiModelProperty(value = "一区等级图标")
    private String capaImg;

    @ApiModelProperty(value = "二区头像")
    private String userUrl2;
    @ApiModelProperty(value = "二区等级图标")
    private String capaImg2;
}
