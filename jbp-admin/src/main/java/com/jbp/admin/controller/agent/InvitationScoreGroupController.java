package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScoreGroup;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.InvitationScoreGroupRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/invitation/score/group")
@Api(tags = "销售业绩分组")
public class InvitationScoreGroupController {
    @Resource
    private InvitationScoreGroupService invitationScoreGroupService;
    @Resource
    private UserService userService;

    @GetMapping("/page")
    @ApiOperation("销售业绩分组列表")
    public CommonResult<CommonPage<InvitationScoreGroup>> getList(InvitationScoreGroupRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(invitationScoreGroupService.pageList(uid, request.getGroupName(), request.getAction(), pageParamRequest)));
    }
}
