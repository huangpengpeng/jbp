package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.PlatformWalletFlow;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ProductProfitConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/platform/product/profit/config")
@Api(tags = "商品配套配置")
public class ProductProfitConfigController {

    @Resource
    private ProductProfitConfigService service;

    @PreAuthorize("hasAuthority('agent:platform:product:profit:config:page')")
    @GetMapping("/page")
    @ApiOperation("商品权益配置")
    public CommonResult<CommonPage<ProductProfitConfig>> getList(PageParamRequest pageParamRequest) {
        PageInfo<ProductProfitConfig> page = service.pageList(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:profit:config:open')")
    @GetMapping("/open")
    @ApiOperation("商品权益开启")
    public CommonResult<Boolean> open(Integer type) {
        return CommonResult.success(service.open(type));
    }

    @PreAuthorize("hasAuthority('agent:platform:product:profit:config:close')")
    @GetMapping("/close")
    @ApiOperation("商品权益关闭")
    public CommonResult<Boolean> close(Integer type) {
        return CommonResult.success(service.close(type));
    }


    @GetMapping("/list")
    @ApiOperation("商品权益列表")
    public CommonResult<List<ProductProfitConfig>> list() {
        return CommonResult.success( service.list(new QueryWrapper<ProductProfitConfig>().lambda().eq(ProductProfitConfig::getIfOpen, true)));
    }

}
