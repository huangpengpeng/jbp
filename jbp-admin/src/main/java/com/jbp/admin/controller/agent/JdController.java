package com.jbp.admin.controller.agent;

import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.UserJdRequest;
import com.jbp.common.response.UserJdResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.UserJdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/admin/agent/user/jd")
@Api(tags = "京东")
public class JdController {

    @Resource
    private UserJdService userJdService;
    @Resource
    private SystemAttachmentService systemAttachmentService;


    @PreAuthorize("hasAuthority('jd:list')")
    @ApiOperation(value = "用户京东账号")
    @GetMapping(value = "/page")
    public CommonResult<CommonPage<UserJdResponse>> page(@ModelAttribute PageParamRequest pageParamRequest, UserJdRequest userJdRequest) {
        return CommonResult.success(CommonPage.restPage(userJdService.getUserJdList(userJdRequest.getAccount(), userJdRequest.getName(), pageParamRequest)));
    }

}
