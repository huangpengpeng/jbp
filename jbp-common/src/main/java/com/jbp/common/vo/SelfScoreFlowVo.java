package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.mybatis.ProductInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SelfScoreFlowVo对象", description = "个人业绩明细VO对象")
public class SelfScoreFlowVo {

    @ApiModelProperty("积分")
    private BigDecimal score;

    @ApiModelProperty("方向  增加|减少")
    private String action;

    @ApiModelProperty("操作  下单|人工")
    private String operate;

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("账户")
    private String account;

}
