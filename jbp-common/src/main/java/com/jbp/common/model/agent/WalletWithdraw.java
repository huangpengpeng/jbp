package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.VersionModel;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@TableName("eb_wallet_withdraw")
@ApiModel(value = "WalletWithdraw对象", description = "钱包提现")
public class WalletWithdraw extends VersionModel {

    private static final long serialVersionUID = 1L;

    public WalletWithdraw(Integer uid, String account, Integer walletType, String walletName, BigDecimal amt, BigDecimal commission, String postscript) {
        this.uid = uid;
        this.account = account;
        this.walletType = walletType;
        this.walletName = walletName;
        this.uniqueNo = StringUtils.N_TO_10("W_");
        this.amt = amt;
        this.commission = commission;
        this.status = StatusEnum.待出款.toString();
        this.postscript = postscript;
        this.createTime = DateTimeUtils.getNow();
    }

    public static enum StatusEnum {
        待出款, 已出款, 已取消
    }

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("钱包类型")
    @TableField("wallet_type")
    private Integer walletType;

    @ApiModelProperty("钱包名称")
    @TableField("wallet_name")
    private String walletName;

    @ApiModelProperty("流水单号")
    @TableField("unique_no")
    private String uniqueNo;

    @ApiModelProperty("提现金额")
    @TableField("amt")
    private BigDecimal amt;

    @ApiModelProperty("手续费")
    @TableField("commission")
    private BigDecimal commission;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("附言")
    @TableField("postscript")
    private String postscript;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("成功时间")
    @TableField("success_time")
    private Date successTime;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("银行卡名称")
    @TableField(exist = false)
    private String bankName;

    @ApiModelProperty("银行卡号")
    @TableField(exist = false)
    private String bankCode;

    @ApiModelProperty("真实姓名")
    @TableField(exist = false)
    private String realName;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickName;

    @ApiModelProperty("手机号")
    @TableField(exist = false)
    private String phone;

    @ApiModelProperty("身份证")
    @TableField(exist = false)
    private String idCardNo;
}