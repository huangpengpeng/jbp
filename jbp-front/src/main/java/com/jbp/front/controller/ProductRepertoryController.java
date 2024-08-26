package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.product.ProductRepertoryFlow;
import com.jbp.common.request.ProductRepertoryRequest;
import com.jbp.common.request.UserScoreRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.vo.ProductRepertoryVo;
import com.jbp.service.service.ProductRepertoryFlowService;
import com.jbp.service.service.ProductRepertoryService;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/front/product/repertory")
@Api(tags = "用户分数控制器")
public class ProductRepertoryController {

    @Autowired
    private ProductRepertoryService productRepertoryService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepertoryFlowService productRepertoryFlowService;


    @ApiOperation(value = "获取当前库存列表")
    @RequestMapping(value = "/getRepertory", method = RequestMethod.GET)
    public CommonResult<List<ProductRepertoryVo>> getUserScore() {
        return CommonResult.success(productRepertoryService.getProductList(userService.getUserId()));
    }

    @ApiOperation(value = "获取当前库存详情")
    @RequestMapping(value = "/updateUserCapa", method = RequestMethod.GET)
    public CommonResult<List<ProductRepertoryFlow>> password(Integer productId) {
        List<ProductRepertoryFlow> productRepertoryFlows = productRepertoryFlowService.list(new QueryWrapper<ProductRepertoryFlow>().lambda().eq(ProductRepertoryFlow::getProductId, productId));
        return CommonResult.success(productRepertoryFlows);
    }


    @ApiOperation(value = "调拨库存")
    @RequestMapping(value = "/updateRepertory", method = RequestMethod.POST)
    public CommonResult<Boolean> updateRepertory(@RequestBody @Validated List<ProductRepertoryRequest> request) {

        productRepertoryService.allot(request);
        return CommonResult.success();
    }

}



