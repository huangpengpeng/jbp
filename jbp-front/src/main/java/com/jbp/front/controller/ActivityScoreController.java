package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.response.ActivityScoreResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ActivityScoreClearingService;
import com.jbp.service.service.agent.ActivityScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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
@RequestMapping("api/front/activity/score")
@Api(tags = "积分活动控制器")
public class ActivityScoreController {

    @Autowired
    private ActivityScoreService activityScoreService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityScoreClearingService activityScoreClearingService;
    @Autowired
    private SystemConfigService systemConfigService;


    @ApiOperation(value = "用户活动分值")
    @RequestMapping(value = "/getUserMark", method = RequestMethod.GET)
    public CommonResult<ActivityScoreResponse> getUserMark(Integer activityId) {
        ActivityScoreResponse activityScoreResponse = new ActivityScoreResponse();
        ActivityScore activityScore = activityScoreService.getById(activityId);
        if (activityScore == null) {
            throw new RuntimeException("活动不存在");
        }


        activityScoreResponse.setEndTime(DateTimeUtils.format(activityScore.getEndTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        activityScoreResponse.setStartTime(DateTimeUtils.format(activityScore.getStartTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        activityScoreResponse.setMark(activityScore.getMark());

        ActivityScoreClearing activityScoreClearing = activityScoreClearingService.getOne(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId).eq(ActivityScoreClearing::getUid, userService.getUserId()));
        if (activityScoreClearing == null) {
            activityScoreResponse.setScore(0);
            activityScoreResponse.setCardCount(0);
            return CommonResult.success(activityScoreResponse);
        }

        activityScoreResponse.setScore(activityScoreClearing.getScore());
        activityScoreResponse.setCardCount(activityScoreClearing.getCardCount());

        return CommonResult.success(activityScoreResponse);
    }


    @ApiOperation(value = "用户活动列表")
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public CommonResult<List<ActivityScore>> getList() {

        List<ActivityScore> list = activityScoreService.list(new QueryWrapper<ActivityScore>().lambda().eq(ActivityScore::getStatus, "开启"));

        return CommonResult.success(list);
    }


}



