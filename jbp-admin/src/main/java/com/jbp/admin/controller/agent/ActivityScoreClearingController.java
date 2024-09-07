package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ActivityScoreClearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score/clearing")
@Api(tags = "积分活动结算管理")
public class ActivityScoreClearingController {

    @Autowired
    private ActivityScoreClearingService activityScoreClearingService;


    @PreAuthorize("hasAuthority('activity:score:clearing')")
    @ApiOperation(value = "积分活动结算")
    @PostMapping(value = "/clearing/{activityId}")
    public CommonResult<String> page(@PathVariable(value = "activityId") Integer activityId) {
        activityScoreClearingService.clearingUser(activityId);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('activity:score:clearing:list')")
    @ApiOperation(value = "积分活动结算管理分页列表")
    @GetMapping(value = "/list")
    public CommonResult<CommonPage<ActivityScoreClearing>> list(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<ActivityScoreClearing> result = CommonPage.restPage(activityScoreClearingService.getList(pageParamRequest));
        return CommonResult.success(result);
    }


    @PreAuthorize("hasAuthority('activity:score:clearing:delete')")
    @ApiOperation(value = "积分活动管理删除")
    @PostMapping(value = "/delete/{activityId}")
    public CommonResult<Boolean> delete(@PathVariable(value = "activityId") Integer activityId) {
        return CommonResult.success(activityScoreClearingService.del(activityId));
    }

    @PreAuthorize("hasAuthority('activity:score:verify')")
    @ApiOperation(value = "积分活动确认")
    @PostMapping(value = "/verify/{activityId}")
    public CommonResult<String> verify(@PathVariable(value = "activityId")Integer activityId) {

        activityScoreClearingService.verifyUser(activityId);
        return CommonResult.success();
    }









}
