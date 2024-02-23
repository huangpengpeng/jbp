package com.jbp.common.model.agent;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户信息
 */
@Data
@NoArgsConstructor
public class UserInfo {

    public UserInfo(String username, String account) {
        this.username = username;
        this.account = account;
    }

    @ApiModelProperty("会员名")
    private String username;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("等级")
    private Long capaId;

    @ApiModelProperty("等级")
    private String capaName;

    @ApiModelProperty("星级")
    private Long capaXsId;

    @ApiModelProperty("星级")
    private String capaXsName;
}
