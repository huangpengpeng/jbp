package com.jbp.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.vo.JdPayOauth2Response;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserJd;
import com.jbp.common.request.IosBindingPhoneRequest;
import com.jbp.common.result.CommonResult;
import com.jbp.service.service.UserJdService;
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
    @Resource
    private UserJdService userJdService;

    @ApiOperation(value = "京东授权（登录后绑定）")
    @RequestMapping(value = "/oauth", method = RequestMethod.GET)
    public CommonResult<Object> auth(String  code) {
        User user = userService.getInfo();
        JdPayOauth2Response response = jdPay.oauth2(code);
        String xid = response.getXid();
        UserJd userJd2 =  userJdService.getOne(new QueryWrapper<UserJd>().lambda().eq(UserJd::getUid,user.getId()));
        if(userJd2!= null){
            return CommonResult.success();
        }
        UserJd userJd = new UserJd();
        userJd.setUid(user.getId());
        userJd.setXid(xid);
        userJdService.save(userJd);

        return CommonResult.success();
    }


    @ApiOperation(value = "京东账号")
    @RequestMapping(value = "/jdAccount", method = RequestMethod.GET)
    public CommonResult<Object> jdAccount() {
        User user = userService.getInfo();

       UserJd userJd =  userJdService.getOne(new QueryWrapper<UserJd>().lambda().eq(UserJd::getUid,user.getId()));
       if(userJd!=null){
           return CommonResult.success(userJd.getXid());
       }

        return CommonResult.success();
    }
}
