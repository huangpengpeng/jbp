package com.jbp.common.model.express;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserWhiteExpress implements Serializable {
    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("白名单名称")
    private String whiteName;
}
