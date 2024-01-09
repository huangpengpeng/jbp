package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaXsSnapshotRequest对象", description = "用户星级快照请求对象")
public class UserCapaXsSnapshotRequest {
    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("账户名称")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("等级ID")
    private Long capaId;
}
