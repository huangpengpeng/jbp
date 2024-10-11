package com.jbp.front.controller;

import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.vo.JdPayOauth2Response;
import com.jbp.common.model.user.User;
import com.jbp.common.request.IosBindingPhoneRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController("JdController")
@RequestMapping("api/front/jd")
@Api(tags = "JD控制器")
public class JdController {

    @Resource
    private UserService userService;
    @Resource
    private JdPay jdPay;

    @ApiOperation(value = "京东授权（登录后绑定）")
    @RequestMapping(value = "/oauth", method = RequestMethod.GET)
    public CommonResult<Object> auth(String  code) {
        User user = userService.getInfo();
        JdPayOauth2Response response = jdPay.oauth2(code);
        String xid = response.getXid();

        return CommonResult.success();
    }
}
