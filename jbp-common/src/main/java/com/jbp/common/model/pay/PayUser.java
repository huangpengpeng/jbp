package com.jbp.common.model.pay;

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
@TableName(value = "pay_user", autoResultMap = true)
@ApiModel(value = "payUser对象", description = "收款用户")
public class PayUser extends BaseModel {

    @ApiModelProperty(value = "后台商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户KEY")
    private String appKey;

    @ApiModelProperty(value = "用户SECRET")
    private String appSecret;
}
