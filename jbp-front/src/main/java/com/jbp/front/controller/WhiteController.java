package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.model.user.White;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.CouponFrontSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteRequest;
import com.jbp.common.response.CouponFrontResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WhiteServicel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/front/white")
@Api(tags = "白名单管理")
public class WhiteController {
    @Autowired
    WhiteServicel whiteServicel;


    @ApiOperation("新增白名单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult addWhiten(@RequestBody WhiteRequest white) {
        if (whiteServicel.addWhiten(white)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/page/list", method = RequestMethod.GET)
    @ApiOperation("白名单列表")
    public CommonResult<CommonPage<White>> getList(@ModelAttribute @Validated WhiteRequest request,
                                                   @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(whiteServicel.pageList(request, pageParamRequest)));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation("指定白名单删除")
    public CommonResult cascadingDelte(@PathVariable("id") Long id) {
        if (whiteServicel.cascadingDelte(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
