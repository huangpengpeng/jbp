package com.jbp.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.product.ProductExtConfig;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.CouponProductSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.ProductFrontSearchRequest;
import com.jbp.common.request.SystemCouponProductSearchRequest;
import com.jbp.common.request.merchant.MerchantProductSearchRequest;
import com.jbp.common.response.*;
import com.jbp.common.result.CommonResult;
import com.jbp.front.service.FrontProductService;
import com.jbp.service.service.ProductExtConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaXsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/front/product/ext/config")
@Api(tags = "商品扩展控制器")
public class ProductExtConfigController {

    @Autowired
    private ProductExtConfigService productExtConfigService;


    @ApiOperation(value = "商品分页列表前置信息")
    @RequestMapping(value = "/list/before", method = RequestMethod.GET)
    public CommonResult<List<ProductExtConfig>> getListBefore(Integer productId) {
        List<ProductExtConfig> list = productExtConfigService.list(new QueryWrapper<ProductExtConfig>().lambda().eq(ProductExtConfig::getProductId, productId));
        return CommonResult.success(list);
    }

}



