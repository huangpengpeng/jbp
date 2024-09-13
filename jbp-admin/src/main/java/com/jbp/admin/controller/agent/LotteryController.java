package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRequest;
import com.jbp.common.request.agent.LotterySearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.LotteryRedisKeyManager;
import com.jbp.service.service.agent.LotteryItemService;
import com.jbp.service.service.agent.LotteryPrizeService;
import com.jbp.service.service.agent.LotteryService;
import com.jbp.service.service.agent.LotteryUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/lottery")
@Api(tags = "抽奖活动管理")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;
    @Autowired
    private LotteryPrizeService lotteryPrizeService;
    @Autowired
    private LotteryItemService lotteryItemService;
    @Autowired
    private LotteryUserService lotteryUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PreAuthorize("hasAuthority('agent:lottery:page')")
    @ApiOperation(value = "抽奖活动分页列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<Lottery>> pageList(LotterySearchRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(lotteryService.pageList(request, pageParamRequest)));
    }

    @ApiOperation(value = "抽奖活动详情")
    @PostMapping(value = "/detail/{id}")
    public CommonResult<Lottery> detail(@PathVariable(value = "id") Long id) {
        Lottery lottery = lotteryService.getById(id);
        if (lottery == null) {
            throw new CrmebException("抽奖活动不存在！");
        }
        return CommonResult.success(lottery);
    }


    @PreAuthorize("hasAuthority('agent:lottery:add')")
    @ApiOperation(value = "抽奖活动新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated LotteryRequest request) {
        if (lotteryService.add(request)) {
            redisTemplate.delete(LotteryRedisKeyManager.getLotteryItemRedisKey(request.getId()));
            redisTemplate.delete(LotteryRedisKeyManager.getLotteryPrizeRedisKey(request.getId()));

            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('agent:lottery:edit')")
    @ApiOperation(value = "抽奖活动编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<String> edit(@RequestBody @Validated LotteryRequest request) {
        if (lotteryService.edit(request)) {

            redisTemplate.delete(LotteryRedisKeyManager.getLotteryItemRedisKey(request.getId()));
            redisTemplate.delete(LotteryRedisKeyManager.getLotteryPrizeRedisKey(request.getId()));

            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('agent:lottery:delete')")
    @ApiOperation(value = "抽奖活动删除")
    @PostMapping(value = "/delete/{id}")
    public CommonResult<Boolean> delete(@PathVariable(value = "id") Long id) {
        Lottery lottery = lotteryService.getById(id);
        if (lottery == null) {
            throw new CrmebException("抽奖活动不存在！");
        }
        lotteryService.removeById(id);
        lotteryPrizeService.remove(new QueryWrapper<LotteryPrize>().lambda().eq(LotteryPrize::getLotteryId, id));
        lotteryItemService.remove(new QueryWrapper<LotteryItem>().lambda().eq(LotteryItem::getLotteryId, id));
        return CommonResult.success(true);
    }

    @PreAuthorize("hasAuthority('agent:lottery:switch')")
    @ApiOperation(value = "抽奖活动开关")
    @PostMapping(value = "/switch/{id}")
    public CommonResult<Boolean> open(@PathVariable(value = "id") Long id) {
        Lottery lottery = lotteryService.getById(id);
        if (lottery == null) {
            throw new CrmebException("抽奖活动不存在！");
        }
        if (lottery.getState() == 2) {
            List<Lottery> list = lotteryService.list();
            list.forEach(e->e.setState(2));
            lotteryService.updateBatchById(list);
            lottery.setState(1);
            lotteryService.updateById(lottery);
        }else {
            lottery.setState(2);
            lotteryService.updateById(lottery);
        }
        return CommonResult.success(true);
    }
















}
