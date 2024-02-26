package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.service.service.agent.ChannelCardService;
import com.jbp.service.service.agent.UserInvitationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
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
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.jbp"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpAdminApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);

//        final UserInvitationService bean = run.getBean(UserInvitationService.class);
//        bean.band(109, 99, false, true);
        System.out.println("ok");

    }

}
