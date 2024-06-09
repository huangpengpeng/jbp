package com.jbp.common.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HistoryOrderRequest对象", description = "历史订单请求对象")
public class HistoryOrderRequest implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("付款开始时间")
    private Date startPayTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("付款结束时间")
    private Date endPayTime;
}
