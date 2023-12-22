package com.jbp.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInvitationDto implements Serializable {

    @ApiModelProperty("关系ID")
    private Long id;

    @ApiModelProperty("自己账户")
    private String accountNo;

    @ApiModelProperty("转挂账户")
    private String mAccountNo;

    @ApiModelProperty("上级账户")
    private String pAccountNo;

    @ApiModelProperty("强弱绑定")
    private Boolean ifForce;

    @ApiModelProperty("创建时间")
    private Date gmtCreated;

    @ApiModelProperty("修改时间")
    private Date gmtModify;

}
