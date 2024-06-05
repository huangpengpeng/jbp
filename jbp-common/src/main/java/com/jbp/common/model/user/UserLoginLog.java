package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_login_log")
@ApiModel(value="UserLoginLog对象", description="用登录日志")
public class UserLoginLog extends BaseModel {

    public UserLoginLog(Integer uid, String account, String ip, String type) {
        this.uid = uid;
        this.account = account;
        this.ip = ip;
        this.type = type;
    }

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "最后一次登录ip")
    private String ip;

    @ApiModelProperty(value = "登录方式")
    private String type;
}
