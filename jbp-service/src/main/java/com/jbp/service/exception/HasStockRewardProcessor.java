package com.jbp.service.exception;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.constants.LotteryConstants;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class HasStockRewardProcessor extends AbstractRewardProcessor {

    @Resource
    AsyncLotteryRecordTask asyncLotteryRecordTask;

    @Autowired
    LotteryPrizeService lotteryPrizeMapper;
    @Autowired
    private LotteryItemService lotteryItemDao;

    @Override
    protected LotteryRecord afterProcessor(RewardContextDTO context) {
        return asyncLotteryRecordTask.saveLotteryRecord(context.getAccountIp(), context.getLotteryItem(), context.getPrizeName(), context.getUserId());
    }

    @Override
    protected void processor(RewardContextDTO context) {
        //扣减库存（redis的更新）
        //   Long result = redisTemplate.opsForHash().increment(context.getKey(), "validStock", -1);
        //当前奖品库存不足，提示未中奖，或者返回一个兜底的奖品
        LotteryPrize lotteryPrize = lotteryPrizeMapper.getById(context.getLotteryItem().getPrizeId());
        List<Object> prizes = new ArrayList<>();
        if (lotteryPrize.getValidStock() - 1 < 0) {

            LotteryItem lotteryItem = lotteryItemDao.getOne(new QueryWrapper<LotteryItem>().lambda().orderByDesc(LotteryItem::getPercent).last(" limit 1"));
            lotteryPrize = lotteryPrizeMapper.getById(lotteryItem.getPrizeId());
            context.setLotteryItem(lotteryItemDao.getOne(new QueryWrapper<LotteryItem>().lambda().eq(LotteryItem::getLotteryId, context.getLottery().getId()).eq(LotteryItem::getPrizeId, lotteryPrize.getId())));
        }

        prizes.add(lotteryPrize.getId());
        prizes.add(lotteryPrize.getPrizeName());

        lotteryPrize.setValidStock(lotteryPrize.getValidStock() - 1);

        context.setPrizeId(Long.valueOf(prizes.get(0).toString()));
        context.setPrizeName(prizes.get(1).toString());


        //更新库存（数据库的更新）
        lotteryPrizeMapper.updateById(lotteryPrize);
    }

    @Override
    protected int getAwardType() {
        return LotteryConstants.PrizeTypeEnum.NORMAL.getValue();
    }
}
