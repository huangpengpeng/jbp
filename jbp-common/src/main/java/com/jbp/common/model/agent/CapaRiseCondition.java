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
@TableName(value = "eb_capa_rise_condition", autoResultMap = true)
@ApiModel(value="CapaRiseCondition对象", description="晋升条件")
public class CapaRiseCondition extends BaseModel {

    public CapaRiseCondition(Integer type, String name, String description, JSONObject value) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @ApiModelProperty("等级类型  0 等级  1 星级")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("名称唯一")
    @TableField("name")
    private String name;

    @ApiModelProperty("条件描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("条件值")
    @TableField(value = "value", typeHandler = JacksonTypeHandler.class)
    private JSONObject value;
}
