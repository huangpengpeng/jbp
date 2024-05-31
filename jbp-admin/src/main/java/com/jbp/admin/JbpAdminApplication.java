package com.jbp.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.controller.tank.TankStoreRelationAct;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.Oldcapaxs;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderInvoice;
import com.jbp.common.model.user.User;
import com.jbp.service.condition.CapaXsInvitationLine2Handler;
import com.jbp.service.condition.CapaXsJoinCapaHandler;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.product.comm.*;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.CapaXsService;
import com.jbp.service.service.agent.UserCapaXsService;

import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.model.city.CityRegion;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.UserOfflineSubsidyService;
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
import java.util.Optional;


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
//        CapaParallelDifferentialCommHandler productCommChain = run.getBean(CapaParallelDifferentialCommHandler.class);
//       OrderDetailService orderDetailService = run.getBean(OrderDetailService.class);
//       OrderService orderService = run.getBean(OrderService.class);
//     Order order  = orderService.getOne(new QueryWrapper<Order>().lambda().eq(Order::getOrderNo,"PT578171705154600244593"));
////
//       List<OrderDetail> platOrderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
//         LinkedList<CommCalculateResult> commList = new LinkedList<>();
//////
//        productCommChain.orderSuccessCalculateAmt(order,platOrderDetailList,commList);
//        productCommChain.orderSuccessCalculateAmt(order,platOrderDetailList,commList);
//        OldcapaxsService oldcapaxsService = run.getBean(OldcapaxsService.class);
//         List<Oldcapaxs> list =  oldcapaxsService.list();
//        CapaXsService capaXsService = run.getBean(CapaXsService.class);
//        UserService userService = run.getBean(UserService.class);
//         int i= 0;
//         for(Oldcapaxs oldcapaxs :list ){
//
//             UserCapaXsService userCapaXsService = run.getBean(UserCapaXsService.class);
//             CapaXs capaXs =  capaXsService.getById(Integer.valueOf(oldcapaxs.getCapaId())+2);
//             userCapaXsService.saveOrUpdateCapa((Integer.valueOf(oldcapaxs.getAccount())+1000000), capaXs == null ? 1 : capaXs.getId(), false, "还原老系统等级", "还原老系统等级");
//
//             System.out.println(i++);
//         }
//
//        TankStoreRelationAct productCommChain = run.getBean(TankStoreRelationAct.class);
//
//        productCommChain.aa_8360();
//
//        OrderInvoiceService orderInvoiceService = run.getBean(OrderInvoiceService.class);
//        OrderService orderService = run.getBean(OrderService.class);
//        MerchantOrderService merchantOrderService = run.getBean(MerchantOrderService.class);
//        MerchantOrder merchantOrder =   merchantOrderService.getOneByOrderNo("PT698171616749741166621");
//        OrderInvoice orderInvoice = orderInvoiceService.getById(3580);
//        LogisticService logisticService = run.getBean(LogisticService.class);
//        logisticService.info(orderInvoice.getTrackingNumber(), null, Optional.ofNullable(orderInvoice.getExpressCode()).orElse(""), merchantOrder.getUserPhone());
//
//
//        orderService.getLogisticsInfoByMerchant(3564);


//
//
        OldcapaxsService oldcapaxsService = run.getBean(OldcapaxsService.class, args);
       List<Oldcapaxs> list = oldcapaxsService.list();
        UserService userService  = run.getBean(UserService.class,args);
        CapaXsService capaXsService  = run.getBean(CapaXsService.class,args);
        int i=0;
       for (Oldcapaxs oldcapaxs :list){
          CapaXs capaXs =  capaXsService.getByName(oldcapaxs.getCapaId());

           List<User> users =    userService.getByPhone(oldcapaxs.getPhone());
           if(!users.isEmpty()){
               continue;
           }

    //    List<User> users =    userService.getByPhone(oldcapaxs.getRphone());

           userService.registerNoBandPater2(oldcapaxs.getAccount(),oldcapaxs.getPhone(),"导入用户",capaXs == null ? null :capaXs.getId(),null);
            System.out.println(i++);
       }







    }
}
