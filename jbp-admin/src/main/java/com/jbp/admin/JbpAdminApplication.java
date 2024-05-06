package com.jbp.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.request.OrderRefundAuditRequest;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.agent.OrderSuccessMsgService;
import com.jbp.service.service.impl.RefundOrderServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

        OrderSuccessMsgService orderSuccessMsgService = run.getBean(OrderSuccessMsgService.class);
        List<OrderSuccessMsg> list = orderSuccessMsgService.list(new QueryWrapper<OrderSuccessMsg>().lambda().eq(OrderSuccessMsg::getExec, false));
        for (OrderSuccessMsg msg : list) {
            orderSuccessMsgService.exec(msg);
        }
    }

}
