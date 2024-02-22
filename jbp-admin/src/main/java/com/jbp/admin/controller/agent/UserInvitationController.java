package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/invitation")
@Api(tags = "销售上下级关系")
public class UserInvitationController {
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:user:invitation:page')")
    @GetMapping("/page")
    @ApiOperation("销售上下级关系列表")
    public CommonResult<CommonPage<UserInvitation>> getlist(UserInvitationRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getUAccount()) || !request.getUAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getUAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        Integer pid = null;
        if (ObjectUtil.isNull(request.getPAccount()) || !request.getPAccount().equals("")) {
            try {
                pid = userService.getByAccount(request.getPAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("邀请上级账号错误");
            }
        }
        Integer mid = null;
        if (ObjectUtil.isNull(request.getMAccount()) || !request.getMAccount().equals("")) {
            try {
                mid = userService.getByAccount(request.getMAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("转挂上级账号账号错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userInvitationService.pageList(uid, pid, mid, pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('agent:user:invitation:band')")
    @GetMapping("/band")
    @ApiOperation("绑定上级")
    public CommonResult band(UserInvitationRequest request) {
        if (StringUtils.isAnyEmpty(request.getUAccount(), request.getPAccount())) {
            throw new CrmebException("账户信息不能为空");
        }
        User user = userService.getByAccount(request.getUAccount());
        if(user == null){
            throw new CrmebException("账户不存在");
        }
        User pUser = userService.getByAccount(request.getPAccount());
        if(pUser == null){
            throw new CrmebException("上级账户不存在");
        }
        userInvitationService.band(user.getId(), pUser.getId(), false, true);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:user:invitation:mount')")
    @GetMapping("/mount")
    @ApiOperation("转挂上级")
    public CommonResult mount(UserInvitationRequest request) {
        if (StringUtils.isAnyEmpty(request.getUAccount(), request.getMAccount())) {
            throw new CrmebException("账户信息不能为空");
        }
        User user = userService.getByAccount(request.getUAccount());
        if (user == null) {
            throw new CrmebException("账户不存在");
        }
        User pUser = userService.getByAccount(request.getMAccount());
        if (pUser == null) {
            throw new CrmebException("上级账户不存在");
        }
        userInvitationService.band(user.getId(), pUser.getId(), true, true);

        return CommonResult.success();
    }
}
