package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Lottery;
import com.jbp.common.model.agent.LotteryItem;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.request.agent.LotteryPrizeFrontRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.LotteryRecordService;
import com.jbp.service.service.agent.LotteryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/front/lottery/prize")
@Api(tags = "抽奖活动中奖记录控制器")
public class LotteryRecordController {

    @Autowired
    private LotteryRecordService lotteryRecordService;
    @Autowired
    private LotteryService lotteryService;

    @ApiOperation(value = "用户抽奖活动中奖记录")
    @GetMapping(value = "/list/{uid}")
    public CommonResult<List<LotteryRecord>> list(@PathVariable(value = "uid") Integer uid) {
        Lottery lottery = lotteryService.getOne(new QueryWrapper<Lottery>().lambda().eq(Lottery::getState, 1).orderByDesc(Lottery::getId).last("LIMIT 1"));
        List<LotteryRecord> prizeList = lotteryRecordService.getFrontList(lottery.getId().intValue(),uid);
        return CommonResult.success(prizeList);
    }

}
