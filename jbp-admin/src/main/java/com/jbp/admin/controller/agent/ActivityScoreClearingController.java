package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ActivityScoreClearingService;
import com.jbp.service.service.agent.ActivityScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score/clearing")
@Api(tags = "积分活动结算管理")
public class ActivityScoreClearingController {

    @Autowired
    private ActivityScoreClearingService activityScoreClearingService;


    @PreAuthorize("hasAuthority('activity:score:clearing')")
    @ApiOperation(value = "积分活动结算")
    @GetMapping(value = "/clearing")
    public CommonResult<String> page( Integer activityId) {

        activityScoreClearingService.clearingUser(activityId);
        return CommonResult.success();
    }





    @PreAuthorize("hasAuthority('activity:score:verify')")
    @ApiOperation(value = "积分活动确认")
    @GetMapping(value = "/verify")
    public CommonResult<String> verify(Integer activityId) {

        activityScoreClearingService.verifyUser(activityId);
        return CommonResult.success();
    }









}
