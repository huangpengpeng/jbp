package com.jbp.admin.controller.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jbp.common.annotation.LogControllerAnnotation;
import com.jbp.common.enums.MethodType;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/user/invitation")
@Api(tags = "销售上下级关系")
public class UserInvitationController {
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserService userService;

    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserInvitationFlowService userInvitationFlowService;


    @PreAuthorize("hasAuthority('agent:user:invitation:page')")
    @GetMapping("/page")
    @ApiOperation("销售上下级关系列表")
    public CommonResult<CommonPage<UserInvitation>> getlist(UserInvitationRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getUAccount())) {
            User user = userService.getByAccount(request.getUAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }

        Integer pid = null;
        if (StringUtils.isNotEmpty(request.getPAccount())) {
            User user = userService.getByAccount(request.getPAccount());
            if (user == null) {
                throw new CrmebException("邀请上级账号信息错误");
            }
            pid = user.getId();
        }
        Integer mid = null;
        if (StringUtils.isNotEmpty(request.getMAccount())) {
            User user = userService.getByAccount(request.getMAccount());
            if (user == null) {
                throw new CrmebException("转挂上级账号信息错误");
            }
            mid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userInvitationService.pageList(uid, pid, mid, pageParamRequest)));
    }


    @PreAuthorize("hasAuthority('agent:user:invitation:band')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "销售关系上下级绑定上级")
    @GetMapping("/band")
    @ApiOperation("绑定上级")
    public CommonResult band(UserInvitationRequest request) {
        if (StringUtils.isAnyEmpty(request.getUAccount(), request.getPAccount())) {
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
        userInvitationService.band(user.getId(), pUser.getId(), false, true, true);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:user:invitation:mount')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "销售关系上下级转挂上级")
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

       TeamUser teamUser = teamUserService.getByUser(user.getId());
        TeamUser teamUser2 = teamUserService.getByUser(pUser.getId());
        if(teamUser != null && teamUser.getTid() != teamUser2.getTid()){
            throw new CrmebException("团队信息不一致");
        }

        Integer pid =  userInvitationService.getPid(user.getId());
        userInvitationService.hasChild( pUser.getId(),pid);

        userInvitationService.band(user.getId(), pUser.getId(), true, true, true);

        return CommonResult.success();
    }


    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "销售关系上下级删除上级")
    @GetMapping("/del")
    @ApiOperation("删除")
    public CommonResult band(Integer uid) {
        if (uid == null) {
            throw new CrmebException("账户信息不能为空");
        }
        User user = userService.getById(uid);
        if (user == null) {
            throw new CrmebException("账户不存在");
        }
        userInvitationService.del(user.getId());
        return CommonResult.success();
    }





    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "销售关系上下级删除挂载上级")
    @GetMapping("/delmId")
    @ApiOperation("删除")
    public CommonResult delmId(Integer uid) {
        if (uid == null) {
            throw new CrmebException("账户信息不能为空");
        }
        User user = userService.getById(uid);
        if (user == null) {
            throw new CrmebException("账户不存在");
        }
        UpdateWrapper<UserInvitation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("mid", null);
        userInvitationService.update(null, updateWrapper);
        return CommonResult.success();
    }
}
