package com.jbp.front.filter;

/** 
 * @author zoro 
 */  
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import jodd.util.StringUtil;
  
public class XssFilter implements Filter {
	private String filterChar;
	private String replaceChar;
	private String splitChar;
	private String excludeUrls;
	FilterConfig filterConfig = null;
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterChar=filterConfig.getInitParameter("FilterChar");
		this.replaceChar=filterConfig.getInitParameter("ReplaceChar");
		this.splitChar=filterConfig.getInitParameter("SplitChar");
		this.excludeUrls=filterConfig.getInitParameter("excludeUrls");
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(isExcludeUrl(request)){
			chain.doFilter(request, response);
		}else{
			chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request,filterChar,replaceChar,splitChar), response);
		}
	}
	
	
	private boolean isExcludeUrl(ServletRequest request) {
		boolean exclude = false;
		if (StringUtil.isNotBlank(excludeUrls)) {
			String[] excludeUrl = excludeUrls.split(splitChar);
			if (excludeUrl != null && excludeUrl.length > 0) {
				for (String url : excludeUrl) {
					if (getURI((HttpServletRequest) request).startsWith(url)) {
						exclude = true;
					}
				}
			}
		}
		return exclude;
	}
	

	private  String getURI(HttpServletRequest request) {
		UrlPathHelper helper = new UrlPathHelper();
		String uri = helper.getOriginatingRequestUri(request);
		String ctx = helper.getOriginatingContextPath(request);
		if (!StringUtil.isBlank(ctx)) {
			return uri.substring(ctx.length());
		} else {
			return uri;
		}
	}
}
