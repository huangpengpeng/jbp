package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ProductCommConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/platform/product/comm/config")
@Api(tags = "商品佣金配置")
public class ProductCommConfigController {

    @Resource
    private ProductCommConfigService service;

    @PreAuthorize("hasAuthority('agent:platform:product:comm:config:page')")
    @GetMapping("/page")
    @ApiOperation("商品佣金配置")
    public CommonResult<CommonPage<ProductCommConfig>> getList(PageParamRequest pageParamRequest) {
        PageInfo<ProductCommConfig> page = service.pageList(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:comm:config:open')")
    @GetMapping("/open")
    @ApiOperation("商品佣金开启")
    public CommonResult<Boolean> open(Integer type) {
        return CommonResult.success(service.open(type));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:comm:config:close')")
    @GetMapping("/close")
    @ApiOperation("商品佣金关闭")
    public CommonResult<Boolean> close(Integer type) {
        return CommonResult.success(service.close(type));
    }


    @GetMapping("/list")
    @ApiOperation("商品佣金列表")
    public CommonResult<List<ProductCommConfig>> list() {
        return CommonResult.success( service.list(new QueryWrapper<ProductCommConfig>().lambda().eq(ProductCommConfig::getIfOpen, true)));
    }

}