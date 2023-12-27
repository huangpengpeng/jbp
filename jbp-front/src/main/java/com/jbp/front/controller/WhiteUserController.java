package com.jbp.front.controller;

import com.jbp.common.model.express.UserWhiteExpress;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WhiteUserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/front/userWhite")
@Api(tags = "用户白名单")
public class WhiteUserController {
    @Autowired
    WhiteUserService whiteUserService;

    @GetMapping("/page/list")

    @ApiOperation("用户白名单列表")
    public CommonResult<CommonPage<WhiteUser>> getList(@ModelAttribute @Validated WhiteUserRequest request, @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(whiteUserService.pageList(request, pageParamRequest)));
    }

    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult<WhiteUser> addUserWhite(@RequestBody WhiteUserRequest userWhiteRequest) {
        if (whiteUserService.add(userWhiteRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();

    }

    @DeleteMapping(value = "/batchDelete",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量删除",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult batchDeletion(@RequestBody @ApiParam("用户名单id") List<Integer> id) {
        if (whiteUserService.removeByIds(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PostMapping( value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导入新增",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult importUserWhite(@RequestBody List<UserWhiteExpress> userWhiteExpresses){
        if (whiteUserService.batchSave(userWhiteExpresses)) {
            return CommonResult.success();
        }
       return CommonResult.failed();

    }
}
