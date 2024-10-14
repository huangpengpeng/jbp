package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_msg_code", autoResultMap = true)
@ApiModel(value="MsgCode对象", description="手机号验证码")
@NoArgsConstructor
@AllArgsConstructor
public class MsgCode extends BaseModel {

    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("消息")
    @TableField("msg")
    private String msg;

    @ApiModelProperty("状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("过期时间")
    @TableField("expired_time")
    private Date expiredTime;

}
