package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserSkin;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserSkinSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserSkinService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/agent/user/skin")
@Api(tags = "皮肤检测报告管理")
public class UserSkinController {

    @Autowired
    private UserSkinService userSkinService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:skin:page')")
    @GetMapping("/list")
    @ApiOperation("皮肤检测报告管理分页列表")
    public CommonResult<CommonPage<UserSkin>> page(UserSkinSearchRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userSkinService.getList(uid, request.getNickname(), request.getPhone(), request.getStartCreateTime(), request.getEndCreateTime(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:skin:export')")
    @ApiOperation(value = "皮肤检测报告管理列表Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<String> exportProductStatement(UserSkinSearchRequest request) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(userSkinService.export(uid, request.getNickname(), request.getPhone(), request.getStartCreateTime(), request.getEndCreateTime()));
    }
}
