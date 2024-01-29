package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserRelationRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserRelationService;
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
        if (ObjectUtil.isNull(request.getUAccount()) || !request.getUAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getUAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
//        邀请上级账号
        Integer pid = null;
        if (ObjectUtil.isNull(request.getPAccount()) || !request.getPAccount().equals("")) {
            try {
                pid = userService.getByAccount(request.getPAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("邀请上级账号错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userRelationService.pageList(uid ,pid,request.getNode(), pageParamRequest)));
    }
}
