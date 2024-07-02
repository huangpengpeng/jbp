package com.jbp.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.jbp.common.model.product.ProductExtConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ProductExtConfigAddRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.ProductExtConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/platform/product/ext/config")
@Api(tags = "商品扩展信息配置")
public class ProductExtConfigController {

    @Autowired
    private ProductExtConfigService service;

    @PreAuthorize("hasAuthority('agent:platform:product:ext:config:page')")
    @GetMapping("/page")
    @ApiOperation("商品扩展信息表")
    public CommonResult<CommonPage<ProductExtConfig>> detail(PageParamRequest pageParamRequest) {
        PageInfo<ProductExtConfig> page = service.pageList(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:ext:config:add')")
    @PostMapping("/add")
    @ApiOperation("商品扩展信息添加")
    public CommonResult<Boolean> add(@RequestBody ProductExtConfigAddRequest request) {
        return CommonResult.success(service.add(request));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:ext:config:delete')")
    @GetMapping("/delete")
    @ApiOperation("商品扩展信息删除")
    public CommonResult<Boolean> delete(Long id) {
        return CommonResult.success(service.del(id));
    }







}
