package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.model.agent.UserCapaXsSnapshot;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserCapaXsSnapshotRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsSnapshotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/capa/xs/snapshot")
@Api(tags = "用户星级快照")
public class UserCapaXsSnapshotController {
    @Resource
    UserCapaXsSnapshotService userCapaXsSnapshotService;

    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:capa:xs:snapshot:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<UserCapaXsSnapshot>> getList(UserCapaXsSnapshotRequest request, PageParamRequest pageParamRequest) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            if (ObjectUtil.isNull(request.getAccount()) || request.getAccount().equals("")) {
                user = new User();
            } else {
                return CommonResult.failed("账户信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userCapaXsSnapshotService.pageList(user.getId(), request.getCapaId(), request.getType(), pageParamRequest)));
    }
}