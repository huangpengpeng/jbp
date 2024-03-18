package com.jbp.front;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.google.common.collect.Lists;
import com.jbp.service.product.comm.CollisionCommHandler;
import com.jbp.service.product.comm.DepthCommHandler;
import com.jbp.service.product.comm.DirectInvitationHandler;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.impl.OrderTaskServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 程序主入口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@EnableAsync //开启异步调用
@EnableSwagger2
@Configuration
@EnableTransactionManagement
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class, DataSourceAutoConfiguration.class}) //去掉数据源
@ComponentScan(basePackages = {"com.jbp", "com.jbp.front"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpFrontApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JbpFrontApplication.class, args);

        final CollisionCommHandler bean = run.getBean(CollisionCommHandler.class);


        final OrderTaskServiceImpl orderTaskService = run.getBean(OrderTaskServiceImpl.class);


        final PayService payService = run.getBean(PayService.class);

//        bean.orderSuccessCalculateAmt(orderService.getByOrderNo("PT166171073091110928243"), Lists.newLinkedList());


        payService.payAfterProcessingTemp("PT692171074429672794771");
        System.out.println("ok");

    }


}
