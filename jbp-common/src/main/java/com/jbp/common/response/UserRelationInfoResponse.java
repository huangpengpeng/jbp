package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserRelationInfoResponse对象", description = "用户服务关系对象")
@NoArgsConstructor
public class UserRelationInfoResponse implements Serializable {

    public UserRelationInfoResponse(Integer rId, String rAccount, Integer node) {
        this.rId = rId;
        this.rAccount = rAccount;
        this.node = node;
    }

    @ApiModelProperty(value = "服务上级ID")
    private Integer rId;

    @ApiModelProperty(value = "服务上级账号")
    private String rAccount;

    @ApiModelProperty(value = "节点")
    private Integer node;
}
