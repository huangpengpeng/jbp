package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户区域【常驻地址】
 */
@Data
@Builder
@TableName("eb_user_company")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "UserCompany对象", description = "分公司")
public class UserCompany extends BaseModel {

    private static final long serialVersionUID = 745806610282093367L;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("公司名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty("执照编号")
    @TableField("license_no")
    private String licenseNo;

    @ApiModelProperty("执照图片")
    @TableField("license_url")
    private String licenseUrl;

    @ApiModelProperty("省份")
    @TableField("province")
    private String province;

    @ApiModelProperty("城市")
    @TableField("city")
    private String city;

    @ApiModelProperty("区域")
    @TableField("area")
    private String area;

    @ApiModelProperty("详细地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("状态  申请中 已开通 已取消")
    @TableField("status")
    private String status;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

    public static enum Constants {
        已开通
    }
}
