package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserCapaSnapshotRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/capa/snapshot")
@Api(tags = "用户等级快照")
public class UserCapaSnapshotController {
    @Resource
    private UserCapaSnapshotService userCapaSnapshotService;
    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:capa:snapshot:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<UserCapaSnapshot>> getList(UserCapaSnapshotRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userCapaSnapshotService.pageList(uid, request.getCapaId(), request.getType(), pageParamRequest)));
    }
}
