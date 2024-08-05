package com.jbp.common.response;

import com.jbp.common.model.agent.WalletFlow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WalletFlowExtResponse对象", description="用户明细")
public class WalletFlowExtResponse extends WalletFlow {

    @ApiModelProperty(value = "团队")
    private String name;

    @ApiModelProperty("用户昵称")
    private String nickname;

}
