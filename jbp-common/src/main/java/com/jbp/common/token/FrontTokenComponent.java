package com.jbp.common.token;

import cn.hutool.core.util.StrUtil;

import com.jbp.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jbp.common.constants.Constants;
import com.jbp.common.constants.RedisConstants;
import com.jbp.common.encryptapi.SecretKeyConfig;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.utils.RequestUtil;
import com.jbp.common.vo.LoginUserVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
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
@Component
public class FrontTokenComponent {

	@Autowired
	private SecretKeyConfig secretKeyConfig;
	
    @Resource
    private RedisUtil redisUtil;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    private static final Long MILLIS_MINUTE = 60 * 1000L;

    // 令牌有效期（默认30分钟） todo 调试期改为5小时
//    private static final int expireTime = 30;
    private static final int expireTime = 5 * 60;

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StrUtil.isNotBlank(token)) {
            String userKey = getTokenKey(token);
            redisUtil.delete(userKey);
        }
    }

	/**
	 * 创建令牌
	 *
	 * @param user 用户信息
	 * @return 令牌
	 */
	public String createToken(User user) {
		String token = UUID.randomUUID().toString().replace("-", "") + "@" + secretKeyConfig.encryptStr(secretKeyConfig.encryptStr(getCheck()));
		redisUtil.set(getTokenKey(token), user.getId(), Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
        redisUtil.set("loginToken" + user.getId(), token);
		return token;
	}

	/**
	 * 验证令牌有效期，相差不足20分钟，自动刷新缓存
	 *
	 * @param loginUser LoginUserVo
	 */
    public void verifyToken(LoginUserVo loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserVo loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisUtil.set(userKey, loginUser, (long) expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(token) && token.startsWith(RedisConstants.USER_TOKEN_REDIS_KEY_PREFIX)) {
            token = token.replace(RedisConstants.USER_TOKEN_REDIS_KEY_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return RedisConstants.USER_TOKEN_REDIS_KEY_PREFIX + uuid;
    }

    /**
     * 推出登录
     *
     * @param request HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        delLoginUser(token);
    }

    /**
     * 获取当前登录用户id
     */
    public Integer getUserId() {
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        return redisUtil.get(getTokenKey(token));
    }
    
    public String getCheck() {
    	  String checkCode =RequestUtil.getParams("checkCode");
    	  return secretKeyConfig.decryptStr(checkCode);
    }

    public Boolean check(String token) {

        try {
            boolean exists = redisUtil.exists(getTokenKey(token));
            if (exists) {
                Integer uid = redisUtil.get(getTokenKey(token));
                redisUtil.set(getTokenKey(token), uid, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }
}
