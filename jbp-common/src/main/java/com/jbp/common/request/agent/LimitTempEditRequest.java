package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LimitTempEditRequest对象", description = "限制模版编辑对象")
public class LimitTempEditRequest {
    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "模版名称")
    private String name;

    @ApiModelProperty(value = "商品显示  商品购买  装修显示")
    private String type;

    @ApiModelProperty(value = "自己等级ID")
    private List<Long> capaIdList;

    @ApiModelProperty(value = "自己星级ID")
    private List<Long> capaXsIdList;

    @ApiModelProperty(value = "白名单ID")
    private List<Long> whiteIdList;

    @ApiModelProperty(value = "团队ID")
    private List<Long> teamIdList;

    @ApiModelProperty(value = "要求必须有上级")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    private List<Long> pCapaIdList;

    @ApiModelProperty(value = "上级星级")
    private List<Long> pCapaXsIdList;

    @ApiModelProperty(value = "要求必须有服务上级")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "服务上级等级")
    private List<Long> rCapaIdList;

    @ApiModelProperty(value = "服务上级星级")
    private List<Long> rCapaXsIdList;

    @ApiModelProperty(value = "说明")
    private String  description;
}