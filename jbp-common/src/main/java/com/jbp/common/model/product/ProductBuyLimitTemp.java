package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
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
@TableName(value = "eb_product_buy_limit_temp", autoResultMap = true)
@ApiModel(value="ProductBuyLimitTemp对象", description="商品购买限制模版")
public class ProductBuyLimitTemp extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模版名称")
    private String name;

    @ApiModelProperty(value = "自己等级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> capaIdList;

    @ApiModelProperty(value = "自己星级ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> capaXsIdList;

    @ApiModelProperty(value = "白名单ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> whiteIdList;

    @ApiModelProperty(value = "团队ID")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> teamIdList;

    @ApiModelProperty(value = "要求必须有上级")
    private Boolean  hasPartner;

    @ApiModelProperty(value = "上级等级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> pCapaIdList;

    @ApiModelProperty(value = "要求必须有服务上级")
    private Boolean  hasRelation;

    @ApiModelProperty(value = "上级星级")
    @TableField(typeHandler = LongListHandler.class)
    private List<Long> pCapaXsIdList;
}
