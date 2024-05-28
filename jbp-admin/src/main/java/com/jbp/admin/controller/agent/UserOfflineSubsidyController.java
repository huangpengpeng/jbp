package com.jbp.admin.controller.agent;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyAddRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyEditRequest;
import com.jbp.common.request.agent.UserOfflineSubsidyRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserOfflineSubsidyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/agent/user/offline/subsidy")
@Api(tags = "线下补助")
public class UserOfflineSubsidyController {

    @Autowired
    UserService userService;
    @Autowired
    UserOfflineSubsidyService service;


    @PreAuthorize("hasAuthority('agent:user:offline:subsidy:page')")
    @ApiOperation(value = "线下补助列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/page")
    public CommonResult<CommonPage<UserOfflineSubsidy>> page(UserOfflineSubsidyRequest request, PageParamRequest pageParamRequest) {
        Integer uid = null;
        if (StringUtils.isNotEmpty(request.getAccount())) {
            User user = userService.getByAccount(request.getAccount());
            if (user == null) {
                throw new CrmebException("账号信息错误");
            }
            uid = user.getId();
        }
        return CommonResult.success(CommonPage.restPage(service.pageList(uid, request.getProvinceId(), request.getCityId(), request.getAreaId(), pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:offline:subsidy:add')")
    @ApiOperation(value = "线下补助新增", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/add")
    public CommonResult add(@RequestBody @Validated UserOfflineSubsidyAddRequest request) {
        service.add(request);
        return CommonResult.success();
    }
    @PreAuthorize("hasAuthority('agent:user:offline:subsidy:del')")
    @ApiOperation(value = "线下补助删除", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/del")
    public CommonResult del(Long id){
        service.removeById(id);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('agent:user:offline:subsidy:edit')")
    @ApiOperation(value = "线下补助编辑", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/edit")
    public CommonResult edit(@RequestBody UserOfflineSubsidyEditRequest request){
        service.edit(request);
        return CommonResult.success();
    }





}






















