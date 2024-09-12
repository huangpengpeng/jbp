package com.jbp.service.exception;

import com.jbp.common.constants.LotteryConstants;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.service.event.RewardProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractRewardProcessor implements RewardProcessor<RewardContextDTO>, ApplicationContextAware {

    public static Map<Integer, RewardProcessor> rewardProcessorMap = new ConcurrentHashMap<Integer, RewardProcessor>();

    @Autowired
    protected RedisTemplate redisTemplate;


    @Override
    public LotteryRecord doReward(RewardContextDTO context) {
        beforeProcessor(context);
        processor(context);
        return afterProcessor(context);
    }

    protected abstract LotteryRecord afterProcessor(RewardContextDTO context);




    private void beforeProcessor(RewardContextDTO context) {
    }

    /**
     * 发放对应的奖品
     *
     * @param context
     */
    protected abstract void processor(RewardContextDTO context);

    /**
     * 返回当前奖品类型
     *
     * @return
     */
    protected abstract int getAwardType();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        rewardProcessorMap.put(LotteryConstants.PrizeTypeEnum.THANK.getValue(), (RewardProcessor) applicationContext.getBean(NoneStockRewardProcessor.class));
        rewardProcessorMap.put(LotteryConstants.PrizeTypeEnum.NORMAL.getValue(), (RewardProcessor) applicationContext.getBean(HasStockRewardProcessor.class));
    }
}
