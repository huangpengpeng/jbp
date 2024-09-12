package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryPrizeFrontRequest对象", description = "抽奖商品前端请求对象")
public class LotteryPrizeFrontRequest implements Serializable {

    @ApiModelProperty("奖品类型， -1-谢谢参与、2-普通奖品")
    private Integer prizeType;
}
