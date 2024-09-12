package com.jbp.service.exception;

import com.jbp.common.constants.LotteryConstants;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.service.service.agent.LotteryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NoneStockRewardProcessor extends AbstractRewardProcessor {

    @Autowired
    LotteryRecordService lotteryRecordMapper;

    @Override
    protected LotteryRecord afterProcessor(RewardContextDTO context) {

        LotteryRecord record = new LotteryRecord();
        record.setAccountIp(context.getAccountIp());
        record.setItemId(context.getLotteryItem().getId());
        record.setPrizeName(context.getPrizeName());
        record.setUid(context.getUserId());
        record.setCreateTime(new Date());
        record.setCreateTime(new Date());
        lotteryRecordMapper.saveOrUpdate(record);
        return record;
    }

    @Override
    protected void processor(RewardContextDTO context) {
        context.setPrizeId(context.getLotteryItem().getPrizeId());
        context.setPrizeName(context.getLotteryItem().getItemName());
    }

    @Override
    protected int getAwardType() {
        return LotteryConstants.PrizeTypeEnum.THANK.getValue();
    }
}
