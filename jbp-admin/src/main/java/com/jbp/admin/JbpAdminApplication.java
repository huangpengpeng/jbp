package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.service.service.agent.LztFundTransferService;
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

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);
        System.out.println("ok");

        LztFundTransferService lztFundTransferService = run.getBean(LztFundTransferService.class);

        LztFundTransfer lztFundTransfer = lztFundTransferService.fundTransfer(14, "d0002", "80094800000009870000000010", BigDecimal.valueOf(3000000), "付款");
        System.out.println(lztFundTransfer);
    }

}
