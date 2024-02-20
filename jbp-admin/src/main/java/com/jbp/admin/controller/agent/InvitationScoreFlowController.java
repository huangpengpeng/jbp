package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.InvitationScoreFlowRequest;
import com.jbp.common.request.agent.InvitationScoreRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/invitation/score/flow")
@Api(tags ="销售业绩明细")
public class InvitationScoreFlowController {
    @Resource
    private InvitationScoreFlowService invitationScoreFlowService;
    @Resource
    private UserService userService;

    @GetMapping("/page")
    @ApiOperation("销售业绩明细列表")
    public CommonResult<CommonPage<InvitationScoreFlow>> getList(InvitationScoreFlowRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        Integer orderuid = null;
        if (ObjectUtil.isNull(request.getOrderAccount()) || !request.getOrderAccount().equals("")) {
            try {
                orderuid = userService.getByAccount(request.getOrderAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("下单用户账号信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(invitationScoreFlowService.pageList(uid,orderuid,request.getAction(),pageParamRequest)));
    }


}
