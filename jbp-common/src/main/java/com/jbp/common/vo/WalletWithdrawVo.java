package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletWithdrawVo对象", description = "微信模板发送类")
public class WalletWithdrawVo {

    @ApiModelProperty
    private Integer id;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("提现金额")
    private BigDecimal amt;

    @ApiModelProperty("手续费")
    private BigDecimal commission;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("成功时间")
    private Date successTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("银行卡名称")
    private String bankName;

    @ApiModelProperty("银行卡号")
    private String bankCode;

    @ApiModelProperty("真实姓名")
    private String realName;
    @ApiModelProperty("用户昵称")
    private String nickName;
}
