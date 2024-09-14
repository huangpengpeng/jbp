package com.jbp.service.exception;

import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.service.service.agent.LotteryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class AsyncLotteryRecordTask {

    @Autowired
    LotteryRecordService lotteryRecordMapper;

    public LotteryRecord saveLotteryRecord(String accountIp, LotteryItem lotteryItem, String prizeName, Integer userId) {
        log.info(Thread.currentThread().getName() + "---saveLotteryRecord");
        //存储中奖信息
        LotteryRecord record = new LotteryRecord();
        record.setAccountIp(accountIp);
        record.setItemId(lotteryItem.getId());
        record.setPrizeName(prizeName);
        record.setUid(userId);
        record.setCreateTime(new Date());
        record.setCreateTime(new Date());
        lotteryRecordMapper.save(record);
        return record;
    }
}
