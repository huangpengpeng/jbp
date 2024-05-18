package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.task.order.OrderPaySuccessTask;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.order.Order;
import com.jbp.service.condition.CapaXsInvitationLine2Handler;
import com.jbp.service.condition.CapaXsInvitationLineHandler;
import com.jbp.service.product.comm.FeelGratefulCapaCommHandler;
import com.jbp.service.product.comm.OfflineSubsidyCommHandler;
import com.jbp.service.product.comm.RiseCapaDifferentialCommHandler;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.CapaXsService;
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

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JbpAdminApplication.class, args);
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

//
//        FeelGratefulCapaCommHandler orderPaySuccessTask =run.getBean(FeelGratefulCapaCommHandler.class);
//
//        OrderService order =run.getBean(OrderService.class);
//
//
//        orderPaySuccessTask.orderSuccessCalculateAmt(order.getByOrderNo("PT656171602506018926726"),null,null);
//
//
//
//        RiseCapaDifferentialCommHandler orderPaySuccessTask =run.getBean(RiseCapaDifferentialCommHandler.class);
//
//        OrderService order =run.getBean(OrderService.class);
//
//
//        OrderDetailService orderDetails =run.getBean(OrderDetailService.class);
//
//
//        orderPaySuccessTask.orderSuccessCalculateAmt(order.getByOrderNo("PT718171602496886734358"),orderDetails.getByOrderNo("PT718171602496886734358"),null);

    }

}
