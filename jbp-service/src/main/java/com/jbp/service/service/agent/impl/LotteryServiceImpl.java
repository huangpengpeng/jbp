package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.model.agent.Lottery;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRequest;
import com.jbp.common.request.agent.LotterySearchRequest;
import com.jbp.service.dao.agent.LotteryDao;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import com.jbp.service.service.agent.LotteryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryServiceImpl extends ServiceImpl<LotteryDao, Lottery> implements LotteryService {

    @Autowired
    private LotteryDao dao;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private LotteryPrizeService lotteryPrizeService;
    @Autowired
    private LotteryItemService lotteryItemService;

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
        List<LotteryPrize> prizeList = CollUtil.newArrayList();
        List<LotteryItem> itemList = CollUtil.newArrayList();
        request.getItemPrizeList().forEach(e->{

            //抽奖奖品信息
            LotteryPrize lotteryPrize = new LotteryPrize();
            BeanUtils.copyProperties(e, lotteryPrize);
            lotteryPrize.setImages(systemAttachmentService.clearPrefix(e.getImages(), cdnUrl));
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
            itemList.forEach(item -> item.setLotteryId(lottery.getId()));
            return Boolean.TRUE;
        });
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public boolean edit(LotteryRequest request) {

        Lottery lottery = getById(request.getLotteryId());
        if (lottery == null) {
            throw new CrmebException("抽奖活动不存在!");
        }
        BeanUtils.copyProperties(request, lottery);
        lottery.setId(request.getLotteryId().longValue());
        String cdnUrl = systemAttachmentService.getCdnUrl();
        lottery.setImages(systemAttachmentService.clearPrefix(request.getImages(), cdnUrl));
        lottery.setLink(systemAttachmentService.clearPrefix(request.getLink(), cdnUrl));
        List<LotteryPrize> prizeList = CollUtil.newArrayList();
        List<LotteryItem> itemList = CollUtil.newArrayList();
        request.getItemPrizeList().forEach(e->{
            //抽奖奖品信息
            LotteryPrize lotteryPrize = new LotteryPrize();
            BeanUtils.copyProperties(e, lotteryPrize);
            lotteryPrize.setId(e.getPrizeId().longValue());
            prizeList.add(lotteryPrize);
            //抽奖概率信息
            LotteryItem lotteryItem = new LotteryItem();
            BeanUtils.copyProperties(e, lotteryItem);
            lotteryItem.setId(e.getItemId().longValue());
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
