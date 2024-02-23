package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaSnapshotRequest对象", description = "用户等级快照请求对象")
public class UserCapaSnapshotRequest implements Serializable {

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("账户名称")
    private String account;

    @ApiModelProperty("等级ID")
    private Long capaId;
}
