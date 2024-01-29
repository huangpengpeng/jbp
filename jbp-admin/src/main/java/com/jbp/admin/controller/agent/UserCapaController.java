package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserCapaAddRequest;
import com.jbp.common.request.agent.UserCapaRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/capa")
@Api(tags = "用户等级")
public class UserCapaController {
    @Resource
    private UserCapaService userCapaService;

    @Resource
    private UserService userService;

    @PreAuthorize("hasAuthority('agent:user:capa:page')")
    @GetMapping("/page")
    @ApiOperation("列表分页查询")
    public CommonResult<CommonPage<UserCapa>> getList(UserCapaRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(userCapaService.pageList(uid, request.getCapaId(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:capa:add')")
    @PostMapping("/add")
    @ApiOperation("添加")
    public CommonResult add(@RequestBody UserCapaAddRequest request) {
        User user = userService.getByAccount(request.getAccount());
        if (ObjectUtil.isNull(user)) {
            return CommonResult.failed("账户信息错误");
        }
        userCapaService.saveOrUpdateCapa(user.getId(), request.getCapaId(), request.getRemark(), request.getDescription());
        return CommonResult.success();
    }

}
