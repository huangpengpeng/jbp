package com.jbp.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.service.product.comm.CommCalculateResult;
import com.jbp.service.product.comm.ProductCommChain;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
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

import java.util.LinkedList;
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


        ProductCommChain productCommChain = run.getBean(ProductCommChain.class);
       OrderDetailService orderDetailService = run.getBean(OrderDetailService.class);
       OrderService orderService = run.getBean(OrderService.class);
     List<Order> orderList  = orderService.list(new QueryWrapper<Order>().lambda().last( " where order_no in ('PT431171855033283233516',\n" +
             "'PT838171871165760286922',\n" +
             "'PT752171871193261926697',  \n" +
             "'PT395171886316622891339',  \n" +
             "'PT301171888526886374487')" ));

     for(Order order : orderList){
         List<OrderDetail> platOrderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
         LinkedList<CommCalculateResult> commList = new LinkedList<>();

         productCommChain.orderSuccessCalculateAmt(order,platOrderDetailList,commList);
     }



    }
}
