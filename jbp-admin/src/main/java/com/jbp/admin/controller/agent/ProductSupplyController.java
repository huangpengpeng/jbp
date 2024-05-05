package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.ProductSupply;
import com.jbp.common.request.agent.ProductSupplyAddRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.ProductSupplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/agent/product/supply")
@Api(tags = "供货商管理")
public class ProductSupplyController {

    @Autowired
    private ProductSupplyService productSupplyService;

    @GetMapping("/list")
    @ApiOperation("供应商列表")
    public CommonResult<List<ProductSupply>> getSupplyList() {
        return CommonResult.success(productSupplyService.list());
    }

    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult add(@RequestBody @Validated ProductSupplyAddRequest request) {
        return CommonResult.success(productSupplyService.add(request));
    }

}
