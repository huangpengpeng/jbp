package com.jbp.common.request.agent;

import com.jbp.common.utils.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreUpdateRequest对象", description = "服务业绩汇总更新对象")
public class RelationScoreUpdateRequest implements Serializable {

    @NotBlank(message = "账户不能为空")
    @ApiModelProperty("用户账号")
    private String account;

    @NotNull(message = "积分不能为空")
    @ApiModelProperty("业绩")
    private BigDecimal score;

    @NotNull(message = "位置不能为空")
    @ApiModelProperty("位置")
    private int node;

    @NotBlank(message = "单据不能为空")
    @ApiModelProperty("单据")
    private String ordersSn;

    @NotNull(message = "调整之间不能为空")
    @DateTimeFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN)
    @ApiModelProperty("调整时间")
    private Date payTime;

    @NotBlank(message = "备注信息不能为空")
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否调整已用, 减少可用的时候需要录入 默认false")
    private Boolean ifUpdateUsed;
}
