package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 从下往上查询所有的用户【销售关系 服务关系公用】
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@ApiModel(value="UserUpperDto对象", description="根据关系网查询所有上级对象")
public class UserUpperDto implements Serializable {

    @ApiModelProperty("自己ID")
    private Integer uId;

    @ApiModelProperty("上级ID")
    private Integer pId;

    @ApiModelProperty("查询人查到顶级下面第一层的人的点位关系")
    private Integer node;

    @ApiModelProperty("查询人到上级的层级")
    private Integer level;
}
