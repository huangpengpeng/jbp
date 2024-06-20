package com.jbp.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jbp.admin.filter.TokenComponent;
import com.jbp.admin.service.AdminLoginService;
import com.jbp.admin.service.ValidateCodeService;
import com.jbp.common.constants.SmsConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.encryptapi.SecretKeyConfig;
import com.jbp.common.enums.RoleEnum;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.admin.SystemMenu;
import com.jbp.common.model.admin.SystemPermissions;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.request.LoginAdminUpdateRequest;
import com.jbp.common.request.SystemAdminLoginRequest;
import com.jbp.common.response.AdminLoginInfoResponse;
import com.jbp.common.response.LoginAdminResponse;
import com.jbp.common.response.MenusResponse;
import com.jbp.common.response.SystemLoginResponse;
import com.jbp.common.token.GoogleAuthUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LoginUserVo;
import com.jbp.common.vo.MenuTree;
import com.jbp.common.vo.WeChatMiniAuthorizeVo;
import com.jbp.service.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 管理端登录服务实现类
 * +---------------------------------------------------------------------- |
 * CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +---------------------------------------------------------------------- |
 * Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +---------------------------------------------------------------------- |
 * Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +---------------------------------------------------------------------- |
 * Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Resource
    private TokenComponent tokenComponent;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemMenuService systemMenuService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private SecretKeyConfig secretKeyConfig;

    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WechatService wechatService;

    private SystemLoginResponse login3(String code, Integer adminType, String ip) {
        WeChatMiniAuthorizeVo weChatMiniAuthorizeVo  = null;
        if(StringUtils.isNotEmpty(code)){
            try {
                weChatMiniAuthorizeVo = wechatService.miniAuthCode(code);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(weChatMiniAuthorizeVo == null){
            return null;
        }
        SystemAdmin systemAdmin = systemAdminService.getOne(new QueryWrapper<SystemAdmin>().lambda().eq(SystemAdmin::getOpenId, weChatMiniAuthorizeVo.getOpenId()));
        if(systemAdmin == null){
            return null;
        }
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = systemAdmin.getAccount() + adminType;
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(principal, CrmebUtil.decryptPassowrd(systemAdmin.getPwd(), systemAdmin.getAccount())));
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        // 更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        if(weChatMiniAuthorizeVo != null && StringUtils.isNotEmpty(weChatMiniAuthorizeVo.getOpenId())){
            systemAdmin.setOpenId(weChatMiniAuthorizeVo.getOpenId());
        }
        systemAdminService.updateById(systemAdmin);

        // 返回后台LOGO图标
        if (adminType.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        } else {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        }
        return systemAdminResponse;

    }

    private SystemLoginResponse login2(SystemAdminLoginRequest request, Integer adminType, String ip) {
        WeChatMiniAuthorizeVo weChatMiniAuthorizeVo  = null;
        if(StringUtils.isNotEmpty(request.getCode())){
            try {
                weChatMiniAuthorizeVo = wechatService.miniAuthCode(request.getCode());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // 手机好 + 验证码登录
        if (StringUtils.isNotBlank(request.getPhone()) && StringUtils.isNotBlank(request.getCaptchaPhone())) {
            //验证短信
            checkValidateCode(request.getPhone(), request.getCaptchaPhone());
            LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
            lqw.eq(SystemAdmin::getPhone, request.getPhone());
            SystemAdmin systemAdmin = systemAdminService.getOne(lqw);
            if (systemAdmin == null) {
                throw new CrmebException("手机号不存在或验证码错误");
            }
            try {
                request.setPwd(CrmebUtil.decryptPassowrd(systemAdmin.getPwd(), systemAdmin.getAccount()));
                request.setAccount(systemAdmin.getAccount());
            } catch (Exception e) {
                throw new CrmebException(e.getMessage());
            }
        }

        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = request.getAccount() + adminType;
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(principal, request.getPwd()));
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();

        Merchant merchant = null;
        if (systemAdmin.getMerId() != null && systemAdmin.getMerId() > 0) {
            merchant = merchantService.getById(systemAdmin.getMerId());
        }

        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        // 更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        if(weChatMiniAuthorizeVo != null && StringUtils.isNotEmpty(weChatMiniAuthorizeVo.getOpenId())){
            List<SystemAdmin> list = systemAdminService.list(new QueryWrapper<SystemAdmin>().lambda().eq(SystemAdmin::getOpenId, weChatMiniAuthorizeVo.getOpenId()));
            for (SystemAdmin admin : list) {
                admin.setOpenId("0000");
                systemAdminService.updateById(systemAdmin);
            }
            systemAdmin.setOpenId(weChatMiniAuthorizeVo.getOpenId());
        }
        systemAdminService.updateById(systemAdmin);

        // 返回后台LOGO图标
        if (adminType.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        } else {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        }
        return systemAdminResponse;

    }
    /**
     * PC登录
     *
     * @param request   请求信息
     * @param adminType 管理员类型
     * @param ip        ip
     */
    private SystemLoginResponse login(SystemAdminLoginRequest request, Integer adminType, String ip) {
        AdminLoginInfoResponse adminLoginInfoResponse = getLoginInfo();
        // 开启了mfa
        if (StringUtils.equalsIgnoreCase(adminLoginInfoResponse.getMfaOpen(), "'0'")) {
            // 客户端传入经过了加密，需要先解密
			request.setReqCode(secretKeyConfig.decryptStr(request.getReqCode()));
            // 校验验证码
			checkCaptcha(request);
            SystemAdmin systemAdmin = new SystemAdmin();
            if (StringUtils.isNotEmpty(request.getAccount())) {
                systemAdmin = systemAdminService.selectUserByUserNameAndType(request.getAccount(), adminType);
                if (ObjectUtil.isEmpty(systemAdmin)) {
                    throw new CrmebException("账号不存在");
                }
            } else if (StringUtils.isNotEmpty(request.getPhone())) {
                systemAdmin = systemAdminService.selectUserByPhoneAndType(request.getPhone(), adminType);
                if (ObjectUtil.isEmpty(systemAdmin)) {
                    throw new CrmebException("手机号不存在");
                }
            }
            systemAdmin.setLastCheckCode(request.getReqCode());
            systemAdminService.updateById(systemAdmin);
        } else {
            // 客户端传入经过了加密，需要先解密
            request.setReqMfa(secretKeyConfig.decryptStr(request.getReqMfa()));
            SystemAdmin systemAdmin = systemAdminService.selectUserByUserNameAndType(request.getAccount(), adminType);
            checkmfa(request, systemAdmin.getMfa());
            systemAdmin.setLastCheckCode(request.getReqMfa());
            systemAdminService.updateById(systemAdmin);
        }

        // 手机好 + 验证码登录
        if (StringUtils.isNotBlank(request.getPhone()) && StringUtils.isNotBlank(request.getCaptchaPhone())) {
            //验证短信
            checkValidateCode(request.getPhone(), request.getCaptchaPhone());
            LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
            lqw.eq(SystemAdmin::getPhone, request.getPhone());
            SystemAdmin systemAdmin = systemAdminService.getOne(lqw);
            if (systemAdmin == null) {
                throw new CrmebException("手机号不存在或验证码错误");
            }
            try {
                request.setPwd(CrmebUtil.decryptPassowrd(systemAdmin.getPwd(), systemAdmin.getAccount()));
                request.setAccount(systemAdmin.getAccount());
            } catch (Exception e) {
                throw new CrmebException(e.getMessage());
            }
        }

        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = request.getAccount() + adminType;
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(principal, request.getPwd()));
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();

        Merchant merchant = null;
        if (systemAdmin.getMerId() != null && systemAdmin.getMerId() > 0) {
            merchant = merchantService.getById(systemAdmin.getMerId());
        }

        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        // 更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);

        systemAdminService.updateById(systemAdmin);

        // 返回后台LOGO图标
        if (adminType.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        } else {
            systemAdminResponse.setLeftTopLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
            systemAdminResponse.setLeftSquareLogo(
                    systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        }
        return systemAdminResponse;
    }

    /**
     * 检测手机验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void checkValidateCode(String phone, String code) {
        String value = systemConfigService.getValueByKey("sms_code_valid_open");
        if (StringUtils.isNotEmpty(value) && "0".equals(value)) {
            return;
        }
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        // 删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    /**
     * 平台端登录
     */
    @Override
    public SystemLoginResponse platformLogin(SystemAdminLoginRequest request, String ip) {
        SystemLoginResponse loginResponse = login(request, RoleEnum.PLATFORM_ADMIN.getValue(), ip);
        asyncService.installStatistics();
        return loginResponse;
    }

    /**
     * 商户端登录
     */
    @Override
    public SystemLoginResponse merchantLogin(SystemAdminLoginRequest request, String ip) {
        return login(request, RoleEnum.MERCHANT_ADMIN.getValue(), ip);
    }

    @Override
    public SystemLoginResponse merchantLogin2(SystemAdminLoginRequest request, String ip) {
        return login2(request, RoleEnum.MERCHANT_ADMIN.getValue(), ip);
    }

    @Override
    public SystemLoginResponse merchantLogin3(String code, String ip) {
        return login3(code, RoleEnum.MERCHANT_ADMIN.getValue(), ip);
    }

    @Override
    public SystemLoginResponse merchantLogin(Integer id, String ip) {
        SystemAdmin systemAdmin = systemAdminService.getById(id);
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = systemAdmin.getAccount() + RoleEnum.MERCHANT_ADMIN.getValue();
            String pwd = CrmebUtil.decryptPassowrd(systemAdmin.getPwd(), systemAdmin.getAccount());
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(principal, pwd));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();

        String token = tokenComponent.createToken(loginUser);
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        // 更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);

        systemAdminService.updateById(systemAdmin);
        // 返回后台LOGO图标
        systemAdminResponse.setLeftTopLogo(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP));
        systemAdminResponse.setLeftSquareLogo(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_LOGO_SQUARE));
        return systemAdminResponse;

    }

    /**
     * 用户登出
     */
    @Override
    public Boolean logout() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        if (ObjectUtil.isNotNull(loginUserVo)) {
            // 删除用户缓存记录
            tokenComponent.delLoginUser(loginUserVo);

            SystemAdmin systemAdmin = systemAdminService.getById(loginUserVo.getUser().getId());
            systemAdmin.setOpenId("0000");
            systemAdminService.updateById(systemAdmin);
        }
        return true;
    }

    /**
     * 获取登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginInfoResponse getLoginInfo() {
        AdminLoginInfoResponse loginPicResponse = new AdminLoginInfoResponse();
        loginPicResponse.setBackgroundImage(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse
                .setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN));
        loginPicResponse
                .setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LEFT_LOGO));
        loginPicResponse.setMfaOpen(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_MFA_OPEN));
        return loginPicResponse;
    }

    /**
     * 获取商户端登录页图片
     *
     * @return AdminLoginPicResponse
     */
    @Override
    public AdminLoginInfoResponse getMerchantLoginInfo() {
        AdminLoginInfoResponse loginPicResponse = new AdminLoginInfoResponse();
        loginPicResponse.setBackgroundImage(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LOGIN));
        loginPicResponse
                .setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LEFT_LOGO));
        loginPicResponse.setMerOpen(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_MER_OPEN));
        loginPicResponse.setMerPhoneLogin(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_MER_PHONE_LOGIN));
        loginPicResponse.setMerSiteBankQuery(
                systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_MER_SITE_BANK_QUERY));
        return loginPicResponse;
    }

    /**
     * 获取管理员可访问目录
     *
     * @return List<MenusResponse>
     */
    @Override
    public List<MenusResponse> getMenus() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        List<String> roleList = Stream.of(loginUserVo.getUser().getRoles().split(",")).collect(Collectors.toList());
        List<SystemMenu> menuList;
        if (roleList.contains("1")) {// 超管
            menuList = systemMenuService.findAllCatalogue(RoleEnum.PLATFORM_ADMIN.getValue());
        } else if (roleList.contains("2")) {// 商户主
            menuList = systemMenuService.findAllCatalogue(RoleEnum.MERCHANT_ADMIN.getValue());
        } else {
            menuList = systemMenuService.getMenusByUserId(loginUserVo.getUser().getId());
        }
        // 组装前端对象
        List<MenusResponse> responseList = menuList.stream().map(e -> {
            MenusResponse response = new MenusResponse();
            BeanUtils.copyProperties(e, response);
            return response;
        }).collect(Collectors.toList());

        MenuTree menuTree = new MenuTree(responseList);
        return menuTree.buildTree();
    }

    /**
     * 根据Token获取对应用户信息
     */
    @Override
    public LoginAdminResponse getInfoByToken() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin systemAdmin = loginUserVo.getUser();
        LoginAdminResponse loginAdminResponse = new LoginAdminResponse();
        BeanUtils.copyProperties(systemAdmin, loginAdminResponse);
        List<String> roleList = Stream.of(systemAdmin.getRoles().split(",")).collect(Collectors.toList());
        List<String> permList = CollUtil.newArrayList();
        if ((roleList.contains("1") && (systemAdmin.getType().equals(RoleEnum.PLATFORM_ADMIN.getValue()))
                || systemAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue()))) {
            permList.add("*:*:*");
        } else if ((roleList.contains("2") && (systemAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue()))
                || systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()))) {
            permList.add("*:*:*");
        } else {
            permList = loginUserVo.getPermissions().stream().map(SystemPermissions::getPath)
                    .collect(Collectors.toList());
        }
        loginAdminResponse.setPermissionsList(permList);

        if (systemAdmin.getMerId() > 0) {
            Merchant merchant = merchantService.getById(systemAdmin.getMerId());
            loginAdminResponse.setMerStarLevel(merchant.getStarLevel());
            loginAdminResponse.setMerReceiptPrintingSwitch(merchant.getReceiptPrintingSwitch());
        }
        return loginAdminResponse;
    }

    /**
     * 修改登录用户信息
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean loginAdminUpdate(LoginAdminUpdateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemAdmin systemAdmin = new SystemAdmin();
        systemAdmin.setId(admin.getId());
        systemAdmin.setRealName(request.getRealName());
        String pwd = CrmebUtil.encryptPassword(request.getPassword(), admin.getAccount());
        systemAdmin.setPwd(pwd);
        return systemAdminService.updateById(systemAdmin);
    }

    /**
     * 校验验证码是否正确
     *
     * @param request 登录参数
     */
    private void checkCaptcha(SystemAdminLoginRequest request) {
        // 判断验证码
        boolean codeCheckResult = validateCodeService.check(request.getKey(), request.getReqCode());
        if (!codeCheckResult)
            throw new CrmebException("验证码不正确");
    }

    private void checkmfa(SystemAdminLoginRequest request, String secret) {
        if (StringUtils.isBlank(request.getPwd()) || StringUtils.equals(request.getPwd(), DigestUtils.md5Hex("123456"))
                || StringUtils.equals(request.getPwd(), DigestUtils.md5Hex("12345"))) {
            throw new CrmebException("不能使用弱口令密码登录");
        }
        com.jbp.common.token.GoogleAuthenticator ga = new com.jbp.common.token.GoogleAuthenticator();
        boolean codeCheckResult = ga.check_code(secret, Long.parseLong(request.getReqMfa()),
                System.currentTimeMillis());
        if (!codeCheckResult)
            throw new CrmebException("验证码不正确");
    }

    @Override
    public Map<String, String> loginUserMfaKey() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        Map<String, String> result = GoogleAuthUtil.genAuthQrCode(admin.getAccount());
        admin.setMfa(result.get("secret"));
        systemAdminService.updateById(admin);
        return result;
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean sendLoginCode(String phone) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getPhone, phone);
        SystemAdmin systemAdmin = systemAdminService.getOne(lqw);
        if (systemAdmin == null) {
            return false;
        }
        return smsService.sendCommonCode(phone);
    }
}
