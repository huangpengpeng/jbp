package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.controller.tank.TankStoreRelationAct;
import com.jbp.admin.task.order.OrderAiServerTask;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderInvoice;
import com.jbp.common.model.user.User;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.service.condition.CapaXsInvitationLine2Handler;
import com.jbp.service.condition.CapaXsJoinCapaHandler;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.product.comm.*;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.*;

import com.jbp.common.model.city.CityRegion;
import com.jbp.common.utils.StringUtils;

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





    }
}
