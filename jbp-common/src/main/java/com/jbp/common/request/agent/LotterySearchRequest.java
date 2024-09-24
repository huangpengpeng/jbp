package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotterySearchRequest对象", description = "抽奖活动查询对象")
public class LotterySearchRequest implements Serializable {
}
