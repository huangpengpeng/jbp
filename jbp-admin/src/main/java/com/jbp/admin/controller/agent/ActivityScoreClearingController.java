package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreClearingEditRequest;
import com.jbp.common.request.agent.ActivityScoreClearingSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ActivityScoreClearingService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/activity/score/clearing")
@Api(tags = "积分活动结算管理")
public class ActivityScoreClearingController {

    @Autowired
    private ActivityScoreClearingService activityScoreClearingService;
    @Autowired
    private UserService userService;


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
    public CommonResult<CommonPage<ActivityScoreClearing>> list(ActivityScoreClearingSearchRequest request, @ModelAttribute PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        CommonPage<ActivityScoreClearing> result = CommonPage.restPage(activityScoreClearingService.getList(uid,request.getActivityScoreName(),pageParamRequest));
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

    @PreAuthorize("hasAuthority('activity:score:clearing:edit')")
    @ApiOperation(value = "积分活动年卡积分编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Boolean> edit(@RequestBody @Validated ActivityScoreClearingEditRequest request) {
        return CommonResult.success(activityScoreClearingService.edit(request));
    }
}
