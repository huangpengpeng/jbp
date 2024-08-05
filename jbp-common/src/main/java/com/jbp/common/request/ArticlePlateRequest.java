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
@ApiModel(value = "ArticlePlateRequest对象", description = "文章板块请求对象")
public class ArticlePlateRequest implements Serializable {

    @ApiModelProperty("名称")
    private String name;
}
