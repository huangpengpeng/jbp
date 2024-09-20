package com.jbp.front.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.dto.CbecOrderSyncDTO;
import com.jbp.common.dto.EncryptionDTO;
import com.jbp.common.encryptapi.AESUtils;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.order.CbecOrder;
import com.jbp.common.model.user.CbecUser;
import com.jbp.common.model.user.User;
import com.jbp.common.request.*;
import com.jbp.common.response.AccountCapaResponse;
import com.jbp.common.response.FrontIndividualCenterConfigResponse;
import com.jbp.common.response.FrontLoginConfigResponse;
import com.jbp.common.response.LoginResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.*;
import com.jbp.front.service.LoginService;
import com.jbp.service.service.CbecOrderService;
import com.jbp.service.service.CbecUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户登陆 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/login")
@Api(tags = "用户 -- 登录注册")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Resource
    private UserService userService;
    @Resource
    private CbecUserService cbecUserService;
    @Resource
    private CbecOrderService cbecOrderService;

    @EncryptIgnore
    @ApiOperation(value = "获取登录配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public CommonResult<FrontLoginConfigResponse> getLoginConfig() {
        return CommonResult.success(loginService.getLoginConfig());
    }

    @EncryptIgnore
    @ApiOperation(value = "获取个人中心配置")
    @RequestMapping(value = "/individual/center/config")
    public CommonResult<FrontIndividualCenterConfigResponse> getIndividualCenterConfig() {
        return CommonResult.success(loginService.getIndividualCenterConfig());
    }

    @EncryptIgnore
    @ApiOperation(value = "手机号获取账号")
    @RequestMapping(value = "/account/List", method = RequestMethod.POST)
    public CommonResult<List<AccountCapaResponse>> accountList(@RequestBody @Validated LoginMobileRequest loginRequest) {
        List<AccountCapaResponse> account = loginService.getAccount(loginRequest);
        return CommonResult.success(account);
    }

    
    @ApiOperation(value = "校验账号")
    @GetMapping("/check/account")
    public CommonResult<String> checkAccount(String account) {
        User user = userService.getByAccount(account);
        if (ObjectUtil.isEmpty(user)) {
            throw new CrmebException("暂无账号,请先注册");
        }
        if (StringUtils.isEmpty(user.getPhone())) {
            throw new CrmebException("请联系管理员绑定手机号");
        }
        return CommonResult.success();
    }

    
    @EncryptIgnore
    @ApiOperation(value = "手机号验证码登录")
    @RequestMapping(value = "/mobile/captcha", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phoneCaptchaLogin(@RequestBody @Validated LoginMobileRequest loginRequest) {
        return CommonResult.success(loginService.phoneCaptchaLogin(loginRequest));
    }

    @EncryptIgnore
    @ApiOperation(value = "手机号密码登录")
    @RequestMapping(value = "/mobile/password", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phonePasswordLogin(@RequestBody @Validated LoginPasswordRequest loginRequest) {
        return CommonResult.success(loginService.phonePasswordLogin(loginRequest));
    }

    @EncryptIgnore
    @ApiOperation("账号登录")
    @PostMapping("/mobile/account")
    public CommonResult<LoginResponse> accountLogin(@RequestBody @Validated LoginAccountwordRequest loginRequest, HttpServletRequest request) {
        loginService.loginOut(request);
        return CommonResult.success(loginService.accountLogin(loginRequest));
    }

    @EncryptIgnore
    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> loginOut(HttpServletRequest request) {
        loginService.loginOut(request);
        return CommonResult.success();
    }

    @EncryptIgnore
    @ApiOperation(value = "发送短信登录验证码")
    @RequestMapping(value = "/send/code", method = RequestMethod.POST)
    public CommonResult<String> sendCode(@RequestBody @Validated SendCodeRequest request) {
        if (loginService.sendLoginCode(request.getPhone())) {
            return CommonResult.success("发送成功");
        }
        return CommonResult.failed("发送失败");
    }

    @EncryptIgnore
    @ApiOperation(value = "微信公众号号授权登录")
    @RequestMapping(value = "/wechat/public", method = RequestMethod.POST)
    public CommonResult<LoginResponse> wechatPublicLogin(@RequestBody @Validated WechatPublicLoginRequest request) {
        return CommonResult.success(loginService.wechatPublicLogin(request));
    }

    @EncryptIgnore
    @ApiOperation(value = "微信登录小程序授权登录")
    @RequestMapping(value = "/wechat/routine", method = RequestMethod.POST)
    public CommonResult<LoginResponse> wechatRoutineLogin(@RequestBody @Validated RegisterThirdUserRequest request) {
        return CommonResult.success(loginService.wechatRoutineLogin(request));
    }

    @ApiOperation(value = "微信注册绑定手机号")
    @RequestMapping(value = "/wechat/register/binding/phone", method = RequestMethod.POST)
    public CommonResult<LoginResponse> wechatRegisterBindingPhone(@RequestBody @Validated WxBindingPhoneRequest request) {
        return CommonResult.success(loginService.wechatRegisterBindingPhone(request));
    }

    @EncryptIgnore
    @ApiOperation(value = "微信登录App授权登录")
    @RequestMapping(value = "/wechat/app/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> wechatAppLogin(@RequestBody @Validated RegisterAppWxRequest request) {
        return CommonResult.success(loginService.wechatAppLogin(request));
    }

    @EncryptIgnore
    @ApiOperation(value = "ios登录")
    @RequestMapping(value = "/ios/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> ioslogin(@RequestBody @Validated IosLoginRequest loginRequest) {
        return CommonResult.success(loginService.ioslogin(loginRequest));
    }

    @EncryptIgnore
    @ApiOperation(value = "校验token是否有效")
    @RequestMapping(value = "/token/is/exist", method = RequestMethod.POST)
    public CommonResult<Boolean> tokenIsExist() {
        return CommonResult.success(loginService.tokenIsExist());
    }

    @ApiOperation(value = "忘记密码")
    @PostMapping(value = "/forgot/password")
    public CommonResult forgotPassword(@RequestBody @Validated ForgotPasswordRequest request, HttpServletRequest request2) {
        loginService.loginOut(request2);
        loginService.forgotPassword(request.getAccount(), request.getPassword(), request.getCaptcha(), request.getPhone());
        return CommonResult.success();
    }


    @EncryptIgnore
    @ApiOperation(value = "跨境授权")
    @RequestMapping(value = "/cbec_user_account", method = RequestMethod.GET)
    public CommonResult<EncryptionDTO> cbec() {
        User user = userService.getInfo();

        String account = user.getAccount();
        CbecUser cbecUser = cbecUserService.getOne(new QueryWrapper<CbecUser>().lambda().eq(CbecUser::getUid, user.getId()));
        if (cbecUser != null) {
            account = cbecUser.getAccountNo();
        }

        EncryptionDTO dto = EncryptionDTO.builder().bizId(account).channelName("SYCP").mobile(user.getPhone())
                .noceStr(SignatureUtil.generateNonceStr()).timestamp(System.currentTimeMillis()).build();
        Map<String, String> stringStringMap = SignatureUtil.convertObjectToMap(dto);
        try {
            stringStringMap.put(SignatureUtil.FIELD_SIGN,
                    SignatureUtil.generateSignature(stringStringMap, "LpfVpVnnyZS1XBEhshztDwt3gG9tOr8t", SignType.MD5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String body = JSON.toJSONString(stringStringMap);
        HttpRequest request = HttpRequest.post("https://buyer.api.xiangyuanb2b.com/buyer/passport/member/mobileEncryption");
        request.contentType("application/json");
        request.charset("utf-8");
        String response = request.body(body).send().bodyText();
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("请求接口https://buyer.api.xiangyuanb2b.com/buyer/passport/member/mobileEncryption返回信息未空");
        }
        JSONObject jsonObject = JSON.parseObject(response);
        if (!jsonObject.containsKey("success") || BooleanUtils.isNotTrue(jsonObject.getBoolean("success"))) {
            throw new RuntimeException("请求接口https://buyer.api.xiangyuanb2b.com/buyer/passport/member/mobileEncryption返回失败");
        }
        JSONObject result = jsonObject.getJSONObject("result");
        dto.setAccessToken(result.getString("accessToken"));
        dto.setRefreshToken(result.getString("refreshToken"));
        return CommonResult.success(dto);
    }



    @EncryptIgnore
    @ApiOperation(value = "跨境订单同步")
    @RequestMapping(value = "/cbec_order/sync", method = RequestMethod.POST)
    public CommonResult<String> ioslogin(@RequestBody String syncStr) throws Exception {


        // 设置回传信息发送异步消息
        JSONObject jsonObject = JSON.parseObject(syncStr);
        String decrypt = AESUtils.decrypt(jsonObject.getString("data"), "IihtFZ3nFq8vVHe4");
        CbecOrderSyncReq orderSyncReq =
                jsonObject.toJavaObject(JSONObject.parseObject(decrypt), CbecOrderSyncReq.class);
        Map<String, String> stringStringMap = SignatureUtil.convertObjectToMap(orderSyncReq);
        stringStringMap.put("sign", jsonObject.getString("sign"));
        boolean signatureValid = SignatureUtil.isSignatureValid(stringStringMap,"LpfVpVnnyZS1XBEhshztDwt3gG9tOr8t", SignType.MD5);
        if (BooleanUtils.isNotTrue(signatureValid)) {
            throw new RuntimeException("验签失败");
        }
         jsonObject = JSON.parseObject(decrypt);

        JSONArray orderItems = jsonObject.getJSONArray("orderItems");
        String orderStatus = CbecOrder.parseCode(jsonObject.getString("orderStatus"));
        if(StringUtils.isBlank(orderStatus)){
            return CommonResult.success();
        }
        List<CbecOrderSyncDTO.GoodsDetail> goodsDetails = Lists.newArrayListWithCapacity(orderItems.size());
        for (int i = 0, size = orderItems.size(); i < size; i++) {
            JSONObject item = orderItems.getJSONObject(i);

            CbecOrderSyncDTO.GoodsDetail goodsDetail =
                    CbecOrderSyncDTO.GoodsDetail.builder().goodsName(item.getString("goodsName"))
                            .price(item.getBigDecimal("unitPrice")).quantity(item.getInteger("num")).build();
            goodsDetails.add(goodsDetail);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date paymentTime =
                StringUtils.isNoneBlank(jsonObject.getString("paymentTime")) ? sdf.parse(jsonObject.getString("paymentTime")) : null;
        Date createTime =
                StringUtils.isNoneBlank(jsonObject.getString("createTime")) ? sdf.parse(jsonObject.getString("createTime")) : null;

        Date logisticsTime = StringUtils.isNoneBlank(jsonObject.getString("logisticsTime"))
                ? sdf.parse(jsonObject.getString("logisticsTime")) : DateTimeUtils.getNow();
        BigDecimal score =
                jsonObject.containsKey("payPoints") && ArithmeticUtils.gt(jsonObject.getBigDecimal("payPoints"), BigDecimal.ZERO) ?
                        jsonObject.getBigDecimal("payPoints") : BigDecimal.ZERO;

        CbecOrderSyncDTO orderSyncDTO =
                CbecOrderSyncDTO.builder().bizId(jsonObject.getString("bizId")).mobile(jsonObject.getString("mobile"))
                        .orderSn(jsonObject.getString("orderSn")).status(orderStatus).totalFee(jsonObject.getBigDecimal("flowPrice"))
                        .goodsFee(jsonObject.getBigDecimal("goodsPrice")).postFee(jsonObject.getBigDecimal("freightPrice")).score(score)
                        .createTime(createTime).paymentTime(paymentTime).shipmentTime(logisticsTime).goodsDetails(goodsDetails)
                        .build();
        cbecOrderService.orderSync(orderSyncDTO);



        return CommonResult.success();
    }




    @EncryptIgnore
    @ApiOperation(value = "手机号验证码注册")
    @RequestMapping(value = "/mobile/register", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phoneCaptchaRegister(@RequestBody @Validated RegisterMobileRequest loginRequest) {
        // 校验密码是否是6个连续的数字
        String regex1 = "^(012345|123456|234567|345678|456789|567890|654321|543210|432109|321098|210987|109876)$";
        // 校验密码是否是6个相同的数字
        String regex2 = "^(\\d)\\1{5}$";

        if (loginRequest.getPwd().matches(regex1)) {
            throw new RuntimeException("登录密码过于简单,请重新设置");
        }
        if (loginRequest.getPwd().matches(regex2)) {
            throw new RuntimeException("登录密码过于简单,请重新设置");
        }
        if (loginRequest.getPayPwd().matches(regex1)) {
            throw new RuntimeException("交易密码过于简单,请重新设置");
        }
        if (loginRequest.getPayPwd().matches(regex2)) {
            throw new RuntimeException("交易密码过于简单,请重新设置");
        }
        return CommonResult.success(loginService.phoneCaptchaRegister(loginRequest));
    }


    @EncryptIgnore
    @ApiOperation(value = "校验交易密码")
    @RequestMapping(value = "/verify/pwt", method = RequestMethod.POST)
    public CommonResult<Boolean> verifyPwt() {
        User user = userService.getInfo();
        if (user == null) {
            throw new CrmebException("未登录！");
        }
        String payPwd = CrmebUtil.encryptPassword("123456");
        return CommonResult.success(!user.getPayPwd().equals(payPwd));
    }
}



