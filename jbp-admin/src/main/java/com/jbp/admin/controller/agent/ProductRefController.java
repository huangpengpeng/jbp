package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.model.product.ProductRef;
import com.jbp.common.request.agent.ProductRefRequest;
import com.jbp.common.request.agent.ProductRefSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ProductRefService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/platform/product/ref")
@Api(tags = "商品关联套组管理")
public class ProductRefController {

    @Autowired
    private ProductRefService productRefService;

    @PreAuthorize("hasAuthority('agent:platform:product:ref:page')")
    @GetMapping("/list")
    @ApiOperation("商品关联套组列表")
    public CommonResult<List<ProductRef>> list(ProductRefSearchRequest request){
       return CommonResult.success(productRefService.getList(request.getProductId()));
    }


    @PreAuthorize("hasAuthority('agent:platform:product:ref:add')")
    @PostMapping("/add")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "商品配套编辑")
    @ApiOperation("商品关联套组增加")
    public CommonResult<Boolean> add(@RequestBody @Validated ProductRefRequest request){
        return CommonResult.success(productRefService.add(request));
    }

}
