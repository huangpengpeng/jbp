package com.jbp.admin;

import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.yop.result.AccountRechargeResult;
import com.jbp.common.yop.result.AllAccountBalanceQueryResult;
import com.jbp.service.service.SystemAdminService;
import com.jbp.service.service.YopService;
import com.jbp.service.util.StringUtils;
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

import java.util.List;


@EnableAsync //开启异步调用	
@EnableSwagger2
@Configuration
@EnableTransactionManagement
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.jbp"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpAdminApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);
        System.out.println("ok");
    }

}
