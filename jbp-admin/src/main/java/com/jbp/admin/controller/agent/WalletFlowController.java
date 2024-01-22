package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.WalletRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.WalletFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/wallet/flow")
@Api(tags = "用户积分详情")
public class WalletFlowController {
    @Resource
    private WalletFlowService walletFlowService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:user:wallet:flow')")
    @GetMapping("/page")
    @ApiOperation("用户积分详情")
    public CommonResult<CommonPage<WalletFlow>> getList(WalletRequest request, PageParamRequest pageParamRequest) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            if (ObjectUtil.isNull(request.getAccount()) || request.getAccount().equals("")) {
                user = new User();
            } else {
                return CommonResult.failed("账户信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(walletFlowService.pageList(user.getId(), request.getType(), pageParamRequest)));
    }
}
