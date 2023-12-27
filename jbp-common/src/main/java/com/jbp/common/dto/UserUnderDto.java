package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 从下往上查询所有的用户【销售关系 服务关系公用】
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserUnder对象", description="更具关系网查询所有的下级")
public class UserUnderDto implements Serializable {

    @ApiModelProperty("自己ID")
    private Integer uId;

    @ApiModelProperty("下级ID")
    private Integer childId;

    @ApiModelProperty("下级在自己的位置关系")
    private Integer node;

    @ApiModelProperty("距离自己的层级")
    private Integer level;
}
