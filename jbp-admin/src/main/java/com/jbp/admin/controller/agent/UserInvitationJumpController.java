package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationJumpRequest;
import com.jbp.common.response.UserInvitationJumpListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/invitation/Jump")
@Api(tags = "销售上下层级跳转关系")
public class UserInvitationJumpController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserInvitationJumpService userInvitationJumpService;

    @PreAuthorize("hasAuthority('agent:user:invitation:jump:page')")
    @GetMapping("/page")
    @ApiOperation("销售上下层级跳转关系列表")
    public CommonResult<CommonPage<UserInvitationJumpListResponse>> pageList(UserInvitationJumpRequest request, PageParamRequest pageParamRequest) {
        //当前用户id
        Integer uId = null;
        if (request.getUId() != null) {
            User user = userService.getById(request.getUId());
            if (user == null) {
                throw new CrmebException("当前id信息错误");
            }
            uId = user.getId();
        }
//        当前上级id
        Integer pId = null;
        if (request.getPId() != null) {
            User user = userService.getById(request.getPId());
            if (user == null) {
                throw new CrmebException("当前上级id信息错误");
            }
            pId = user.getId();
        }
        //原上级id
        Integer orgPid = null;
        if (request.getOrgPid() != null) {
            User user = userService.getById(request.getOrgPid());
            if (user == null) {
                throw new CrmebException("原上级id信息错误");
            }
            orgPid = user.getId();
        }

        return CommonResult.success(CommonPage.restPage(userInvitationJumpService.pageList(uId, pId, orgPid, pageParamRequest)));
    }

}
