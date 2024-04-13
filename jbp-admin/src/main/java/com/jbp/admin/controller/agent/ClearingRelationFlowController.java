package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingRelationFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserInvitationFlowRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingRelationFlowService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/clearing/relation/flow")
@Api(tags = "结算管理")
public class ClearingRelationFlowController {

    @Resource
    private UserService userService;
    @Resource
    private ClearingRelationFlowService service;

    @GetMapping("/page")
    @ApiOperation("结算服务关系")
    public CommonResult<CommonPage<ClearingRelationFlow>> getList(UserInvitationFlowRequest request, Long clearingId, Integer level,
                                                                  PageParamRequest pageParamRequest) {
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
                throw new CrmebException("邀请上级账号错误");
            }
            pid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(service.pageList(uid, pid, clearingId, level, pageParamRequest)));

    }
}
