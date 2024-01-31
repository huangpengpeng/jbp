package com.jbp.common.model.agent;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RiseCondition对象", description="晋升条件")
public class RiseCondition extends BaseModel {

    public RiseCondition(String name, String description, String value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @ApiModelProperty("名称唯一")
    @TableField("name")
    private String name;

    @ApiModelProperty("条件描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("条件值")
    private String value;
}
