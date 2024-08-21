package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.CapaOrderRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.CapaOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/capa/order")
@Api(tags = "等级")
public class CapaOrderController {

    @Resource
    private CapaOrderService capaOrderService;

    @PreAuthorize("hasAuthority('capa:order:list')")
    @ApiOperation(value = "订货管理分页列表列表")
    @GetMapping(value = "/list")
    public CommonResult<CommonPage<CapaOrder>> page(@ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<CapaOrder> result = CommonPage.restPage(capaOrderService.getList(pageParamRequest));
        return CommonResult.success(result);
    }

    @PreAuthorize("hasAuthority('capa:order:edit')")
    @ApiOperation(value = "订货管理编辑")
    @PostMapping(value = "/edit")
    public CommonResult<Object> edit(@RequestBody @Validated CapaOrderRequest capaRequest) {
        return CommonResult.success(capaOrderService.edit(capaRequest));
    }








}
