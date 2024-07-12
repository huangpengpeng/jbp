package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.TeamItem;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.TeamItemAddRequest;
import com.jbp.common.request.agent.TeamItemPageRequest;
import com.jbp.common.request.agent.TeamItemUpdateRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.TeamItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/team/item")
@Api(tags = "团队项目管理")
public class TeamItemController {

    @Autowired
    private TeamItemService teamItemService;

    @PreAuthorize("hasAuthority('agent:team:item:page')")
    @GetMapping("/page")
    @ApiOperation("团队项目列表分页查询")
    public CommonResult<CommonPage<TeamItem>> getList(TeamItemPageRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(teamItemService.pageList(request.getTid(), request.getName(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:team:item:add')")
    @PostMapping("/add")
    @ApiOperation("新增团队项目")
    public CommonResult add(@RequestBody TeamItemAddRequest request) {
        teamItemService.add(request.getTid(), request.getName());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:team:item:update')")
    @PostMapping("/update")
    @ApiOperation("修改团队项目")
    public CommonResult update(@RequestBody TeamItemUpdateRequest request) {
        teamItemService.edit(request.getId(),request.getTid(),request.getName());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:team:item:delete')")
    @GetMapping("/delete")
    @ApiOperation("删除团队项目")
    public CommonResult delete(Integer id) {
        TeamItem teamItem = teamItemService.getById(id);
        if (teamItem == null) {
            throw new CrmebException("团队项目不存在");
        }
        teamItemService.removeById(id);
        return CommonResult.success();
    }


}
