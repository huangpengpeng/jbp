package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.Lottery;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.request.agent.LotteryPrizeFrontRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.LotteryPrizeService;
import com.jbp.service.service.agent.LotteryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/front/lottery/prize")
@Api(tags = "抽奖活动商品控制器")
public class LotteryPrizeController {

    @Autowired
    private LotteryPrizeService lotteryPrizeService;
    @Autowired
    private LotteryService lotteryService;

    @ApiOperation(value = "抽奖活动奖品列表")
    @PostMapping(value = "/list")
    public CommonResult<List<LotteryPrize>> list(LotteryPrizeFrontRequest request) {
        Lottery lottery = lotteryService.getOne(new QueryWrapper<Lottery>().lambda().eq(Lottery::getState, 1).orderByDesc(Lottery::getId).last("LIMIT 1"));
        List<LotteryPrize> prizeList = lotteryPrizeService.getFrontList(request.getPrizeType(),lottery.getId());
        return CommonResult.success(prizeList);
    }












}
