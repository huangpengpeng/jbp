package com.jbp.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.jbp.common.dto.UserWhiteDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.WhiteUserRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WhiteUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/admin/agent/white/user")
@Api(tags = "用户白名单")
public class WhiteUserController {
    @Autowired
    private WhiteUserService whiteUserService;
    @Resource
    private UserService userService;
    @PreAuthorize("hasAuthority('agent:white:user:page:list')")
    @GetMapping("/page/list")
    @ApiOperation("用户白名单列表")
    public CommonResult<CommonPage<WhiteUser>> getList(@ModelAttribute @Validated WhiteUserRequest request,
                                                       @ModelAttribute PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (ObjectUtil.isNull(request.getAccount()) || !request.getAccount().equals("")) {
            try {
                uid = userService.getByAccount(request.getAccount()).getId();
            } catch (NullPointerException e) {
                throw new CrmebException("账号信息错误");
            }
        }
        return CommonResult.success(CommonPage.restPage(whiteUserService.pageList(uid,request.getWhiteId(), pageParamRequest)));
    }
    @PreAuthorize("hasAuthority('agent:white:user:add')")
    @PostMapping("/add")
    @ApiOperation("新增")
    public CommonResult<WhiteUser> addUserWhite(@RequestBody WhiteUserRequest userWhiteRequest) {
        User user = userService.getByAccount(userWhiteRequest.getAccount());
        if (ObjectUtil.isNull(user)) {
            return CommonResult.failed("未找到用户信息");
        }
        whiteUserService.add(user.getId(),userWhiteRequest.getWhiteId(),userWhiteRequest.getOrdersSn());
        return CommonResult.success();

    }
    @DeleteMapping(value = "/batchDelete",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量删除",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult batchDelete(@RequestBody @ApiParam("用户名单id") List<Integer> id) {
        if (CollectionUtils.isEmpty(id)) {
            return CommonResult.failed("参数不能为空");
        }
        whiteUserService.removeByIds(id);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:white:user:import')")
    @PostMapping( value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导入新增",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult importUserWhite(@RequestBody List<UserWhiteDto> userWhiteList) {
        whiteUserService.batchSave(userWhiteList);
        return CommonResult.success();
    }
}
