package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Lottery;
import com.jbp.common.model.agent.LotteryRecord;
import com.jbp.common.model.agent.LotteryUser;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.LotteryRecordService;
import com.jbp.service.service.agent.LotteryService;
import com.jbp.service.service.agent.LotteryUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/front/lottery/user")
@Api(tags = "抽奖活动中奖记录控制器")
public class LotteryUserController {

    @Autowired
    private LotteryUserService lotteryUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private LotteryService lotteryService;

    @ApiOperation(value = "用户抽奖次数")
    @GetMapping(value = "/time")
    public CommonResult<LotteryUser> time() {
        Integer userId = userService.getUserId();
        if (userId== null) {
            throw new CrmebException("请先登录！");
        }
        Lottery lottery = lotteryService.getOne(new QueryWrapper<Lottery>().lambda().eq(Lottery::getState, 1));
        if (lottery == null) {
            throw new CrmebException("暂无抽奖活动！");
        }
        LotteryUser lotteryUser = lotteryUserService.getOne(new QueryWrapper<LotteryUser>().lambda().eq(LotteryUser::getUid, userId).eq(LotteryUser::getLotteryId, lottery.getId()));
        return CommonResult.success(lotteryUser);
    }
}
