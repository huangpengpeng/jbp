package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LztReviewDto对象", description="来账通复核详情对象")
public class LztReviewDetailDto implements Serializable {

    private static final long serialVersionUID = 801239086144633763L;

    @ApiModelProperty("编号")
    private Long id;

    @ApiModelProperty("类型【代付】【提现】")
    private String type;

    @ApiModelProperty("业务单号")
    private String txnSeqno;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("账户编号")
    private String userId;

    @ApiModelProperty("账户名称")
    private String username;

    @ApiModelProperty("金额")
    private String amt;

    @ApiModelProperty("状态")
    private String status;



}
