package com.jbp.admin;

import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.task.order.OrderAutoReceiptTask;
import com.jbp.admin.task.user.UserCapaXsQueueTask;
import com.jbp.common.kqbill.result.KqPayQueryResult;
import com.jbp.common.model.agent.*;
import com.google.common.collect.Maps;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.condition.CapaXsInvitationLine2Handler;
import com.jbp.service.condition.CapaXsInviteOneLevelHandler;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.product.comm.CommCalculateResult;
import com.jbp.service.product.comm.ProductCommChain;
import com.jbp.service.service.KqPayService;
import com.jbp.service.service.OldcapaxsService;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.*;

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
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");


        KqPayService kqPayService = run.getBean(KqPayService.class);
        KqPayQueryResult result = kqPayService.queryPayResult("PT985172706701730859627");
        System.out.println(JSONObject.toJSONString(result));


    }
}
