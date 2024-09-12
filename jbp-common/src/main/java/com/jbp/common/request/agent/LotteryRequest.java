package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LotteryRequest对象", description = "抽奖活动新增编辑对象")
public class LotteryRequest implements Serializable {

    @ApiModelProperty("活动id")
    private Integer lotteryId;

    @ApiModelProperty("活动名称")
//    @NotBlank(message = "活动名称不能为空!")
    private String topic;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态，1-上线，2-下线")
//    @NotNull(message = "活动状态不能为空!")
    private Integer state;

    @ApiModelProperty("活动描述")
    private String link;

    @ApiModelProperty("活动图片")
    private String images;

    @ApiModelProperty("中奖名单是否开启")
    private Boolean isWinners;

    @ApiModelProperty("个人中奖记录是否开启")
    private Boolean isSelfRecord;

    @ApiModelProperty("活动项目商品列表")
    private List<LotteryItemPrizeRequest> itemPrizeList;





}
