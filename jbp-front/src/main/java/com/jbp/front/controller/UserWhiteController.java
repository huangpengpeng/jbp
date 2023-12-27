package com.jbp.front.controller;

import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserWhiteRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserWhiteService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/front/userWhite")
@Api(tags = "用户白名单")
public class UserWhiteController {
    @Autowired
    UserWhiteService userWhiteService;

    @GetMapping("/page/list")

    @ApiOperation("用户白名单列表")
    public CommonResult<CommonPage<WhiteUser>> getList(@ModelAttribute @Validated UserWhiteRequest request, @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userWhiteService.pageList(request, pageParamRequest)));
    }

    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult<WhiteUser> addUserWhite(@RequestBody UserWhiteRequest userWhiteRequest) {
        return CommonResult.success(userWhiteService.add(userWhiteRequest));
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除")
    public CommonResult batchDeletion(@RequestBody @ApiParam("用户名单id") List<Long> id) {
        if (userWhiteService.batch(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PostMapping( value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导入新增",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<WhiteUser>> importUserWhite(@RequestBody List<UserWhiteExpress> userWhiteExpresses){
       return CommonResult.success( userWhiteService.importUserWhite(userWhiteExpresses));

    }
}
