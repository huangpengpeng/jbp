package com.jbp.admin.config;

import com.jbp.admin.filter.ResponseFilter;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.Constants;
import com.jbp.common.constants.UploadConstants;
import com.jbp.common.encryptapi.SignInterceptor;
import com.jbp.common.interceptor.ResultInterceptor;
import com.jbp.common.interceptor.SwaggerInterceptor;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.YopClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * token验证拦截器
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
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 这里使用一个Bean为的是可以在拦截器中自由注入，也可以在拦截器中使用SpringUtil.getBean 获取
    // 但是觉得这样更优雅

    @Autowired
    CrmebConfig crmebConfig;

    @Bean
    public ResponseFilter responseFilter() {
        return new ResponseFilter();
    }

    @Value("${swagger.basic.username}")
    private String username;
    @Value("${swagger.basic.password}")
    private String password;
    @Value("${swagger.basic.check}")
    private Boolean check;

	/**
	 * 添加自定义拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        // 添加token拦截器
        // addPathPatterns添加需要拦截的命名空间；
        // excludePathPatterns添加排除拦截命名空间
        registry.addInterceptor(new ResultInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new SignInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/publicly/**",
                        "/api/publicly/payment/callback/**",
                        "/api/admin/platform/getLoginPic",
                        "/api/admin/platform/login",
                        "/api/admin/merchant/sendCode",
                        "/api/admin/merchant/getLoginPic",
                        "/api/admin/merchant/login",
                        "/jmreport/desreport_/cdn/iview/fonts/**",
                        "**/admin/platform/getMenus",
                        "**/admin/merchant/city/region/city/tree",
                        "/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/**",
                        "/" + UploadConstants.DOWNLOAD_FILE_KEYWORD + "/**",
                        "/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/**"
                )
        ;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        /** 本地文件上传路径 */
        registry.addResourceHandler(UploadConstants.UPLOAD_FILE_KEYWORD + "/**")
                .addResourceLocations("file:" + crmebConfig.getImagePath() + "/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/");

        registry.addResourceHandler(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/**")
                .addResourceLocations("file:" + crmebConfig.getImagePath() + "/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/");

    }

    @Bean
    public FilterRegistrationBean filterRegister() {
        //注册过滤器
        FilterRegistrationBean registration = new FilterRegistrationBean(responseFilter());
        // 仅仅api前缀的请求才会拦截
        registration.addUrlPatterns("/api/*");
        registration.addUrlPatterns("/jmreport/desreport_/corelib/*");
        return registration;
    }

    /* 必须在此处配置拦截器,要不然拦不到swagger的静态资源 */
    @Bean
    @ConditionalOnProperty(name = "swagger.basic.enable", havingValue = "true")
    public MappedInterceptor getMappedInterceptor() {
        return new MappedInterceptor(new String[]{"/doc.html", "/webjars/**"}, new SwaggerInterceptor(username, password, check));
    }

    @Bean
    public YopClient yopClient(){
        return YopClientBuilder.builder().build();
    }


}
