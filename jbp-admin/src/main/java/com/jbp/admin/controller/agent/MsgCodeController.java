package com.jbp.admin.controller.agent;

import com.jbp.common.model.agent.MsgCode;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.agent.MsgCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/admin/agent/msg/code")
@Api(tags = "短信消息")
public class MsgCodeController {

    @Resource
    private MsgCodeService msgCodeService;


    @GetMapping("/page")
    @ApiOperation("短信验证码")
    public CommonResult<CommonPage<MsgCode>> page(String phone, PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(msgCodeService.page(phone, pageParamRequest)));

    }
}
