package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.request.agent.ProductCommRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
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
@RequestMapping("api/admin/agent/platform/product/comm")
@Api(tags = "商品佣金")
public class ProductCommController {

    @Resource
    private ProductCommService service;
    @Resource
    private ProductCommConfigService configService;

    @GetMapping("/detail")
    @ApiOperation("商品佣金详情")
    public CommonResult<List<ProductCommConfig>> detail(Integer productId) {
        List<ProductCommConfig> configList = configService.getOpenList();
        List<ProductComm> list = service.getByProduct(productId);
        Map<Integer, ProductComm> map = FunctionUtil.keyValueMap(list, ProductComm::getType);
        for (ProductCommConfig config : configList) {
            ProductComm productComm = map.get(config.getType());
            config.setStatus(productComm != null ? productComm.getStatus() : false);
            config.setRule(productComm != null ? productComm.getRule() : null);
        }
        return CommonResult.success(configList);
    }

    @PreAuthorize("hasAuthority('agent:platform:product:comm:edit')")
    @PostMapping("/edit")
    @ApiOperation("商品佣金编辑")
    public CommonResult<Boolean> edit(@RequestBody @Validated ProductCommRequest request) {
        ProductCommConfig config = configService.getByType(request.getType());
        if (config == null || BooleanUtils.isNotTrue(config.getIfOpen())) {
            throw new CrmebException("当前佣金未开启请联系管理员");
        }
        ProductComm productComm = new ProductComm(request.getProductId(),
                config.getType(), config.getName(), request.getScale(), request.getRule(), request.getStatus());
        service.edit(productComm);
        return CommonResult.success();
    }
}
