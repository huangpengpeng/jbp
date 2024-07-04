package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.vo.WeChatMiniAuthorizeVo;
import com.jbp.service.service.WechatService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.service.impl.WechatServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.BigDecimal;


@EnableAsync //开启异步调用	
@EnableSwagger2
@Configuration
@EnableTransactionManagement
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.jbp"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpAdminApplication {

    public static void main(String[] args){
        ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);
        System.out.println("ok");
        LztTransferMorepyeeService lztTransferMorepyeeService = run.getBean(LztTransferMorepyeeService.class);

        String payCode = "LZT_NDF_76480031869492_5";
        LztTransferMorepyee result = lztTransferMorepyeeService.transferMorepyee(17, "10090455403", payCode, BigDecimal.valueOf(2), null,"服务费", null, null, "10090336294", "60.177.228.155", "服务费");
    }

}
