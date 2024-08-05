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
public class ArticleCategoryListRequest implements Serializable {

    @ApiModelProperty("文章模板id")
    private Long plateId;
}
