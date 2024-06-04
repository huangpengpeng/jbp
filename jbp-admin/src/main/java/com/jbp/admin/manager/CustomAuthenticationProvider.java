package com.jbp.admin.manager;

import cn.hutool.core.util.ObjectUtil;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.vo.LoginUserVo;
import com.jbp.service.service.impl.UserDetailServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 自定义验证（admin登录）
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailServiceImpl userDetailsService;

    public CustomAuthenticationProvider(UserDetailServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

//    private final UserDetailServiceImpl userDetailsService = new UserDetailServiceImpl();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        //以下自定义方法，判断是否登录成功
        LoginUserVo userDetails = (LoginUserVo) userDetailsService.loadUserByUsername(name);
        if (ObjectUtil.isNull(userDetails)) {
            throw new CrmebException("用户名不存在");
        }
        log.info("登录用户信息:{}", JSONObject.toJSONString(userDetails));
        // base64加密获取真正密码
        String encryptPassword = CrmebUtil.encryptPassword(password);
        if (!userDetails.getUser().getPwd().equals(encryptPassword)) {
            throw new CrmebException("账号或者密码不正确, 登录用户信息:"+JSONObject.toJSONString(userDetails));
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //确保authentication能转成该类
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public static void main(String[] args) {
        String username = "admin3";
        String name = username.substring(0, username.length() - 1);
        Integer type = Integer.valueOf(username.substring(username.length() - 1));
        System.out.println(name);
        System.out.println(type);

      //  System.out.println(CrmebUtil.encryptPassword("123merdpzx", "merdpzx"));
    }

}
