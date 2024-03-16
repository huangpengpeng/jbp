package com.jbp.common.model.agent;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
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
