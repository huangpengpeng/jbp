package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserCapaRequest;
import com.jbp.common.request.agent.UserCapaXsAddRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/capa/xs")
@Api(tags = "用户星级")
public class UserCapaXsController {
    @Resource
    private UserCapaXsService userCapaXsService;

    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:capa:xs:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<UserCapaXs>> getList(UserCapaRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(userCapaXsService.pageList(uid, request.getCapaId(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:capa:xs:add')")
    @PostMapping("/add")
    @ApiOperation("添加")
    public CommonResult add(@Validated  @RequestBody UserCapaXsAddRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            return CommonResult.failed("账户信息错误");
        }
        userCapaXsService.saveOrUpdateCapa(user.getId(), request.getCapaId(), request.getIfFake(), request.getRemark(), request.getDescription());
        return CommonResult.success();
    }

}
