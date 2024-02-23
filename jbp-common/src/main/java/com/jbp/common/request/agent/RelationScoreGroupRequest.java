package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreGroupRequest对象", description = "服务业绩明细请求对象")
public class RelationScoreGroupRequest implements Serializable {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("分组名称")
    private String groupName;
}
