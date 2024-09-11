package com.jbp.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.enums.ReturnCodeEnum;
import com.jbp.common.model.agent.LotteryUser;
import com.jbp.common.response.LotteryResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.IPUtil;
import com.jbp.common.utils.LotteryRedisKeyManager;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.LotteryService;
import com.jbp.service.service.agent.LotteryUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


/**
 * 签到控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/lottery")
@Api(tags = "等级控制器")
public class LotteryController {


    @Autowired
    private LotteryUserService lotteryUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LotteryService lotteryService;

    @ApiOperation(value = "抽奖")
    @RequestMapping(value = "/entrance", method = RequestMethod.GET)
    public CommonResult<LotteryResponse> entrance(Long id, HttpServletRequest request) {


        String accountIp = null;
        JSONObject jsonObject = null;
        try {
            accountIp = IPUtil.getIpAddress(request);
            jsonObject = new JSONObject();
            // 判断当前用户上一次抽奖是否结束
            checkDrawParams(id, accountIp);
            // 抽奖
            RewardContextDTO context = lotteryService.doDraw(accountIp, id);
            jsonObject.put("id", context.getPrizeId());
            LotteryPrize lotteryPrize = lotteryPrizeMng.getInfo(context.getPrizeId());
            // 如果关联了商品执行商品下单操作
            if (StringUtils.isNotBlank(lotteryPrize.getLotteryGood())) {
                JSONObject paySecret = new JSONObject();
                paySecret.put(lotteryPrize.getLotteryGood(), context.getLotteryRecord().getId());
                jsonObject.put("paySecret", CryptoDesUtils.encrypt(paySecret.toJSONString()));
                jsonObject.put("goodsId", lotteryPrize.getLotteryGood());
            }
        } finally {
            redisTemplate.delete(LotteryRedisKeyManager.getDrawingRedisKey(accountIp));
        }


        return CommonResult.success();
    }


    private void checkDrawParams(Long id, String accountIp) {
        if (null == id) {
            throw new RuntimeException(ReturnCodeEnum.REQUEST_PARAM_NOT_VALID.getMsg());
        }
        LotteryUser lotteryUser = lotteryUserService.getOne(new QueryWrapper<LotteryUser>().lambda().eq(LotteryUser::getUid, userService.getUserId()).eq(LotteryUser::getLotteryId, id));

        if (lotteryUser == null || lotteryUser.getNumber() <= 0) {
            throw new RuntimeException(ReturnCodeEnum.LOTTER_NUMBER.getMsg());
        }

        //采用setNx命令，判断当前用户上一次抽奖是否结束
        Boolean result = redisTemplate.opsForValue().setIfAbsent(LotteryRedisKeyManager.getDrawingRedisKey(accountIp), "1", 60, TimeUnit.SECONDS);
        //如果为false，说明上一次抽奖还未结束
        if (!result) {
            throw new RuntimeException(ReturnCodeEnum.LOTTER_DRAWING.getMsg());
        }
    }


}



