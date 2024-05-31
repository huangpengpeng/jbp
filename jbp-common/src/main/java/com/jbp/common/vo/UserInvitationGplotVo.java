package com.jbp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserInvitationGplotVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户账户")
    private String uAccount;

    @ApiModelProperty("用户昵称")
    private String uNickName;

    @ApiModelProperty("下级总人数")
    private Integer count;

    @ApiModelProperty("是否为父级")
    private Boolean isParent;

    @ApiModelProperty("星级名称")
    private String capaXsName;

    @ApiModelProperty("等级名称")
    private String capaName;

    @ApiModelProperty("星级id")
    private Long ucapaXsId;

    @ApiModelProperty("等级id")
    private Long ucapaId;

    @ApiModelProperty("子集")
    private List<UserInvitationGplotVo> children;

}
