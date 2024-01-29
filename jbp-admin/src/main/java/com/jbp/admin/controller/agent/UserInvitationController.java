package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/company")
@Api(tags = "销售上下级关系")
public class UserInvitationController {
    @Resource
    UserInvitationService userInvitationService;
    @Resource
    private UserService userService;

    @GetMapping("/page")
    @ApiOperation("销售上下级关系列表")
    public CommonResult<CommonPage<UserInvitation>> getlist(UserInvitationRequest request, PageParamRequest pageParamRequest) {
        //用户ID账号
        User uUser = userService.getByAccount(request.getUAccount());
        if (ObjectUtil.isNull(uUser)) {
            if (ObjectUtil.isNull(request.getUAccount()) || request.getUAccount().equals("")) {
                uUser = new User();
            } else {
                return CommonResult.failed("账户信息错误");
            }
        }
//        邀请上级账号
        User pUser = userService.getByAccount(request.getPAccount());
        if (ObjectUtil.isNull(pUser)) {
            if (ObjectUtil.isNull(request.getPAccount()) || request.getPAccount().equals("")) {
                pUser = new User();
            } else {
                return CommonResult.failed("邀请上级账户信息错误");
            }
        }
//        转挂上级账号
        User mUser = userService.getByAccount(request.getMAccount());
        if (ObjectUtil.isNull(mUser)) {
            if (ObjectUtil.isNull(request.getMAccount()) || request.getMAccount().equals("")) {
                mUser = new User();
            } else {
                return CommonResult.failed("账户信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userInvitationService.pageList(uUser.getId(), pUser.getId(), mUser.getId(), pageParamRequest)));
    }
}
