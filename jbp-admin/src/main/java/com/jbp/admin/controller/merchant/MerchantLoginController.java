package com.jbp.admin.controller.merchant;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.admin.SystemAdminRef;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LoginUserVo;
import com.jbp.service.service.SystemAdminRefService;
import com.jbp.service.service.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jbp.admin.service.AdminLoginService;
import com.jbp.common.request.LoginAdminUpdateRequest;
import com.jbp.common.request.SendCodeRequest;
import com.jbp.common.request.SystemAdminLoginRequest;
import com.jbp.common.request.merchant.MerchantSendCodeRequest;
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
 * 商户端登录控制器
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
@RequestMapping("api/admin/merchant")
@Api(tags = "商户端登录控制器")
public class MerchantLoginController {

    @Autowired
    private AdminLoginService loginService;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private SystemAdminRefService systemAdminRefService;

    @ApiOperation(value="登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> login(@RequestBody @Validated SystemAdminLoginRequest systemAdminLoginRequest, HttpServletRequest request) {
        String ip = CrmebUtil.getClientIp(request);
        SystemLoginResponse systemAdminResponse = loginService.merchantLogin(systemAdminLoginRequest, ip);
        return CommonResult.success(systemAdminResponse);
    }


    @ApiOperation(value="登录")
    @RequestMapping(value = "/login2", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> login(Integer sId, HttpServletRequest request) {
        String ip = CrmebUtil.getClientIp(request);
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        if (loginUserVo == null || loginUserVo.getUser() == null) {
            throw new RuntimeException("当前账户未登录");
        }
        SystemAdmin user = loginUserVo.getUser();
        user = systemAdminService.getById(user.getId());
        List<SystemAdminRef> list = systemAdminRefService.getList(user.getMerId());
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("未设置关联关系");
        }
        Boolean isRef = false;
        for (SystemAdminRef systemAdminRef : list) {
            if (systemAdminRef.getSId().intValue() == sId.intValue()) {
                isRef = true;
                break;
            }
        }
        if (!isRef) {
            throw new RuntimeException("未设置关联关系2");
        }

        user.setMerId(sId);
        systemAdminService.updateById(user);

        SystemLoginResponse systemAdminResponse = loginService.merchantLogin(user.getId(), ip);
        return CommonResult.success(systemAdminResponse);
    }

    @PreAuthorize("hasAuthority('merchant:logout')")
    @ApiOperation(value="登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> SystemAdminLogout() {
        loginService.logout();
        return CommonResult.success("logout success");
    }

    @PreAuthorize("hasAuthority('merchant:login:user:info')")
    @ApiOperation(value="获取登录用户详情")
    @RequestMapping(value = "/getAdminInfoByToken", method = RequestMethod.GET)
    public CommonResult<LoginAdminResponse> getAdminInfo() {
        return CommonResult.success(loginService.getInfoByToken());
    }

    @ApiOperation(value = "获取登录页图片")
    @RequestMapping(value = "/getLoginPic", method = RequestMethod.GET)
    public CommonResult<AdminLoginInfoResponse> getLoginPic() {
        return CommonResult.success(loginService.getMerchantLoginInfo());
    }

    @PreAuthorize("hasAuthority('merchant:login:menus')")
    @ApiOperation(value = "获取管理员可访问目录")
    @RequestMapping(value = "/getMenus", method = RequestMethod.GET)
    public CommonResult<List<MenusResponse>> getMenus() {
        return CommonResult.success(loginService.getMenus());
    }

    @ApiOperation(value = "发送短信登录验证码")
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    public CommonResult<String> sendCode(@RequestBody @Validated MerchantSendCodeRequest request) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getPhone, request.getPhone());
        SystemAdmin systemAdmin = systemAdminService.getOne(lqw);
        if (systemAdmin == null) {
            return CommonResult.success("手机号不存在");
        }
        if (loginService.sendLoginCode(request.getPhone())) {
            return CommonResult.success("发送成功");
        }
        return CommonResult.failed("发送失败");
    }
    
    @PreAuthorize("hasAuthority('merchant:login:admin:update')")
    @ApiOperation(value="修改登录用户信息")
    @RequestMapping(value = "/login/admin/update", method = RequestMethod.POST)
    public CommonResult<SystemLoginResponse> loginAdminUpdate(@RequestBody @Validated LoginAdminUpdateRequest request) {
        if (loginService.loginAdminUpdate(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
