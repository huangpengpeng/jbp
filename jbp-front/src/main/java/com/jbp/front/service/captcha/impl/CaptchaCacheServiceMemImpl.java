package com.jbp.front.service.captcha.impl;

import com.jbp.common.captcha.util.CacheUtil;
import com.jbp.front.service.captcha.CaptchaCacheService;

public class CaptchaCacheServiceMemImpl implements CaptchaCacheService {
    @Override
    public void set(String key, String value, long expiresInSeconds) {

        CacheUtil.set(key, value, expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return CacheUtil.exists(key);
    }

    @Override
    public void delete(String key) {
        CacheUtil.delete(key);
    }

    @Override
    public String get(String key) {
        return CacheUtil.get(key);
    }

	@Override
	public Long increment(String key, long val) {
    	Long ret = Long.valueOf(CacheUtil.get(key))+val;
		CacheUtil.set(key,ret+"",0);
		return ret;
	}

	@Override
    public String type() {
        return "local";
    }
}
