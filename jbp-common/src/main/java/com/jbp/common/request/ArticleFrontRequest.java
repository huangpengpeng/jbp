package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ArticleFrontRequest对象", description = "前端文章请求对象")
public class ArticleFrontRequest implements Serializable {

    @ApiModelProperty("文章板块id")
    private Long plateId;

    @ApiModelProperty("分类id")
    private Integer cid;

    @ApiModelProperty("文章展示限制数量")
    private Integer count;

    @ApiModelProperty("文章展示数量是否限制")
    private Boolean ifLimit;

}
