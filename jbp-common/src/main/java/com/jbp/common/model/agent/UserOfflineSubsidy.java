package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 用户线下补助
 */
@Data
@Builder
@TableName(value="eb_user_offline_subsidy",autoResultMap = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserOfflineSubsidy对象", description="用户线下补助")
public class UserOfflineSubsidy extends BaseModel {
    private static final long serialVersionUID = 745806610282093367L;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("省份")
    @TableField("province")
    private String province;

    @ApiModelProperty("城市")
    @TableField("city")
    private String city;

    @ApiModelProperty("区域")
    @TableField("area")
    private String area;

    @ApiModelProperty("状态  申请中 已开通 已取消")
    @TableField("status")
    private String status;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市id")
    private Integer cityId;

    @ApiModelProperty(value = "区/县id")
    private Integer areaId;

    public static enum Constants {
        申请中, 已开通, 已取消
    }
}
