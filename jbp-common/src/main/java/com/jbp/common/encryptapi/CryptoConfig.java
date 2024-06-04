package com.jbp.common.encryptapi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.user.User;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.RequestUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.LoginUserVo;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CryptoConfig {

	//当前登录用户ID
		public static final String CUSTOM_RESPONSE_RESULT_LOGINUSER= "CUSTOM_RESPONSE_RESULT_LOGINUSER";
		
	@Autowired
	SecretKeyConfig secretKeyConfig;

	public String encrypt(String word) {
		try {
			Map<String, Object> restulMap = new HashMap<String, Object>();
			String key = secretKeyConfig.getSecureKey();

			if (!StringUtils.hasText(key)) {
				throw new NullPointerException("Please configure rsa.encrypt.privatekeyc parameter!");
			}

			// 如果已经登录 则使用登录验证码 二次加密
			if (SecurityUtil.hasLogin()) {
				LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();

				String iv = secretKeyConfig.afterCutAndappend(loginUserVo.getUser().getLastCheckCode(), "0", 16);

				byte[] data = AESUtils.encrypt(word.getBytes(secretKeyConfig.getCharset()),
						key.getBytes(secretKeyConfig.getCharset()), iv);
				String result = Base64Util.encode(data);
				if (secretKeyConfig.isShowLog()) {
					log.info("Pre-encryptedone data：{}，After encryptionone：{}", word, result);
				}
				word = result;
				// code == 2 表示多层加密
				restulMap.put("modeCode", "2");
			} 
//			else if(RequestUtil.getRequest().getAttribute(CUSTOM_RESPONSE_RESULT_LOGINUSER) != null) {
//				User user=(User) RequestUtil.getRequest().getAttribute(CUSTOM_RESPONSE_RESULT_LOGINUSER);
//				String iv = secretKeyConfig.afterCutAndappend(user.getLastCheckCode(), "0", 16);
//
//                log.info("saadgyusgqye:{}",user.getLastCheckCode());
//				byte[] data = AESUtils.encrypt(word.getBytes(secretKeyConfig.getCharset()),
//						key.getBytes(secretKeyConfig.getCharset()), iv);
//				String result = Base64Util.encode(data);
//				if (secretKeyConfig.isShowLog()) {
//					log.info("Pre-encryptedone data：{}，After encryptionone：{}", word, result);
//				}
//				word = result;
//				// code == 2 表示多层加密
//				restulMap.put("modeCode", "2");
//			}
			else {
				// 单层加密
				restulMap.put("modeCode", "1");
			}

			byte[] data = word.getBytes();

			// 在 aes 加密 两层加密
			String result = SecureUtil.des(key.getBytes(secretKeyConfig.getCharset())).encryptBase64(data);

			if (secretKeyConfig.isShowLog()) {
			//	log.info("Pre-encrypted data：{}，After encryption：{}", word, result);
			}
			restulMap.put("data", result);

			return JsonUtils.writeValueAsString(CommonResult.success(restulMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CrmebException(e.getMessage());
		}
	}
}
