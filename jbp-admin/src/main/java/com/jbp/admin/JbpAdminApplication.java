package com.jbp.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.OrdersFundSummary;
import com.google.common.collect.Maps;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.Oldcapaxs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.product.comm.CommCalculateResult;
import com.jbp.service.product.comm.ProductCommChain;
import com.jbp.service.service.OldcapaxsService;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.OrdersFundSummaryService;
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

import java.math.BigDecimal;

import java.util.*;
import java.util.stream.Collectors;


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
        FundClearingService fundClearingService = run.getBean(FundClearingService.class);

        OrdersFundSummaryService ordersFundSummaryService = run.getBean(OrdersFundSummaryService.class);

        List<OrdersFundSummary> list =  ordersFundSummaryService.list();
        int i=0 ;
        List<String> list1 =new ArrayList<>();
        list1.add("已创建");
        list1.add("待审核");
        list1.add("待出款");
        list1.add("已出款");

        for(OrdersFundSummary ordersFundSummary : list){
            i++;
            List<FundClearing> fundClearings = fundClearingService.getByExternalNo(ordersFundSummary.getOrdersSn(),list1);
            BigDecimal commamt  = BigDecimal.ZERO;
            for(FundClearing fundClearing : fundClearings){
                commamt = commamt.add(fundClearing.getCommAmt());
            }
            ordersFundSummary.setCommAmt(commamt);
            ordersFundSummaryService.updateById(ordersFundSummary);
            System.out.println("初始化"+ i);
        }






    }
}
