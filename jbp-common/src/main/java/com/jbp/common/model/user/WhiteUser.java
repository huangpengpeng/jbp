package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("eb_white_user")
@NoArgsConstructor
@AllArgsConstructor
public class WhiteUser extends BaseModel {

    @ApiModelProperty("用户ID")
    private Integer uid;

    @ApiModelProperty("白名单ID")
    @TableField("white_id")
    private Long whiteId;

    @ApiModelProperty("单号")
    @TableField("orders_sn")
    private String ordersSn;

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("白名单名称")
    @TableField(exist = false)
    private String whiteName;

    @ApiModelProperty("等级名称")
    @TableField(exist = false)
    private String CapaName;

    @ApiModelProperty("星级名称")
    @TableField(exist = false)
    private String CapaXsName;

    @ApiModelProperty("团队名称")
    @TableField(exist = false)
    private String teamName;
}
