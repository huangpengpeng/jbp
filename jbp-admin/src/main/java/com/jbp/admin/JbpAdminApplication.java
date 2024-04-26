package com.jbp.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.controller.tank.TankStoreRelationAct;
import com.jbp.common.model.user.User;
import com.jbp.service.product.comm.GroupThreeRetOneHandler;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.UserService;
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

import java.awt.*;


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
        System.out.println("spring.datasource.url="+ bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");


        TankStoreRelationAct groupThreeRetOneHandler = run.getBean(TankStoreRelationAct.class);
        groupThreeRetOneHandler.aa_3968();
        groupThreeRetOneHandler.aa_15866();
        groupThreeRetOneHandler.aa_3174_4();
        groupThreeRetOneHandler.aa_12692_8();

//        OrderService orderService = run.getBean(OrderService.class);
//
//        groupThreeRetOneHandler.orderSuccessCalculateAmt(orderService.getByOrderNo("PT434171379135267789373"),null);





    }




}
