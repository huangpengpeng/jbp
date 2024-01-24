package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.request.agent.ProductProfitRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/agent/platform/product/profit")
@Api(tags = "商品配套")
public class ProductProfitController {

    @Resource
    private ProductProfitService service;
    @Resource
    private ProductProfitConfigService configService;

    @GetMapping("/detail")
    @ApiOperation("商品配套详情")
    public CommonResult<List<ProductProfitConfig>> detail(Integer productId) {
        List<ProductProfitConfig> configList = configService.getOpenList();
        List<ProductProfit> list = service.getByProduct(productId);
        Map<Integer, ProductProfit> map = FunctionUtil.keyValueMap(list, ProductProfit::getType);
        for (ProductProfitConfig config : configList) {
            ProductProfit productProfit = map.get(config.getType());
            config.setStatus(productProfit != null ? productProfit.getStatus() : false);
            config.setRule(productProfit != null ? productProfit.getRule() : null);
        }
        return CommonResult.success(configList);
    }

    @PreAuthorize("hasAuthority('agent:platform:product:profit:edit')")
    @PostMapping("/edit")
    @ApiOperation("商品配套编辑")
    public CommonResult<Boolean> edit(@RequestBody @Validated ProductProfitRequest request) {
        ProductProfitConfig profitConfig = configService.getByType(request.getType());
        if (profitConfig == null || BooleanUtils.isNotTrue(profitConfig.getIfOpen())) {
            throw new CrmebException("当前配套未开启请联系管理员");
        }
        ProductProfit productProfit = new ProductProfit(request.getProductId(),
                profitConfig.getType(), profitConfig.getName(), request.getRule(), request.getStatus());
        service.edit(productProfit);
        return CommonResult.success();
    }
}
