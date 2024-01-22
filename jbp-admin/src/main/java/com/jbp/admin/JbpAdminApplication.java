package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserSearchRequest;
import com.jbp.service.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableAsync //开启异步调用	
@EnableSwagger2
@Configuration
@EnableTransactionManagement
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class}) 
@ComponentScan(basePackages = {"com.jbp"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
//@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class JbpAdminApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);

//        final UserService bean = run.getBean(UserService.class);
//
//        PageParamRequest pageParamRequest =  new PageParamRequest();
//        pageParamRequest.setPage(1);
//        pageParamRequest.setLimit(15);
//        bean.getPlatformPage(new UserSearchRequest(), pageParamRequest);
        System.out.println("ok");

    }

}
