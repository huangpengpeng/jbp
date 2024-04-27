package com.jbp.admin.controller.platform;


import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.UserAdminDetailResponse;
import com.jbp.common.response.UserResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.UUID;

import java.util.List;

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

    @Autowired
    private UserCapaXsService userCapaXsService;

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
    @PreAuthorize("hasAuthority('platform:user:phone')")
    @GetMapping("/phone")
    @ApiOperation(value = "获取手机号")
    public CommonResult<String> getPhone(String account) {
        return CommonResult.success(userService.getPhone(account));
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
        User users = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNotEmpty(users)){
            throw new CrmebException("账号已存在");
        }
        userService.registerPhone(request.getUsername(),request.getPhone(),request.getAccount(),
                request.getUserCapaTemplateRequest(),request.getRegionPAccount(),request.getRegionPNode(),
                request.getInvitationPAccount(),request.getPwd());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('platform:user:update:user')")
    @PostMapping("/update/user")
    @ApiOperation("修改用户基本信息")
    public CommonResult updateUser(@RequestBody  PlatformUpdateUserRequest request) {
        userService.updateUser(request.getId(), request.getPwd(), request.getSex(), request.getNickname(), request.getPhone(), request.getCountry(), request.getProvince(), request.getCity(), request.getDistrict(), request.getAddress(), request.getPayPwd(),request.getOpenShop());
        return CommonResult.success();
    }






    @PreAuthorize("hasAuthority('platform:user:update:saveVitalityXscapeUser')")
    @ApiOperation(value = "增加合伙人账号", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/saveVitalityXscapeUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult saveVitalityXscapeUser( String numberCode) {

        User user = userService.getByAccount(numberCode);
        if (user == null) {
            throw new RuntimeException("用户编号错误");
        }


        for (int i = 0; i < 3; i++) {
            //增加3个1星账号
            String userName = user.getNickname() + "_" + i;
            String number = 8 + RandomStringUtils.random(4, "0123456789");

            User user1 = userService.registerPhone(userName, number, user.getId());
            UserCapaXs userCapaXs = new UserCapaXs();
            userCapaXs.setUid(user1.getId());
            userCapaXs.setCapaId(1L);
            userCapaXsService.save(userCapaXs);

            String userName2 = user1.getNickname() + "_" + i;
            String number2 = 8 + RandomStringUtils.random(4, "0123456789");
            User user2 = userService.registerPhone(userName2, number2, user1.getId());

            UserCapaXs userCapaXs2 = new UserCapaXs();
            userCapaXs2.setUid(user2.getId());
            userCapaXs2.setCapaId(1L);
            userCapaXsService.save(userCapaXs2);

        }
        return CommonResult.success();
    }



    @PreAuthorize("hasAuthority('platform:user:import:user')")
    @PostMapping("/import")
    @ApiOperation("用户导入")
    public CommonResult importUser(@RequestBody @Validated List<UserImportRequest> request) {
        userService.importUser(request);
        return CommonResult.success();
    }

}



