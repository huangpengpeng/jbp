package com.jbp.service.event.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.utils.LotteryRedisKeyManager;
import com.jbp.service.event.InitPrizeToRedisEvent;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件监听
 */
@Slf4j
@Component
public class InitPrizeToRedisListener implements ApplicationListener<InitPrizeToRedisEvent> {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    LotteryPrizeService lotteryPrizeMapper;

    @Autowired
    LotteryItemService lotteryItemMapper;

    @Override
    public void onApplicationEvent(InitPrizeToRedisEvent initPrizeToRedisEvent) {
        log.info("begin InitPrizeToRedisListener," + initPrizeToRedisEvent);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(LotteryRedisKeyManager.getLotteryPrizeRedisKey(initPrizeToRedisEvent.getLotteryId()), "1");
        //已经初始化到缓存中了，不需要再次缓存
        if (!result) {
            log.info("already initial");
            initPrizeToRedisEvent.getCountDownLatch().countDown();
            return;
        }

        List<LotteryItem> lotteryItems = lotteryItemMapper.list(new QueryWrapper<LotteryItem>().lambda().eq(LotteryItem::getLotteryId, initPrizeToRedisEvent.getLotteryId()));

        //如果指定的奖品没有了，会生成一个默认的奖项
//        LotteryItem defaultLotteryItem = lotteryItems.parallelStream().filter(o -> o.getDefaultItem().intValue() == 1).findFirst().orElse(null);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> lotteryItemMap = new HashMap<>(16);
        try {
            lotteryItemMap.put(LotteryRedisKeyManager.getLotteryItemRedisKey(initPrizeToRedisEvent.getLotteryId()), mapper.writeValueAsString(lotteryItems));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        lotteryItemMap.put(RedisKeyManager.getDefaultLotteryItemRedisKey(initPrizeToRedisEvent.getLotteryId()), JsonUtils.renderJson(defaultLotteryItem) );
        redisTemplate.opsForValue().multiSet(lotteryItemMap);


//        List<LotteryPrize> lotteryPrizes = lotteryPrizeMapper.getList(1L);
//
//      //  保存一个默认奖项
//        AtomicReference<LotteryPrize> defaultPrize = new AtomicReference<>();
//        lotteryPrizes.stream().forEach(lotteryPrize -> {
//            if (lotteryPrize.getId().equals(defaultLotteryItem.getPrizeId())) {
//                defaultPrize.set(lotteryPrize);
//            }
//            String key = RedisKeyManager.getLotteryPrizeRedisKey(initPrizeToRedisEvent.getLotteryId(), lotteryPrize.getId().intValue());
//            setLotteryPrizeToRedis(key, lotteryPrize);
//        });
//        String key = RedisKeyManager.getDefaultLotteryPrizeRedisKey(initPrizeToRedisEvent.getLotteryId());
//        setLotteryPrizeToRedis(key, defaultPrize.get());
        initPrizeToRedisEvent.getCountDownLatch().countDown(); //表示初始化完成
        log.info("finish InitPrizeToRedisListener," + initPrizeToRedisEvent);
    }

    private void setLotteryPrizeToRedis(String key, LotteryPrize lotteryPrize) {
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.opsForHash().put(key, "id", lotteryPrize.getId());
        redisTemplate.opsForHash().put(key, "lotteryId", lotteryPrize.getLotteryId());
        redisTemplate.opsForHash().put(key, "prizeName", lotteryPrize.getPrizeName());
        redisTemplate.opsForHash().put(key, "prizeType", lotteryPrize.getPrizeType());
        redisTemplate.opsForHash().put(key, "totalStock", lotteryPrize.getTotalStock());
        redisTemplate.opsForHash().put(key, "validStock", lotteryPrize.getValidStock());
    }
}

