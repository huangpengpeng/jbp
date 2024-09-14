package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.LotteryPrizeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lottery/prize")
@Api(tags = "抽奖活动管理")
public class LotteryPrizeController {

    @Autowired
    private LotteryPrizeService lotteryPrizeService;

    @ApiOperation(value = "抽奖活动奖品列表")
    @PostMapping(value = "/list/{id}")
    public CommonResult<List<LotteryPrize>> detail(@PathVariable(value = "id") Long id) {
        List<LotteryPrize> prizeList = lotteryPrizeService.getListByLotteryId(id);
        return CommonResult.success(prizeList);
    }
}
