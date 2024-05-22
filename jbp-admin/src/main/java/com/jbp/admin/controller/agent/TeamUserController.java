package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.TeamUserRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TeamUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/team/user")
@Api(tags = "团队用户")
public class TeamUserController {
    @Resource
    private TeamUserService teamUserService;

    @PreAuthorize("hasAuthority('agent:team:user:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<TeamUser>> getList(TeamUserRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(teamUserService.pageList(request.getTid(), request.getAccount(), request.getTeamLeader(), request.getNickname(),pageParamRequest)));
    }
}
