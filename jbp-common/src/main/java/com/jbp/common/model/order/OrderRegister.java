package com.jbp.common.model.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 下单注册用户信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderRegister implements Serializable {

    public OrderRegister(String username, String mobile, String pAccount, String rAccount, Integer node, Long capaId, Long capaXsId) {
        this.username = username;
        this.mobile = mobile;
        this.pAccount = pAccount;
        this.rAccount = rAccount;
        this.node = node;
        this.capaId = capaId;
        this.capaXsId = capaXsId;
    }

    @ApiModelProperty(value = "会员名")
    private String username;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "销售上级")
    private String pAccount;

    @ApiModelProperty(value = "安置上级")
    private String rAccount;

    @ApiModelProperty(value = "安置点位")
    private Integer node;

    @ApiModelProperty(value = "等级编号")
    private Long capaId;

    @ApiModelProperty(value = "星级编号")
    private Long capaXsId;

}
