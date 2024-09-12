package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryRequest对象", description = "抽奖活动新增编辑对象")
public class LotteryItemPrizeRequest implements Serializable {


    @ApiModelProperty("奖品类型,1-谢谢参与、2-普通商品")
    private Integer prizeType;

    @ApiModelProperty("奖品名称")
    private String prizeName;

    @ApiModelProperty("奖品图片")
    private String images;

    @ApiModelProperty("总库存")
    private  Integer totalStock;

    @ApiModelProperty("奖项概率：0-1")
    private BigDecimal percent;

    @ApiModelProperty("备注-提示语")
    private String remark;

    @ApiModelProperty("奖品id")
    private Integer prizeId;

    @ApiModelProperty("奖品概率id")
    private Integer itemId;

    @ApiModelProperty("默认奖项")
    private Integer defaultItem;


}
