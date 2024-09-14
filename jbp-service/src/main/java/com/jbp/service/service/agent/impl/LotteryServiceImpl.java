package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.enums.ReturnCodeEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.utils.LotteryRedisKeyManager;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRequest;
import com.jbp.common.request.agent.LotterySearchRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.LotteryDao;
import com.jbp.service.event.InitPrizeToRedisEvent;
import com.jbp.service.exception.AbstractRewardProcessor;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import com.jbp.service.service.agent.LotteryService;
import com.jbp.service.service.agent.LotteryUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryServiceImpl extends ServiceImpl<LotteryDao, Lottery> implements LotteryService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private LotteryItemService lotteryItemService;
    @Autowired
    private LotteryPrizeService lotteryPrizeService;
    @Autowired
    private LotteryUserService lotteryUserService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    private static final int mulriple = 10000;





    @Override
    public RewardContextDTO doDraw(String ip, Long id) {

        RewardContextDTO context = new RewardContextDTO();
        LotteryItem lotteryItem = null;

        // JUC工具 需要等待线程结束之后才能运行
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 判断活动有效性
        Lottery lottery = checkLottery(id);
        // 发布事件，用来加载指定活动的奖品信息
        applicationContext.publishEvent(new InitPrizeToRedisEvent(this, lottery.getId().intValue(), countDownLatch));
        // 开始抽奖
        lotteryItem = doPlay(lottery);
        // 记录奖品并扣减库存
        try {
            countDownLatch.await(); // 等待奖品初始化完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String key = LotteryRedisKeyManager.getLotteryPrizeRedisKey(lottery.getId().intValue(),
                lotteryItem.getPrizeId().intValue());

        LotteryPrize lotteryPrize = lotteryPrizeService.getById(lotteryItem.getPrizeId());

//		int prizeType = Integer.parseInt(redisTemplate.opsForHash().get(key, "prizeType").toString());
        context.setLottery(lottery);
        context.setLotteryItem(lotteryItem);
        context.setAccountIp(ip);
        context.setKey(key);
        context.setUserId(userService.getUserId());
        // 调整库存及记录中奖信息
        LotteryRecord lotteryRecord = AbstractRewardProcessor.rewardProcessorMap.get(lotteryPrize.getPrizeType()).doReward(context);
        context.setLotteryRecord(lotteryRecord);

        //扣除抽奖次数
        lotteryUserService.reducere(userService.getUserId(), id, 1);

        return context;

    }

    //执行抽奖
    private LotteryItem doPlay(Lottery lottery) {
        LotteryItem lotteryItem = null;
        Object lotteryItemsObj = redisTemplate.opsForValue().get(LotteryRedisKeyManager.getLotteryItemRedisKey(lottery.getId().intValue()));
        List<LotteryItem> lotteryItems;
        //说明还未加载到缓存中，同步从数据库加载，并且异步将数据缓存
        if (lotteryItemsObj == null) {
            lotteryItems = lotteryItemService.list(new QueryWrapper<LotteryItem>().lambda().eq(LotteryItem::getLotteryId, lottery.getId()));
        } else {
            lotteryItems = JSONArray.parseArray(lotteryItemsObj.toString(), LotteryItem.class);
        }
        //奖项数据未配置
        if (lotteryItems.isEmpty()) {
            throw new RuntimeException(ReturnCodeEnum.LOTTER_ITEM_NOT_INITIAL.getMsg());
        }
        int lastScope = 0;
        //将数组随机打乱
        Collections.shuffle(lotteryItems);
        Map<Integer, int[]> awardItemScope = new HashMap<>();
        //item.getPercent=0.05 = 5%
        for (LotteryItem item : lotteryItems) {
            int currentScope = lastScope + new BigDecimal(item.getPercent().floatValue()).multiply(new BigDecimal(mulriple)).intValue();
            awardItemScope.put(item.getId().intValue(), new int[]{lastScope + 1, currentScope});
            lastScope = currentScope;
        }
        int luckyNumber = new Random().nextInt(mulriple);
        int luckyPrizeId = 0;
        if (!awardItemScope.isEmpty()) {
            Set<Map.Entry<Integer, int[]>> set = awardItemScope.entrySet();
            for (Map.Entry<Integer, int[]> entry : set) {
                if (luckyNumber >= entry.getValue()[0] && luckyNumber <= entry.getValue()[1]) {
                    luckyPrizeId = entry.getKey();
                    break;
                }
            }
        }
        for (LotteryItem item : lotteryItems) {
            if (item.getId().intValue() == luckyPrizeId) {
                lotteryItem = item;
                break;
            }
        }
        return lotteryItem;
    }


    /**
     * 校验当前活动的有效信息
     *
     * @return
     */
    private Lottery checkLottery(Long id) {
        Lottery lottery;
        Object lotteryJsonStr = redisTemplate.opsForValue().get(LotteryRedisKeyManager.getLotteryRedisKey(id.intValue()));
        if (null != lotteryJsonStr) {
            lottery = JSON.parseObject(lotteryJsonStr.toString(), Lottery.class);
        } else {
            lottery = getById(id);
        }
        if (lottery == null) {

            throw new RuntimeException(ReturnCodeEnum.LOTTER_NOT_EXIST.getMsg());
        }
        //判断活动是否结束
        Date now = new Date();
        if (now.before(lottery.getStartTime()) || now.after(lottery.getEndTime())) {
            throw new RuntimeException(ReturnCodeEnum.LOTTER_FINISH.getMsg());

        }
        return lottery;
    }
    @Override
    public boolean add(LotteryRequest request) {
        if (request.getItemPrizeList().isEmpty()) {
            throw new CrmebException("奖品未设置!");
        }
        //抽奖活动基本信息
        Lottery lottery = new Lottery();
        BeanUtils.copyProperties(request, lottery);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        lottery.setImages(systemAttachmentService.clearPrefix(request.getImages(), cdnUrl));
        lottery.setLink(systemAttachmentService.clearPrefix(request.getLink(), cdnUrl));
        lottery.setState(2);
        List<LotteryPrize> prizeList = CollUtil.newArrayList();
        List<LotteryItem> itemList = CollUtil.newArrayList();
        request.getItemPrizeList().forEach(e->{

            //抽奖奖品信息
            LotteryPrize lotteryPrize = new LotteryPrize();
            BeanUtils.copyProperties(e, lotteryPrize);
            lotteryPrize.setImages(systemAttachmentService.clearPrefix(e.getImages(), cdnUrl));
            lotteryPrize.setTotalStock(e.getValidStock());
            lotteryPrizeService.save(lotteryPrize);

            //抽奖概率信息
            LotteryItem lotteryItem = new LotteryItem();
            BeanUtils.copyProperties(e, lotteryItem);
            lotteryItem.setPrizeId(lotteryPrize.getId());
            lotteryItem.setDefaultItem(-1);
            lotteryItemService.save(lotteryItem);

            prizeList.add(lotteryPrize);
            itemList.add(lotteryItem);
        });
        Boolean execute = transactionTemplate.execute(e -> {
            save(lottery);
            prizeList.forEach(prize -> prize.setLotteryId(lottery.getId()));
            lotteryPrizeService.updateBatchById(prizeList);
            itemList.forEach(item -> item.setLotteryId(lottery.getId()));
            lotteryItemService.updateBatchById(itemList);
            return Boolean.TRUE;
        });
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public boolean edit(LotteryRequest request) {

        Lottery lottery = getById(request.getId());
        if (lottery == null) {
            throw new CrmebException("抽奖活动不存在!");
        }
        BeanUtils.copyProperties(request, lottery);
        lottery.setId(request.getId().longValue());
        String cdnUrl = systemAttachmentService.getCdnUrl();
        lottery.setImages(systemAttachmentService.clearPrefix(request.getImages(), cdnUrl));
        lottery.setLink(systemAttachmentService.clearPrefix(request.getLink(), cdnUrl));
        if (StringUtils.isEmpty(request.getRule())){
            lottery.setRule("");
        }
        List<LotteryPrize> prizeList = CollUtil.newArrayList();
        List<LotteryItem> itemList = CollUtil.newArrayList();
        request.getItemPrizeList().forEach(e->{
            //抽奖奖品信息
            LotteryPrize lotteryPrize = lotteryPrizeService.getById(e.getPrizeId());
            BeanUtils.copyProperties(e, lotteryPrize);
            lotteryPrize.setImages(systemAttachmentService.clearPrefix(e.getImages(), cdnUrl));
            lotteryPrize.setTotalStock(e.getValidStock());
            prizeList.add(lotteryPrize);
            //抽奖概率信息
            LotteryItem lotteryItem = lotteryItemService.getById(e.getItemId());
            BeanUtils.copyProperties(e, lotteryItem);
            itemList.add(lotteryItem);
        });
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(lottery);
            lotteryPrizeService.updateBatchById(prizeList);
            lotteryItemService.updateBatchById(itemList);
            return Boolean.TRUE;
        });

        return Boolean.TRUE.equals(execute);
    }

    @Override
    public PageInfo<Lottery> pageList(LotterySearchRequest request, PageParamRequest pageParamRequest) {
        Page<Lottery> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<Lottery> list = list(new QueryWrapper<Lottery>().lambda().orderByDesc(Lottery::getId));
        return CommonPage.copyPageInfo(page, list);
    }
}
