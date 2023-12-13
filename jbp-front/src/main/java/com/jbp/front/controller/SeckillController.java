package com.jbp.front.controller;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.SeckillProductFrontSearchRequest;
import com.jbp.common.response.SeckillFrontTimeResponse;
import com.jbp.common.response.SeckillProductFrontResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.front.service.SeckillService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 秒杀控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/Seckill")
@Api(tags = "秒杀控制器")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @ApiOperation(value = "秒杀时段信息")
    @RequestMapping(value = "/activity/time/info", method = RequestMethod.GET)
    public CommonResult<List<SeckillFrontTimeResponse>> activityTimeInfo() {
        return CommonResult.success(seckillService.activityTimeInfo());
    }

    @ApiOperation(value = "秒杀商品列表")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SeckillProductFrontResponse>> getProductList(@ModelAttribute @Validated SeckillProductFrontSearchRequest request,
                                                                                @ModelAttribute PageParamRequest pageRequest) {
        return CommonResult.success(CommonPage.restPage(seckillService.getProductList(request, pageRequest)));
    }
}



