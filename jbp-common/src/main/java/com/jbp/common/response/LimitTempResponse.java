package com.jbp.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.user.White;
import com.jbp.common.mybatis.LongListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LimitTempResponse对象", description = "限制模版响应对象")
public class LimitTempResponse {
    @ApiModelProperty(value = "模版名称")
    private String name;

    @ApiModelProperty(value = "商品显示  商品购买  装修显示")
    private String type;

    @ApiModelProperty(value = "自己等级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Capa> capaIdList;

    @ApiModelProperty(value = "自己星级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<CapaXs> capaXsIdList;

    @ApiModelProperty(value = "白名单ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<White> whiteIdList;

    @ApiModelProperty(value = "团队ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Team> teamIdList;

    @ApiModelProperty(value = "要求必须有上级")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Capa> pCapaIdList;

    @ApiModelProperty(value = "上级星级")
    @TableField(typeHandler = LongListHandler.class)
    private List<CapaXs> pCapaXsIdList;

    @ApiModelProperty(value = "要求必须有服务上级")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "服务上级等级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Capa> rCapaIdList;

    @ApiModelProperty(value = "服务上级星级")
    @TableField(typeHandler = LongListHandler.class)
    private List<CapaXs> rCapaXsIdList;

    @ApiModelProperty(value = "说明")
    private String  description;
}
