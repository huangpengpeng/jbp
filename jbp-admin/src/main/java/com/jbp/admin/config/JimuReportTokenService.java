package com.jbp.admin.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jbp.common.model.admin.SystemPermissions;
import com.jbp.common.model.admin.SystemRole;
import com.jbp.common.model.admin.SystemRoleMenu;
import org.apache.commons.codec.binary.StringUtils;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.jbp.admin.filter.TokenComponent;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.utils.RequestUtil;
import com.jbp.common.utils.SecurityUtil;

import java.util.List;

/**
 * 自定义积木报表鉴权(如果不进行自定义，则所有请求不做权限控制)
 * 1.自定义获取登录token
 * 2.自定义获取登录用户
 */
@Component
public class JimuReportTokenService implements JmReportTokenServiceI {
	
	 @Resource
	  private TokenComponent tokenComponent;

    /**
     * 通过请求获取Token
     * @param request
     * @return
     */
    @Override
    public String getToken(HttpServletRequest request) {
        return tokenComponent.getToken(request);
    }

    /**
     * 自定义获取租户
     *
     * @return
     */
    @Override
    public String getTenantId() {
        return "1";
    }

    
    /**
     * 通过Token获取登录人用户名
     * @param token
     * @return
     */
    @Override
    public String getUsername(String token) {
    	if(SecurityUtil.hasLogin()) {
    		SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
    		return admin.getAccount();
    	}
    	else {
    		return "";
    	}
    }

	/**
	 * 自定义用户拥有的角色
	 * 
	 * @param token
	 * @return
	 */
	@Override
	public String[] getRoles(String token) {
		// 只有 admin权限 才能设计报表
		if (SecurityUtil.hasLogin()) {
			SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
			return new String[] { StringUtils.equals(admin.getRoles(), "1") ? "admin" : "none" };
		} else {
			return new String[] {};
		}
	}

	/**
	 * Token校验 只有 admin权限才能查看报表 更细粒度权限控制，之后开发
	 * 
	 * @param token
	 * @return
	 */
    @Override
    public Boolean verifyToken(String token) {
//         if(!SecurityUtil.hasLogin()) {
//        	 return false;
//         }
//         SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
//         List<SystemPermissions> permissions =  SecurityUtil.getLoginUserVo().getPermissions();
//		//查询是否包含报表的权限
//		 Boolean ifContain = permissions.stream().anyMatch(s ->s.getPath() ==null? false : s.getPath().contains("/jmreport/list"));
//         if(ifContain|| StringUtils.equals(admin.getRoles(), "1")){
         	return true;
//		 }
//
//         return false;


    }

	/**
	 * 自定义请求头
	 * 
	 * @return
	 */
	@Override
	public HttpHeaders customApiHeader() {
		HttpServletRequest request = RequestUtil.getRequest();
		String token = tokenComponent.getToken(request);
		HttpHeaders header = new HttpHeaders();
		header.add(com.jbp.common.constants.Constants.HEADER_AUTHORIZATION_KEY,token);
		return header;
	}
}