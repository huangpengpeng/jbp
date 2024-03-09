package com.jbp.admin.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.constants.UploadConstants;
import com.jbp.common.encryptapi.CryptoConfig;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.encryptapi.SecretKeyConfig;
import com.jbp.common.utils.SpringUtil;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.SystemConfigService;

/**
 * response路径处理
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
public class ResponseRouter {


	// 是否忽略签名
	private static final String CUSTOM_RESPONSE_RESULT_ENCRYPTIGNORE = "CUSTOM-RESPONSE-RESULT-ENCRYPTIGNORE";

	public String filter(HttpServletRequest request, String data, String path, CrmebConfig crmebConfig,
			CryptoConfig cryptoConfig, SecretKeyConfig secretKeyConfig,SystemConfigService systemConfigService) {
		boolean result = un().contains(path);
		if (result) {
			return data;
		}
		
		//做内容替换，比如  引用了报表插件做logo替换
		data=stringFilter(data, systemConfigService);
		
		// 系统不用过滤的URL，针对数据而非token
		PathMatcher pathMatcher = new AntPathMatcher();
		for (String configUrl : crmebConfig.getIgnored()) {
			if (pathMatcher.match(path, configUrl)) {
				return data;
			}
		}

		// 根据需要处理返回值
		if ((data.contains(UploadConstants.UPLOAD_FILE_KEYWORD + "/") && !data.contains("data:image/png;base64"))
				|| data.contains(UploadConstants.DOWNLOAD_FILE_KEYWORD)
				|| data.contains(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD)) {
			if (data.contains(UploadConstants.DOWNLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_MODEL_PATH_EXCEL)) {
				data = SpringUtil.getBean(SystemAttachmentService.class).prefixFile(data);
			} else if (data.contains(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/")) {
				data = SpringUtil.getBean(SystemAttachmentService.class).prefixUploadf(data);
			} else {
				data = SpringUtil.getBean(SystemAttachmentService.class).prefixImage(data);
			}
		}
		
		if (!path.contains("api/admin/") && !path.contains("api/front/")) {
			return data;
		}
		
		if(!secretKeyConfig.isOpen()) {
			return data;
		}

		// 对返回数据加密处理
		EncryptIgnore EncryptIgnore = (EncryptIgnore) request.getAttribute(CUSTOM_RESPONSE_RESULT_ENCRYPTIGNORE);
		if (EncryptIgnore == null) {
			if (secretKeyConfig.isOpen()) {
				data = cryptoConfig.encrypt(data);
			}
		}
		return data;
	}

	public static String un() {
		return "";
	}
	
	
	public static String stringFilter(String data, SystemConfigService systemConfigService) {
		// 替换积木报表 左上角LOGO 如果之后有很多替换可以考虑做一层分装
		if (data.contains("../logoimage/jim_logo_20200814094127.png")) {
			String siteLogoLeftTop = systemConfigService
					.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LEFT_TOP);
			return data.replace("../logoimage/jim_logo_20200814094127.png", siteLogoLeftTop);
		}
		return data;
	}
}
