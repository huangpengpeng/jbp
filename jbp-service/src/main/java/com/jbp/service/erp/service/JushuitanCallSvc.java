package com.jbp.service.erp.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.jbp.common.model.system.JushuitanConfig;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.erp.tools.Constants;
import com.jbp.service.erp.tools.SignUtil;
import com.jbp.service.service.JushuitanConfigService;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JushuitanCallSvc {

	public String getAccessToken(String code) {
		URIBuilder uriBuilder=null;
		try {
			uriBuilder = new URIBuilder(getLocation(getNativeRequest()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		String domain = uriBuilder.getHost();
		JushuitanConfig jushuitanConfig = jushuitanConfigService.def();
		Map<String, Object> params = Maps.newConcurrentMap();
		params.put("app_key", environment.getProperty("jushuitan.appKey"));
		params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		params.put("grant_type", "authorization_code");
		params.put("charset", "utf-8");
		params.put("code", code);
		String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
		params.put("sign", sign);
		JSONObject jsonObject = callapi(Constants.ACCESS_TOKEN_URL, params);
		if (!ifSuccess(jsonObject)) {
			throw new RuntimeException(jsonObject.toJSONString());
		}
		JSONObject config = jsonObject.getJSONObject("data");
		if (jushuitanConfig == null) {
			jushuitanConfig = jushuitanConfigService.add(null, environment.getProperty("jushuitan.appKey"), environment.getProperty("jushuitan.appSecret"),
					config.getString("access_token"), config.getLong("expires_in"), config.getString("refresh_token"),
					config.getString("scope"), Constants.shipCallApi.replace("[domain]", domain),
					Constants.cancelCallApi.replace("[domain]", domain),
					Constants.repCallApi.replace("[domain]", domain), Constants.refundCallApi.replace("[domain]", domain));
		} else {
			jushuitanConfig.setAccessToken(config.getString("access_token"));
			jushuitanConfig.setExpiresIn(config.getLong("expires_in"));
			jushuitanConfig.setRefreshToken(config.getString("refresh_token"));
			jushuitanConfig.setScope(config.getString("scope"));
			jushuitanConfig.setShipCallApi(Constants.shipCallApi.replace("[domain]", domain));
			jushuitanConfig.setCancelCallApi(Constants.cancelCallApi.replace("[domain]", domain));
			jushuitanConfig.setRepCallApi(Constants.repCallApi.replace("[domain]", domain));
			jushuitanConfig.setRefundCallApi(Constants.refundCallApi.replace("[domain]", domain));
			jushuitanConfigService.updateById(jushuitanConfig);
		}
		return jushuitanConfig.getAccessToken();
	}

	public String getAccessToken() {
		JushuitanConfig jushuitanConfig = jushuitanConfigService.def();
		/**
		 * 判断是否过期
		 */
		if (ifExpire(jushuitanConfig)) {
			Map<String, Object> params = Maps.newConcurrentMap();
			params.put("app_key", environment.getProperty("jushuitan.appKey"));
			params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("grant_type", "refresh_token");
			params.put("charset", "utf-8");
			params.put("refresh_token", jushuitanConfig.getRefreshToken());
			params.put("scope", "scope");
			String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
			params.put("sign", sign);
			JSONObject	jsonObject = callapi(Constants.ACCESS_TOKEN_REFRESH_URL,params);
			if (!ifSuccess(jsonObject)) {
				throw new RuntimeException(jsonObject.toJSONString());
			}
			JSONObject config = jsonObject.getJSONObject("data");
			jushuitanConfig.setAccessToken(config.getString("access_token"));
			jushuitanConfig.setExpiresIn(config.getLong("expires_in"));
			jushuitanConfig.setRefreshToken(config.getString("refresh_token"));
			jushuitanConfig.setScope(config.getString("scope"));
			jushuitanConfigService.updateById(jushuitanConfig);
		}
		return jushuitanConfig.getAccessToken();
	}

	/**
	 * 发货订单上传
	 *
	 * @param upload
	 */
	public void orderUpload( JSONArray upload) {
		Map<String, Object> params = Maps.newConcurrentMap();
		params.put("biz", upload.toJSONString());
		JSONObject jsonObject = callapi(Constants.ORDER_UPLOAD, params, "jushuitan.orders.upload");
		{
			log.info("jushuitanorderUpload:{}",jsonObject.toJSONString());
		}
		if (!ifSuccess(jsonObject)) {
			throw new RuntimeException(jsonObject.toJSONString());
		}
	}

	private Boolean ifExpire(JushuitanConfig config) {
		if (config == null) {
			return true;
		}
		Long expires_in = config.getExpiresIn();
		Date time = DateTimeUtils.addSeconds(config.getGmtModify(), expires_in.intValue());
		if (DateTimeUtils.getNow().getTime() >= time.getTime()) {
			return true;
		}
		return false;
	}

	protected boolean ifSuccess(JSONObject jsonObject) {
		return StringUtils.equals(jsonObject.getString("code"), "0");
	}

	protected JSONObject callapi(String url, Map<String, Object> params) {
		HttpRequest httpRequest = HttpRequest.post(url);
		httpRequest.charset(Constants.CHARSET);
		httpRequest.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		httpRequest.form(params);
		try {
			HttpResponse response = httpRequest.send();
			String text = new String(response.bodyBytes(), "utf-8");
			return JSONObject.parseObject(text);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	protected JSONObject callapi(String url, Map<String, Object> params, String method) {
		params.put("app_key", environment.getProperty("jushuitan.appKey"));
		params.put("access_token", getAccessToken());
		params.put("charset", "utf-8");
		params.put("version", "2");
		params.put("timestamp", System.currentTimeMillis() / 1000);
		String sign = SignUtil.getSign(environment.getProperty("jushuitan.appSecret"), params);
		params.put("sign", sign);
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse(Constants.REQUEST_CHARSET);
		String requestBody=toRequestBody(params);
		{
			log.info("requestBody:{}",requestBody);
		}
		RequestBody body = RequestBody.create(mediaType,
				requestBody);
		Request request = new Request.Builder().url(url)
				.method("POST", body).addHeader("Content-Type", Constants.REQUEST_CHARSET)
				.build();
		try {
			Response response = client.newCall(request).execute();
			String text = new String(response.body().bytes(), "utf-8");
			return JSONObject.parseObject(text);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String toRequestBody(Map<String, Object> params) {
		StringBuffer buffer=new StringBuffer();
		for (String keySet : params.keySet()) {
			try {
				buffer.append(keySet).append("=").append(URLEncoder.encode(params.get(keySet).toString(),"utf-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return buffer.toString().substring(0, buffer.length() -1);
	}


    public static String getLocation(HttpServletRequest request) {
        UrlPathHelper helper = new UrlPathHelper();
        StringBuffer buff = request.getRequestURL();
        String uri = request.getRequestURI();
        String origUri = helper.getOriginatingRequestUri(request);
        buff.replace(buff.length() - uri.length(), buff.length(), origUri);
        String queryString = helper.getOriginatingQueryString(request);
        if (queryString != null) {
            buff.append("?").append(queryString);
        }

        return buff.toString();
    }

    public static HttpServletRequest getNativeRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }




	/**
	 * 历史发货订单上传
	 *
	 * @param upload
	 */
	public void historyOrderUpload( JSONArray upload,Integer dbName) {
		Map<String, Object> params = Maps.newConcurrentMap();
		params.put("biz", upload.toJSONString());
		JSONObject jsonObject = historyCallapi(Constants.ORDER_UPLOAD, params, "jushuitan.orders.upload",dbName);
		{
			log.info("jushuitanorderUpload:{}",jsonObject.toJSONString());
		}
		if (!ifSuccess(jsonObject)) {
			throw new RuntimeException(jsonObject.toJSONString());
		}
	}

	protected JSONObject historyCallapi(String url, Map<String, Object> params, String method,Integer dbName) {

		JushuitanConfig jushuitanConfig = jushuitanConfigService.getOne(new QueryWrapper<JushuitanConfig>().lambda().eq(JushuitanConfig ::getId,dbName));
		params.put("app_key", jushuitanConfig.getAppKey());
		params.put("access_token", historyGetAccessToken(dbName));
		params.put("charset", "utf-8");
		params.put("version", "2");
		params.put("timestamp", System.currentTimeMillis() / 1000);
		String sign = SignUtil.getSign(jushuitanConfig.getAppSecret(), params);
		params.put("sign", sign);
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse(Constants.REQUEST_CHARSET);
		String requestBody=toRequestBody(params);
		{
			log.info("requestBody:{}",requestBody);
		}
		RequestBody body = RequestBody.create(mediaType,
				requestBody);
		Request request = new Request.Builder().url(url)
				.method("POST", body).addHeader("Content-Type", Constants.REQUEST_CHARSET)
				.build();
		try {
			Response response = client.newCall(request).execute();
			String text = new String(response.body().bytes(), "utf-8");
			return JSONObject.parseObject(text);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public String  historyGetAccessToken(Integer dbName) {
		JushuitanConfig jushuitanConfig = jushuitanConfigService.getOne(new QueryWrapper<JushuitanConfig>().lambda().eq(JushuitanConfig ::getId,dbName));
		/**
		 * 判断是否过期
		 */
		if (ifExpire(jushuitanConfig)) {
			Map<String, Object> params = Maps.newConcurrentMap();
			params.put("app_key", jushuitanConfig.getAppKey());
			params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("grant_type", "refresh_token");
			params.put("charset", "utf-8");
			params.put("refresh_token", jushuitanConfig.getRefreshToken());
			params.put("scope", "scope");
			String sign = SignUtil.getSign(jushuitanConfig.getAppSecret(), params);
			params.put("sign", sign);
			JSONObject	jsonObject = callapi(Constants.ACCESS_TOKEN_REFRESH_URL,params);
			if (!ifSuccess(jsonObject)) {
				throw new RuntimeException(jsonObject.toJSONString());
			}
			JSONObject config = jsonObject.getJSONObject("data");
			jushuitanConfig.setAccessToken(config.getString("access_token"));
			jushuitanConfig.setExpiresIn(config.getLong("expires_in"));
			jushuitanConfig.setRefreshToken(config.getString("refresh_token"));
			jushuitanConfig.setScope(config.getString("scope"));
			jushuitanConfigService.updateById(jushuitanConfig);
		}
		return jushuitanConfig.getAccessToken();
	}



	@Resource
	private JushuitanConfigService jushuitanConfigService;
	@Autowired
	private Environment environment;
}
