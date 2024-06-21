package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LimitTempEditRequest对象", description = "限制模版编辑对象")
public class LimitTempEditRequest implements Serializable {
    @ApiModelProperty(value = "编号")
    private Long id;

    @NotBlank(message = "模版名称不能为空")
    @ApiModelProperty(value = "模版名称")
    private String name;

    @NotBlank(message = "类型不能为空")
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

    @NotNull(message = "要求必须有上级必填")
    @ApiModelProperty(value = "要求必须有上级")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    private List<Long> pCapaIdList;

    @ApiModelProperty(value = "上级星级")
    private List<Long> pCapaXsIdList;

    @NotNull(message = "要求必须有服务上级必填")
    @ApiModelProperty(value = "要求必须有服务上级")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "服务上级等级")
    private List<Long> rCapaIdList;

    @ApiModelProperty(value = "服务上级星级")
    private List<Long> rCapaXsIdList;

    @NotNull(message = "限购选择是或否必填")
    @ApiModelProperty(value = "是否限购")
    private Boolean hasBuyLimit;

    @ApiModelProperty(value = "限购数量")
    private Integer buyLimitNum;

    @ApiModelProperty(value = "限购开始时间")
    private Date buyLimitStartTime;

    @ApiModelProperty(value = "限购结束时间")
    private Date buyLimitEndTime;

    @ApiModelProperty(value = "单次购买限购数")
    private Integer oneTimeNum;

    @ApiModelProperty(value = "说明")
    private String  description;

    @ApiModelProperty(value = "单次购买最小数量")
    private Integer oneTimeNumMin;
}
