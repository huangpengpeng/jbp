package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationFlowRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/invitation/flow")
@Api(tags = "销售上下层级关系")
public class UserInvitationFlowController {
    @Resource
    private UserInvitationFlowService userInvitationFlowService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:user:invitation:flow:page')")
    @GetMapping("/page")
    @ApiOperation("销售上下层级关系列表")
    public CommonResult<CommonPage<UserInvitationFlow>> pageList(UserInvitationFlowRequest request, PageParamRequest pageParamRequest) {
        //用户ID账号
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
        return CommonResult.success(CommonPage.restPage(userInvitationFlowService.pageList(uid, pid, request.getLevel(), pageParamRequest)));
    }
}
