package com.jbp.front.service.captcha;

import java.util.Properties;

import com.jbp.common.captcha.model.common.ResponseModel;
import com.jbp.common.captcha.model.vo.CaptchaVO;

/**
 * 验证码服务接口
 * @author lide1202@hotmail.com
 * @date 2020-05-12
 */
public interface CaptchaService {
    /**
     * 配置初始化
     */
    void init(Properties config);

    /**
     * 获取验证码
     * @param captchaVO
     * @return
     */
    ResponseModel get(CaptchaVO captchaVO);

    /**
     * 核对验证码(前端)
     * @param captchaVO
     * @return
     */
    ResponseModel check(CaptchaVO captchaVO);

    /**
     * 二次校验验证码(后端)
     * @param captchaVO
     * @return
     */
    ResponseModel verification(CaptchaVO captchaVO);

    /***
     * 验证码类型
     * 通过java SPI机制，接入方可自定义实现类，实现新的验证类型
     * @return
     */
    String captchaType();

	/**
	 * 历史资源清除(过期的图片文件，生成的临时图片...)
	 * @param config 配置项 控制资源清理的粒度
	 */
	void destroy(Properties config);
}
