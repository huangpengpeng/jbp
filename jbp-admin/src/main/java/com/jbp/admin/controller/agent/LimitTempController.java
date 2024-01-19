package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.LimitTemp;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LimitTempAddRequest;
import com.jbp.common.request.agent.LimitTempEditRequest;
import com.jbp.common.request.agent.LimitTempRequest;
import com.jbp.common.response.LimitTempResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.LimitTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/limit/temp")
@Api(tags = "限制模版")
public class LimitTempController {
    @Resource
    private LimitTempService limitTempService;

    @ApiOperation(value = "限制模版等级列表")
    @GetMapping(value = "/page")
    private CommonResult<CommonPage<LimitTemp>> pageList(LimitTempRequest request, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(limitTempService.pageList(request.getName(), request.getType(), pageParamRequest)));
    }

    @ApiOperation("添加")
    @PostMapping("/add")
    private CommonResult add(@RequestBody LimitTempAddRequest request) {
        LimitTemp limitTemp = limitTempService.getByName(request.getName());
        if (!ObjectUtil.isNull(limitTemp)) {
            return CommonResult.failed("限制模板等级名称已经存在");
        }
        limitTempService.add(request.getName(), request.getType(), request.getCapaIdList(), request.getCapaXsIdList(), request.getWhiteIdList(), request.getTeamIdList(), request.getHasPartner(), request.getPCapaIdList(), request.getPCapaXsIdList(), request.getHasRelation(), request.getRCapaIdList(), request.getRCapaXsIdList(), request.getDescription());
        return CommonResult.success();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    private CommonResult update(@RequestBody LimitTempEditRequest request) {
        LimitTemp limitTemp = limitTempService.getByName(request.getName());
        if (!ObjectUtil.isNull(limitTemp)) {
            return CommonResult.failed("限制模板等级名称已经存在");
        }
        limitTempService.update(request.getId(), request.getName(), request.getType(), request.getCapaIdList(), request.getCapaXsIdList(), request.getWhiteIdList(), request.getTeamIdList(), request.getHasPartner(), request.getPCapaIdList(), request.getPCapaXsIdList(), request.getHasRelation(), request.getRCapaIdList(), request.getRCapaXsIdList(), request.getDescription());
        return CommonResult.success();
    }

    @ApiOperation("详情")
    @GetMapping("/details/{id}")
    private CommonResult<LimitTempResponse> details(@PathVariable("id") Integer id) {
        return CommonResult.success(limitTempService.details(id));
    }
}
