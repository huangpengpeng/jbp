package com.jbp.common.response;

import com.jbp.common.model.agent.ChannelCard;
import com.jbp.common.model.agent.OrdersFundSummary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ChannelCardExtResponse对象", description="银行卡")
public class ChannelCardExtResponse extends ChannelCard {
    @ApiModelProperty(value = "团队")
    private String name;

}
