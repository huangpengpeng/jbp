package com.jbp.common.dto;

import com.jbp.common.model.agent.Lottery;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryRecord;
import lombok.Data;

@Data
public class RewardContextDTO {

    private Lottery lottery;

    private LotteryItem lotteryItem;

    private String key;

    private String accountIp;

    private String prizeName;

    private Integer level;

    private Long prizeId;
    private Integer userId;

    private LotteryRecord lotteryRecord;
}
