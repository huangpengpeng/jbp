package com.jbp.front.controller;

import com.jbp.common.dto.UserWhiteDto;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.WhiteUserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/front/white/user")
@Api(tags = "用户白名单")
public class WhiteUserController {
    @Autowired
    private WhiteUserService whiteUserService;

    @GetMapping("/page/list")
    @ApiOperation("用户白名单列表")
    public CommonResult<CommonPage<WhiteUser>> getList(@ModelAttribute @Validated WhiteUserRequest request,
                                                       @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(whiteUserService.pageList(request, pageParamRequest)));
    }

    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult<WhiteUser> addUserWhite(@RequestBody WhiteUserRequest userWhiteRequest) {
        whiteUserService.add(userWhiteRequest);
        return CommonResult.success();

    }

    @DeleteMapping(value = "/batchDelete",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量删除",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult batchDelete(@RequestBody @ApiParam("用户名单id") List<Integer> id) {
        if (CollectionUtils.isEmpty(id)) {
            return CommonResult.failed("参数不能为空");
        }
        whiteUserService.removeByIds(id);
        return CommonResult.success();
    }

    @PostMapping( value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导入新增",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult importUserWhite(@RequestBody List<UserWhiteDto> userWhiteList) {
        whiteUserService.batchSave(userWhiteList);
        return CommonResult.success();
    }
}
