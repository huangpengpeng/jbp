package com.jbp.common.model.article;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_article_plate")
@ApiModel(value="ArticlePlate对象", description="文章板块管理表")
public class ArticlePlate extends BaseModel {

    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

}
