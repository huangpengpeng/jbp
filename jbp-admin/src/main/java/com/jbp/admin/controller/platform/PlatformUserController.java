package com.jbp.admin.controller.platform;


import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.UserAdminDetailResponse;
import com.jbp.common.response.UserResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端用户控制器
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
@RequestMapping("api/admin/platform/user")
@Api(tags = "平台端用户控制器")
@Validated
public class PlatformUserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('platform:user:page:list')")
    @ApiOperation(value = "平台端用户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(@ModelAttribute @Validated UserSearchRequest request,
                                                          @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getPlatformPage(request, pageParamRequest));
        return CommonResult.success(userCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:user:update')")
    @ApiOperation(value = "修改用户信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated UserUpdateRequest userRequest) {
        if (userService.updateUser(userRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:detail')")
    @ApiOperation(value = "用户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAdminDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userService.getAdminDetail(id));
    }

    @PreAuthorize("hasAuthority('platform:user:tag')")
    @ApiOperation(value = "用户分配标签")
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public CommonResult<String> tag(@RequestBody @Validated UserAssignTagRequest request) {
        if (userService.tag(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:integer')")
    @ApiOperation(value = "操作用户积分")
    @RequestMapping(value = "/operate/integer", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateIntegralRequest request) {
        if (userService.operateUserInteger(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:balance')")
    @ApiOperation(value = "操作用户余额")
    @RequestMapping(value = "/operate/balance", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateBalanceRequest request) {
        if (userService.operateUserBalance(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:register:phone')")
    @PostMapping("/register/phone")
    @ApiOperation("用户注册")
    public CommonResult<User> registerPhone(@Validated @RequestBody RegisterPhoneRequest request) {
        User user = userService.registerPhone(request.getUsername(), request.getPhone(), null);
        return CommonResult.success(user);
    }

    @PreAuthorize("hasAuthority('platform:user:update:user')")
    @PostMapping("/update/user")
    @ApiOperation("修改用户基本信息")
    public CommonResult updateUser(@RequestBody @Validated PlatformUpdateUserRequest request) {
        userService.updateUser(request.getId(), request.getPwd(), request.getSex(), request.getNickname(), request.getPhone(), request.getCountry(), request.getProvince(), request.getCity(), request.getDistrict(), request.getAddress(), request.getPayPwd());
        return CommonResult.success();
    }

}



