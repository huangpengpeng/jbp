package com.jbp.admin.controller.agent;


import com.jbp.common.model.user.UserScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserScoreFlowSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserScoreFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/agent/user/score/flow")
@Api(tags = "用户分数明细管理")
public class UserScoreFlowController {
    @Autowired
    private UserScoreFlowService userScoreFlowService;

    @PreAuthorize("hasAuthority('agent:user:score:flow:page')")
    @GetMapping("/list")
    @ApiOperation("用户分数管理分页列表")
    public CommonResult<CommonPage<UserScoreFlow>> page(UserScoreFlowSearchRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userScoreFlowService.getList(request.getUid(), pageParamRequest)));
    }
}
