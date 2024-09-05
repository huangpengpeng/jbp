package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserScoreEditRequest;
import com.jbp.common.request.agent.UserScoreSearchRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserScoreService;
import com.jbp.service.service.UserService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/user/score")
@Api(tags = "用户分数管理")
public class UserScoreController {

    @Autowired
    private UserScoreService userScoreService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:score:page')")
    @GetMapping("/list")
    @ApiOperation("用户分数管理分页列表")
    public CommonResult<CommonPage<UserScore>> page(UserScoreSearchRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userScoreService.getList(uid, request.getNickname(), request.getPhone(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:score:edit')")
    @PostMapping("/edit")
    @ApiOperation("修改分数")
    public CommonResult<Boolean> edit(@RequestBody UserScoreEditRequest request) {
        return CommonResult.success(userScoreService.edit(request));
    }

    @PreAuthorize("hasAuthority('agent:user:score:export')")
    @ApiOperation(value = "导出用户分数管理列表Excel")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public CommonResult<String> exportProductStatement(UserScoreSearchRequest request) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (ObjectUtil.isNull(user)) {
                return CommonResult.failed("账户信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(userScoreService.export(uid, request.getNickname(), request.getPhone()));
    }
}
