package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.Team;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.TeamEditRequest;
import com.jbp.common.request.agent.TeamPageRequest;
import com.jbp.common.request.agent.TeamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/team")
@Api(tags = "团队管理积分")
public class TeamController {
    @Resource
    private TeamService teamService;

    @PreAuthorize("hasAuthority('agent:team:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<Team>> getList(TeamPageRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(teamService.pageList(request.getName(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:team:add')")
    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult add(@RequestBody TeamRequest request) {
        teamService.save(request.getLeaderId(), request.getName());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:team:update')")
    @PostMapping("/update")
    @ApiOperation("修改")
    public CommonResult update(@RequestBody TeamEditRequest request) {
        teamService.editName(request.getId(), request.getName());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:team:delete')")
    @GetMapping("/delete/{id}")
    @ApiOperation("删除")
    public CommonResult delete(@PathVariable("id") Integer id) {
        teamService.delete(id);
        return CommonResult.success();
    }
}
