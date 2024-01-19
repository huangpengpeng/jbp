package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.TeamEditRequest;
import com.jbp.common.request.agent.TeamPageRequest;
import com.jbp.common.request.agent.TeamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/team")
@Api(tags = "团队管理")
public class TeamController {
    @Resource
    UserService userService;
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
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            return CommonResult.failed("账户信息错误");
        }
        teamService.save(user.getId(), request.getName());
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
    @GetMapping("/delete/{name}")
    @ApiOperation("删除")
    public CommonResult delete(@PathVariable("name") String name) {
        Team byName = teamService.getByName(name);
        teamService.delete(byName.getId());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:team:name')")
    @GetMapping("/name")
    @ApiOperation("获取团队名")
    public CommonResult getTeamName() {
        return CommonResult.success(teamService.list());
    }

    @ApiOperation("列表")
    @GetMapping("/list")
    public CommonResult<List<Team>> list(String name) {
        return CommonResult.success(teamService.getByNameList(name));
    }
}
