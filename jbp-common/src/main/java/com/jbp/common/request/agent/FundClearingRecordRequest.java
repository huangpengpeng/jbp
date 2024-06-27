package com.jbp.common.request.agent;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingRecordRequest对象", description = "佣金统计请求对象")
public class FundClearingRecordRequest {

    @ApiModelProperty("下单账号/昵称")
    private String orderAccount;

    @ApiModelProperty("获得者账号/昵称")
    private String account;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("开始创建时间")
    private Date startCreateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("结束创建时间")
    private Date endCreateTime;

    @ApiModelProperty("单号列表")
    private List<String> orderList;
}
