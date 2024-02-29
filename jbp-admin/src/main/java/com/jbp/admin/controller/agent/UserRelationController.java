package com.jbp.admin.controller.agent;

import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserRelationRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserRelationService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/relation")
@Api(tags = "服务关系上下级")
public class UserRelationController {
    @Resource
    private UserRelationService userRelationService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:relation:page')")
    @GetMapping("/page")
    @ApiOperation("服务关系上下级列表")
    public CommonResult<CommonPage<UserRelation>> pageList(UserRelationRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getUAccount())) {
            User user = userService.getByAccount(request.getUAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
//        邀请上级账号
        Integer pid = null;
        if (StringUtils.isNotEmpty(request.getPAccount())) {
            User user = userService.getByAccount(request.getPAccount());
            if (user == null) {
                throw new CrmebException("邀请上级账号信息错误");
            }
            pid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userRelationService.pageList(uid, pid, request.getNode(), pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('agent:user:relation:band')")
    @GetMapping("/band")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "服务关系上下级绑定上级")
    @ApiOperation("绑定上级")
    public CommonResult band(UserRelationRequest request) {
        if (StringUtils.isAnyEmpty(request.getUAccount(), request.getPAccount()) || request.getNode() == null) {
            throw new CrmebException("账户信息不能为空");
        }
        User user = userService.getByAccount(request.getUAccount());
        if (user == null) {
            throw new CrmebException("账户不存在");
        }
        User pUser = userService.getByAccount(request.getPAccount());
        if (pUser == null) {
            throw new CrmebException("上级账户不存在");
        }
        userRelationService.band(user.getId(), pUser.getId(), null, request.getNode());
        return CommonResult.success();
    }


}
