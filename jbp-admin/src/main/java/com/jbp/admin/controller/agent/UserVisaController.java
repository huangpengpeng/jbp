package com.jbp.admin.controller.agent;

import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserVisaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/user/visa")
@Api(tags = "法大大签约")
public class UserVisaController {
    @Resource
    private UserVisaService userVisaService;

    @Resource
    private UserService userService;


    @PreAuthorize("hasAuthority('agent:user:visa:page')")
    @ApiOperation(value = "用户签署法大大列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<CommonPage<UserVisaRecordResponse>> getList(String account, PageParamRequest pageParamRequest) {

        return CommonResult.success(CommonPage.restPage(userVisaService.pageList(account, pageParamRequest)));
    }

    @PreAuthorize("hasAuthority('agent:user:visa:save')")
    @ApiOperation(value = "增加用户签署信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult saves(String account, String contract) {

        User user = userService.getByAccount(account);
        UserVisa userVisa = new UserVisa();
        userVisa.setVisa(false);
        userVisa.setContract(contract);
        userVisa.setUid(user.getId());
        userVisaService.save(userVisa);
        return CommonResult.success();
    }

}
