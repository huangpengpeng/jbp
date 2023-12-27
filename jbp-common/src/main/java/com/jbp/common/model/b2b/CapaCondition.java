package com.jbp.common.model.b2b;

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
@TableName(value = "b2b_capa_condition", autoResultMap = true)
@ApiModel(value="CapaCondition对象", description="平台等级升级条件")
public class CapaCondition extends BaseModel {

    public CapaCondition(Long capaId, String name, JSONObject value) {
        this.capaId = capaId;
        this.name = name;
        this.value = value;
    }

    @ApiModelProperty("等级编号")
    @TableField("capaId")
    private Long capaId;

    @ApiModelProperty("条件名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("条件值")
    @TableField(value = "value", typeHandler = JacksonTypeHandler.class)
    private JSONObject value;
}
