package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RiseCondition对象", description="晋升条件")
public class RiseCondition implements Serializable {

    public RiseCondition(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @ApiModelProperty("名称唯一")
    @TableField("name")
    private String name;

    @ApiModelProperty("条件值")
    private String value;
}
