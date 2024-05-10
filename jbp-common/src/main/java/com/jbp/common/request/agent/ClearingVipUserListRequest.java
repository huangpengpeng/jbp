package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ClearingVipUserListRequest对象", description="结算vip用户列表请求对象")

public class ClearingVipUserListRequest implements Serializable {

    @ApiModelProperty("账户")
    private String accountNo;

    @ApiModelProperty("佣金类型")
    private Integer commType;

    @ApiModelProperty("级别")
    private Long level;

    @ApiModelProperty("级别名称")
    private String levelName;

    @ApiModelProperty("状态 0有效 -1失效")
    private Integer status;




}
