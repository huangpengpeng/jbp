package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserInvitationJumpListResponse对象", description="销售跳转关系对象")
public class UserInvitationJumpListResponse implements Serializable {

    @ApiModelProperty("用户id")
    private Integer uId;

    @ApiModelProperty("当前上级id")
    private Integer pId;

    @ApiModelProperty("用户账户")
    private String uaccount;

    @ApiModelProperty("当前上级账户")
    private String paccount;

    @ApiModelProperty("原来上级id")
    private Integer orgPid;

    @ApiModelProperty("原来上级账户")
    private String oaccount;

    @ApiModelProperty("创建时间")
    private Date gmtCreated;




}
