package com.jbp.common.encryptapi;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.User;
import com.jbp.common.token.FrontTokenComponent;
import com.jbp.common.utils.RequestUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LoginUserVo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;

public class SignInterceptor  extends HandlerInterceptorAdapter {
	
	final static com.google.common.cache.Cache<String, Object>  CACHE_NONCE = CacheBuilder.newBuilder().expireAfterWrite(180, TimeUnit.SECONDS).build();
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String path=RequestUtil.getUri(request);
		if (!path.contains("api/admin/") && !path.contains("api/front/")) {
			return super.preHandle(request, response, handler);
		}
		
		
		SecretKeyConfig secretKeyConfig = com.jbp.common.utils.SpringUtil.getBean(SecretKeyConfig.class);
		FrontTokenComponent frontTokenComponent = com.jbp.common.utils.SpringUtil.getBean(FrontTokenComponent.class);

		HandlerMethod hm = (HandlerMethod) handler;
		Method method = hm.getMethod();
		
		


		if (method.isAnnotationPresent(EncryptIgnore.class)) {
			return super.preHandle(request, response, handler);
		}

		// 获取当前类
		final Class<?> clazz = hm.getBeanType();
		if (clazz.isAnnotationPresent(EncryptIgnore.class)) {
			return super.preHandle(request, response, handler);
		}

		if (!secretKeyConfig.isOpen()) {
			return super.preHandle(request, response, handler);
		}

		String timestampStr = request.getHeader("m");
		if (StringUtils.isBlank(timestampStr)) {
			throw new CrmebException("签名错误");
		}
		String sign = request.getHeader("sign");
		if (StringUtils.isBlank(sign)) {
			sign = request.getHeader("s");
		}
		if (StringUtils.isBlank(sign)) {
			throw new CrmebException("签名错误");
		}
		String nonce = request.getHeader("o");
		if (StringUtils.isBlank(nonce)) {
			throw new CrmebException("签名错误");
		}
		// 得到正确的sign供检验用
		String origin = secretKeyConfig.afterCutAndappend(secretKeyConfig.getKey(), "0", 10, 16) + timestampStr + nonce
				+ secretKeyConfig.afterCutAndappend(secretKeyConfig.getKey(), "0", 28, 16) + "md5";
		// 后台用户已经登录
		if (SecurityUtil.hasLogin()) {
			LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
			String decryptStr = secretKeyConfig.encryptStr(loginUserVo.getUser().getLastCheckCode());
			origin = secretKeyConfig.afterCutAndappend(decryptStr, "0", 0, 4) + origin;
		}

		// 前台用户已经登录
		String frontToken = frontTokenComponent.getToken(request);
		if (StringUtils.isNotBlank(frontToken)) {
			String[] tokenValues = frontToken.split("@");
			if (tokenValues.length > 1) {
				String checkCode = tokenValues[1];

				String decryptStr = secretKeyConfig.decryptStr(checkCode);
				origin = secretKeyConfig.afterCutAndappend(decryptStr, "0", 0, 4) + origin;
			}
		}

		String signEcrypt = SecureUtil.md5(origin);
		long timestamp = 0;
		try {
			timestamp = Long.parseLong(timestampStr);
		} catch (Exception e) {
			throw new CrmebException("签名错误");
		}
		// 前端的时间戳与服务器当前时间戳相差如果大于180，判定当前请求的timestamp无效"
		if ((timestamp - System.currentTimeMillis()) / 1000 > 180) {
			throw new CrmebException("签名到期");
		}
		// nonce是否存在于redis中，检查当前请求是否是重复请求
		boolean nonceExists = CACHE_NONCE.getIfPresent(timestampStr + nonce) != null;
		if (nonceExists) {
			throw new CrmebException("重复请求");
		}
		// 后端MD5签名校验与前端签名sign值比对
		if (!(sign.equalsIgnoreCase(signEcrypt))) {
			throw new CrmebException("签名验证失败");
		}

		return super.preHandle(request, response, handler);
	}
	
	private String sort(TreeMap<String, Object> params) {
		StringBuffer buffer = new StringBuffer();
		for (String key : params.keySet()) {
			Object values = params.get(key);
			if (values instanceof String[]) {
				for (String v : (String[]) values) {
					buffer.append(key).append("=").append(v).append("&");
				}
			} else {
				buffer.append(key).append("=").append(values.toString()).append("&");
			}
		}
		return buffer.toString();
	}
}
