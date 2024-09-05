package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
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
@RequestMapping("api/admin/agent/activity/score")
@Api(tags = "积分活动管理")
public class ActivityScoreController {

    @Autowired
    private ActivityScoreService activityScoreService;

    @PreAuthorize("hasAuthority('activity:score:list')")
    @ApiOperation(value = "积分活动管理分页列表")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<ActivityScore>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<ActivityScore> result = CommonPage.restPage(activityScoreService.getList(pageParamRequest));
        return CommonResult.success(result);
    }














}
