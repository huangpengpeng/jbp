package com.jbp.front.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.LoginConstants;
import com.jbp.common.constants.SmsConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.constants.UserConstants;
import com.jbp.common.constants.WeChatConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserToken;
import com.jbp.common.request.IosLoginRequest;
import com.jbp.common.request.LoginMobileRequest;
import com.jbp.common.request.LoginPasswordRequest;
import com.jbp.common.request.RegisterAppWxRequest;
import com.jbp.common.request.RegisterThirdUserRequest;
import com.jbp.common.request.WechatPublicLoginRequest;
import com.jbp.common.request.WxBindingPhoneRequest;
import com.jbp.common.response.FrontLoginConfigResponse;
import com.jbp.common.response.LoginResponse;
import com.jbp.common.token.FrontTokenComponent;
import com.jbp.common.utils.CommonUtil;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.utils.WxUtil;
import com.jbp.common.vo.MyRecord;
import com.jbp.common.vo.WeChatAuthorizeLoginUserInfoVo;
import com.jbp.common.vo.WeChatMiniAuthorizeVo;
import com.jbp.common.vo.WeChatOauthToken;
import com.jbp.front.service.LoginService;
import com.jbp.service.service.CouponService;
import com.jbp.service.service.SmsService;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.UserTokenService;
import com.jbp.service.service.WechatService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * 移动端登录服务类
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
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CouponService couponService;

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean sendLoginCode(String phone) {
        return smsService.sendCommonCode(phone);
    }

    /**
     * 检测手机验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    private void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        //删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    private void checkValidateCodeNoDel(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
    }

    /**
     * 退出登录
     * @param request HttpServletRequest
     */
    @Override
    public void loginOut(HttpServletRequest request) {
        tokenComponent.logout(request);
    }

    /**
     * 手机号验证码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phoneCaptchaLogin(LoginMobileRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getCaptcha())) {
            throw new CrmebException("手机号码验证码不能为空");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        //检测验证码
        checkValidateCode(loginRequest.getPhone(), loginRequest.getCaptcha());
        //查询用户信息
        List<User> userList = userService.getByPhone(loginRequest.getPhone());
        // 默认注册
        MyRecord record = systemConfigService.getValuesByKeyList(Lists.newArrayList(SysConfigConstants.CONFIG_KEY_MOBILE_DEFAULT_REGISTER_OPEN));
        Boolean defaultRegister = record.getBoolean(SysConfigConstants.CONFIG_KEY_MOBILE_DEFAULT_REGISTER_OPEN);
        if (userList.isEmpty()) {// 此用户不存在，走新用户注册流程，默认注册用户走注册
            if(BooleanUtils.isNotTrue(defaultRegister)){
                throw new CrmebException("当前手机号未注册请先申请账号");
            }
            User user = userService.registerPhone(loginRequest.getPhone(), spreadPid);
            return getLoginResponse_V1_3(user, true);
        }
        if (userList.size() > 1 && StringUtils.isEmpty(loginRequest.getAccount())) {
            throw new CrmebException("当前手机号存在多个账号, 请选择账号在进行登录");
        }
        if (userList.size() == 1) {
            return commonLogin(userList.get(0), spreadPid);
        }
        return commonLogin(userService.getByAccount(loginRequest.getAccount()), spreadPid);
    }

    @Override
    public List<String> getAccount(LoginMobileRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getCaptcha())) {
            throw new CrmebException("手机号码验证码不能为空");
        }
        checkValidateCodeNoDel(loginRequest.getPhone(), loginRequest.getCaptcha());
        List<User> userList = userService.getByPhone(loginRequest.getPhone());
        return ListUtils.emptyIfNull(userList).stream().map(User::getAccount).collect(Collectors.toList());
    }

    /**
     * 手机号密码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phonePasswordLogin(LoginPasswordRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getPassword())) {
            throw new CrmebException("密码不能为空");
        }
        //查询用户信息
        User user = userService.getByAccount(loginRequest.getAccount());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            throw new CrmebException("用户名或密码不正确");
        }
        if (!CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone()).equals(user.getPwd())) {
            throw new CrmebException("用户名或密码不正确");
        }
        if (!user.getStatus()) {
            throw new CrmebException("当前帐户已禁用，请与管理员联系！");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        return commonLogin(user, spreadPid);
    }

    /**
     * 微信公众号授权登录
     * @param request 登录参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatPublicLogin(WechatPublicLoginRequest request) {
        // 通过code获取获取公众号授权信息
        WeChatOauthToken oauthToken = wechatService.getOauth2AccessToken(request.getCode());
        //检测是否存在
		UserToken userToken = userTokenService.getByOpenidAndType(oauthToken.getOpenId(),  UserConstants.USER_TOKEN_TYPE_WECHAT);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        WeChatAuthorizeLoginUserInfoVo userInfo = wechatService.getSnsUserInfo(oauthToken.getAccessToken(), oauthToken.getOpenId());
        logger.info("微信公众号授权登录，开放平台用户信息 = {}", JSONObject.toJSONString(userInfo));
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
        registerThirdUserRequest.setSpreadPid(spreadPid);
        registerThirdUserRequest.setType(UserConstants.REGISTER_TYPE_WECHAT);
        registerThirdUserRequest.setOpenId(oauthToken.getOpenId());
        String key = SecureUtil.md5(oauthToken.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        
    	User user = userService.getById(userToken.getUid());
    	saveLastCheckCode(user);
        return loginResponse;
    }

    /**
     * 微信登录小程序授权登录
     * @param request 用户参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatRoutineLogin(RegisterThirdUserRequest request) {
        WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        request.setSpreadPid(spreadPid);
        request.setType(UserConstants.REGISTER_TYPE_ROUTINE);
        request.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        
        return loginResponse;
    }

    /**
     * 微信注册绑定手机号
     * 一个微信 在相同的客户端 只能绑定一个账户
     * 1、保证这个原则，主需要检查当前微信在需要绑定的客户端是否存在 存在就报错
     * 2、要保证 微信授权登录只能查出来一个账号
     * @param request 请求参数
     * @return 登录信息
     */
    @Override
    public LoginResponse wechatRegisterBindingPhone(WxBindingPhoneRequest request) {
        // 检验并获取手机号【可以是传进来 也可以是自动获取的】
        checkBindingPhone(request);

        // 进入创建用户绑定手机号流程
        String value = redisUtil.get(request.getKey());
        if (StrUtil.isBlank(value)) {
            throw new CrmebException("用户缓存已过期，请清除缓存重新登录");
        }
        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(value, RegisterThirdUserRequest.class);
        if (!request.getType().equals(registerThirdUserRequest.getType())) {
            throw new CrmebException("用户的类型与缓存中的类型不符");
        }
        Integer userTokenType = getUserTokenType(request.getType());
        UserToken userToken = userTokenService.getByOpenidAndType(registerThirdUserRequest.getOpenId(), userTokenType);
        if (userToken != null) {
            throw new CrmebException("当前微信已经绑定过账户");
        }
        boolean isNew = true;
        List<User> userList = userService.getByPhone(request.getPhone());
        User user = userList.isEmpty() ? null : userList.get(0);
        // 手机号唯一  并且存在用户的情况下
        if (userService.isUnique4Phone() && !userList.isEmpty()) {
            if (userList.size() > 1) { // 要求唯一但存在多个 说明数据错误 不需要考虑业务逻辑直接异常
                throw new CrmebException("当前手机号重复注册" + request.getPhone());
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) && user.getIsWechatPublic()) {
                throw new CrmebException("该手机号已绑定微信公众号");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ROUTINE) && user.getIsWechatRoutine()) {
                throw new CrmebException("该手机号已绑定微信小程序");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX) && user.getIsWechatAndroid()) {
                throw new CrmebException("该手机号已绑定微信Android");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) && user.getIsWechatIos()) {
                throw new CrmebException("该手机号已绑定微信IOS");
            }
            userToken = userTokenService.getTokenByUserId(user.getId(), userTokenType);
            if (ObjectUtil.isNotNull(userToken)) {
                throw new CrmebException("该手机号已被注册");
            }
            isNew = false;
        } else {
            // 手机号不是唯一 或者 手机号没注册  走注册流程
            user = new User();
            user.setRegisterType(registerThirdUserRequest.getType());
            user.setPhone(request.getPhone());
            user.setAccount(userService.getAccount());
            user.setSpreadUid(0);
            user.setPwd(CommonUtil.createPwd(request.getPhone()));
            user.setNickname(CommonUtil.createNickName(request.getPhone()));
            user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
            user.setSex(0);
            user.setAddress("");
            user.setLevel(1);
        }
        switch (request.getType()) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                user.setIsWechatPublic(true);
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                user.setIsWechatRoutine(true);
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                user.setIsWechatIos(true);
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                user.setIsWechatAndroid(true);
                break;
        }
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        User finalUser = user;
        boolean finalIsNew = isNew;
        Boolean execute = transactionTemplate.execute(e -> {
            Integer spreadPid = Optional.ofNullable(registerThirdUserRequest.getSpreadPid()).orElse(0);
            if (finalIsNew) {// 新用户
                // 分销绑定
                if (spreadPid > 0 && userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
                    finalUser.setSpreadTime(CrmebDateUtil.nowDateTime());
                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), Constants.OPERATION_TYPE_ADD);
                }
                userService.save(finalUser);
            } else {
                userService.updateById(finalUser);
                if (finalUser.getSpreadUid().equals(0) && spreadPid > 0) {
                    // 绑定推广关系
                    bindSpread(finalUser, spreadPid);
                }
            }
            userTokenService.bind(registerThirdUserRequest.getOpenId(), userTokenType, finalUser.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
            throw new CrmebException(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
        }
        return getLoginResponse_V1_3(finalUser, isNew);
    }

    /**
     * 获取用户Token类型
     * @param type 用户注册类型
     */
    private Integer getUserTokenType(String type) {
        Integer userTokenType = 0;
        switch (type) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                userTokenType = UserConstants.USER_TOKEN_TYPE_WECHAT;
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ROUTINE;
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_IOS_WX;
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ANDROID_WX;
                break;
        }
        return userTokenType;
    }

    /**
     * 绑定手机号数据校验
     */
    private void checkBindingPhone(WxBindingPhoneRequest request) {
        if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) || request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) || request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            if (StrUtil.isBlank(request.getPhone()) || StrUtil.isBlank(request.getCaptcha())) {
                throw new CrmebException("手机号、验证码不能为空");
            }
            checkValidateCode(request.getPhone(), request.getCaptcha());
        } else {
            // 小程序自填手机号校验
            if (StrUtil.isNotBlank(request.getCaptcha())) {
                if (StrUtil.isBlank(request.getPhone())) {
                    throw new CrmebException("手机号不能为空");
                }
                checkValidateCode(request.getPhone(), request.getCaptcha());
                return;
            }
            //  获取微信小程序手机号 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException("小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
//                throw new CrmebException("小程序获取手机号加密数据不能为空");
                throw new CrmebException("请认证微信账号：获取手机号码失败");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException("小程序获取手机号加密算法的初始向量不能为空");
            }
            // 获取appid
            String programAppId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
            if (StringUtils.isBlank(programAppId)) {
                throw new CrmebException("微信小程序appId未设置");
            }

            WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("微信小程序获取手机号解密失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
                throw new CrmebException("微信小程序没有获取到有效的手机号");
            }
            request.setPhone(jsonObject.getString("phoneNumber"));
        }
    }

    /**
     * 绑定分销关系
     *
     * @param user      User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     */
    private Boolean bindSpread(User user, Integer spreadUid) {
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getId(), spreadUid));
        }
        return execute;
    }
    
    /**
     * 保存用户最后一次登录code 用做接口加密签名
     */
    private void saveLastCheckCode(User user) {
    	String lastcheckCode=	tokenComponent.getCheck();
    	user.setLastCheckCode(lastcheckCode);
    	userService.updateById(user);
	}

    /**
     * 获取登录配置
     */
    @Override
    public FrontLoginConfigResponse getLoginConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE);
        keyList.add(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE);
        keyList.add(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION);
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        keyList.add(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        keyList.add(SysConfigConstants.CONFIG_KEY_COPY_RIGHT_LOGO);

        keyList.add(SysConfigConstants.CONFIG_KEY_WECHAT_LOGIN_OPEN);
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_OPEN);
        keyList.add(SysConfigConstants.CONFIG_KEY_ACCOUNT_LOGIN_OPEN);
        keyList.add(SysConfigConstants.CONFIG_KEY_LOGIN_PRIVACY_AGREEMENT_OPEN);

        MyRecord record = systemConfigService.getValuesByKeyList(keyList);
        FrontLoginConfigResponse response = new FrontLoginConfigResponse();
        response.setLogo(record.getStr(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        response.setWechatBrowserVisit(record.getStr(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE));
        response.setRoutinePhoneVerification(record.getStr(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION));
        response.setMobileLoginLogo(record.getStr(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO));
        response.setSiteName(record.getStr(SysConfigConstants.CONFIG_KEY_SITE_NAME));
        response.setCopyrightLogo(record.getStr(SysConfigConstants.CONFIG_KEY_COPY_RIGHT_LOGO));
        response.setOpenWechatLogin(record.getBoolean(SysConfigConstants.CONFIG_KEY_WECHAT_LOGIN_OPEN));
        response.setOpenMobileLogin(record.getBoolean(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_OPEN));
        response.setOpenAccountLogin(record.getBoolean(SysConfigConstants.CONFIG_KEY_ACCOUNT_LOGIN_OPEN));
        response.setOpenPrivacyAgreement(record.getBoolean(SysConfigConstants.CONFIG_KEY_LOGIN_PRIVACY_AGREEMENT_OPEN));
        return response;
    }

    /**
     * 微信登录App授权登录
     */
    @Override
    public LoginResponse wechatAppLogin(RegisterAppWxRequest request) {
        //检测是否存在
        UserToken userToken = null;

        if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  UserConstants.USER_TOKEN_TYPE_IOS_WX);
        }
        if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  UserConstants.USER_TOKEN_TYPE_ANDROID_WX);
        }
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("APP微信登录记录最后一次登录时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        registerThirdUserRequest.setSpreadPid(0);
        registerThirdUserRequest.setType(request.getType());
        registerThirdUserRequest.setOpenId(request.getOpenId());
        String key = SecureUtil.md5(request.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        
        User user = userService.getById(userToken.getUid());
        	saveLastCheckCode(user);
        return loginResponse;
    }

    /**
     * ios登录
     */
    @Override
    public LoginResponse ioslogin(IosLoginRequest loginRequest) {
        // 检测是否存在
        logger.info("ios ================ 登录 请求参数：loginRequest = " + loginRequest);
        UserToken userToken = userTokenService.getByOpenidAndType(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS);
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("App记录用户最后一次登陆时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户Ios直接创建新用户
        User user = new User();
        user.setAccount(userService.getAccount());
        user.setPhone("");
        user.setSpreadUid(0);
        user.setPwd("123");
        user.setRegisterType(UserConstants.REGISTER_TYPE_IOS);
        user.setNickname(user.getAccount());
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        user.setSex(0);
        user.setAddress("");
        user.setIsBindingIos(true);
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        user.setLevel(1);
        Boolean execute = transactionTemplate.execute(e -> {
            userService.save(user);
            userTokenService.bind(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS, user.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("App用户注册生成失败，nickName = " + user.getNickname());
        }
        return getLoginResponse_V1_3(user, true);
    }

    /**
     * 校验token是否有效
     * @return true 有效， false 无效
     */
    @Override
    public Boolean tokenIsExist() {
        Integer userId = userService.getUserId();
        return userId > 0;
    }

    private LoginResponse commonLogin(User user, Integer spreadPid) {
        if (user.getSpreadUid().equals(0) && spreadPid > 0) {
            // 绑定推广关系
            bindSpread(user, spreadPid);
        }
        // 记录最后一次登录时间
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        boolean b = userService.updateById(user);
        if (!b) {
            logger.error("用户登录时，记录最后一次登录时间出错,uid = " + user.getId());
        }
        return getLoginResponse(user);
    }

    private LoginResponse getLoginResponse(User user) {
        //生成token
        LoginResponse loginResponse = new LoginResponse();
        String token = tokenComponent.createToken(user);
        loginResponse.setToken(token);
        loginResponse.setId(user.getId());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
        loginResponse.setAvatar(user.getAvatar());
        loginResponse.setAccount(user.getAccount());
        //保存最后登录随机code
        saveLastCheckCode(user);
        
        return loginResponse;
    }


	private LoginResponse getLoginResponse_V1_3(User user, Boolean isNew) {
		// 生成token
		LoginResponse loginResponse = new LoginResponse();
		String token = tokenComponent.createToken(user);
		loginResponse.setToken(token);
		loginResponse.setId(user.getId());
		loginResponse.setNikeName(user.getNickname());
		loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
		loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
		loginResponse.setAvatar(user.getAvatar());
		if (isNew) {
			loginResponse.setIsNew(true);
			List<Coupon> couponList = couponService.sendNewPeopleGift(user.getId());
			if (CollUtil.isNotEmpty(couponList)) {
				loginResponse.setNewPeopleCouponList(couponList);
			}
		}
		// 保存最后登录随机code
		saveLastCheckCode(user);
		return loginResponse;
	}
}
