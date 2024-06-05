package com.jbp.admin.controller.platform;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jbp.admin.service.AdminLoginService;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.request.LoginAdminUpdateRequest;
import com.jbp.common.request.SystemAdminLoginRequest;
import com.jbp.common.response.AdminLoginInfoResponse;
import com.jbp.common.response.LoginAdminResponse;
import com.jbp.common.response.MenusResponse;
import com.jbp.common.response.SystemLoginResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台端登录控制器
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
@RequestMapping("api/admin/platform")
@Api(tags = "平台端登录控制器")
public class PlatformLoginController {

    @Autowired
    private AdminLoginService loginService;

    @ApiOperation(value="登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> systemAdminLogin(@RequestBody @Validated SystemAdminLoginRequest systemAdminLoginRequest, HttpServletRequest request) {
        String ip = CrmebUtil.getClientIp(request);
        SystemLoginResponse systemAdminResponse = loginService.platformLogin(systemAdminLoginRequest, ip);
        return CommonResult.success(systemAdminResponse);
    }

    @PreAuthorize("hasAuthority('platform:logout')")
    @ApiOperation(value="登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<Object> systemAdminLogout() {
        loginService.logout();
        return CommonResult.success("logout success");
    }

    @PreAuthorize("hasAuthority('platform:login:user:info')")
    @ApiOperation(value="获取登录用户详情")
    @RequestMapping(value = "/getAdminInfoByToken", method = RequestMethod.GET)
    public CommonResult<LoginAdminResponse> getAdminInfo() {
        return CommonResult.success(loginService.getInfoByToken());
    }

    @ApiOperation(value = "获取登录页图片")
    @RequestMapping(value = "/getLoginPic", method = RequestMethod.GET)
    public CommonResult<AdminLoginInfoResponse> getLoginPic() {
        return CommonResult.success(loginService.getLoginInfo());
    }

    @EncryptIgnore
    @PreAuthorize("hasAuthority('platform:login:menus')")
    @ApiOperation(value = "获取管理员可访问目录")
    @RequestMapping(value = "/getMenus", method = RequestMethod.GET)
    public CommonResult<List<MenusResponse>> getMenus() {
        return CommonResult.success(loginService.getMenus());
    }

	@PreAuthorize("hasAuthority('platform:login:admin:update')")
	@ApiOperation(value = "修改登录用户信息")
	@RequestMapping(value = "/login/admin/update", method = RequestMethod.POST)
	public CommonResult<SystemLoginResponse> loginAdminUpdate(@RequestBody @Validated LoginAdminUpdateRequest request) {
		if (loginService.loginAdminUpdate(request)) {
			return CommonResult.success();
		}
		return CommonResult.failed();
	}
    
	@PreAuthorize("hasAuthority('platform:login:admin:maf_update')")
	@ApiOperation(value = "修改登录用户信息")
	@RequestMapping(value = "/maf_update", method = RequestMethod.GET)
	public CommonResult<Object> loginUserMfaKey() {
		return CommonResult.success(loginService.loginUserMfaKey());
	}
}
