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
@TableName("eb_user_white")
@NoArgsConstructor
@AllArgsConstructor
public class UserWhite extends BaseModel {


    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("白名单ID")
    @TableField("white_id")
    private Long whiteId;

    @ApiModelProperty("单号")
    @TableField("orders_sn")
    private String ordersSn;


    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String accountNo;

    @ApiModelProperty("白名单名称")
    @TableField(exist = false)
    private String whiteName;
}
