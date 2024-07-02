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

//
//        ProductCommChain productCommChain = run.getBean(ProductCommChain.class);
//       OrderDetailService orderDetailService = run.getBean(OrderDetailService.class);
//       OrderService orderService = run.getBean(OrderService.class);
//     List<Order> orderList  = orderService.list(new QueryWrapper<Order>().lambda().last( " where order_no in ('PT815171835807157093876',\n" +
//             "'PT875171835985197235710',\n" +
//             "'PT652171836053115713995',\n" +
//             "'PT784171836846836374758',\n" +
//             "'PT902171859699572657269',\n" +
//             "'PT685171861009726771947',\n" +
//             "'PT410171862048186980692',\n" +
//             "'PT230171863509791114695',\n" +
//             "'PT646171867210688466835',\n" +
//             "'PT803171867613903758920',\n" +
//             "'PT758171868261070476675',\n" +
//             "'PT391171869095769546089',\n" +
//             "'PT841171869823510817959',\n" +
//             "'PT920171869911584054967',\n" +
//             "'PT480171870142325468560',\n" +
//             "'PT336171870421999368253',\n" +
//             "'PT341171871139496760635',\n" +
//             "'PT511171871248452768063',\n" +
//             "'PT605171871280695943688',\n" +
//             "'PT798171871583842253300',\n" +
//             "'PT699171877006141130938',\n" +
//             "'PT747171879431523475509',\n" +
//             "'PT865171885730484431965',\n" +
//             "'PT416171887561261629018',\n" +
//             "'PT716171887711072387476',\n" +
//             "'PT507171888855673673442',\n" +
//             "'PT748171892937348522162',\n" +
//             "'PT862171902431529387201',\n" +
//             "'PT763171903758376282109',\n" +
//             "'PT206171910767711337119',\n" +
//             "'PT697171913065291314578',\n" +
//             "'PT576171913928832116860',\n" +
//             "'PT141171921031202523475',\n" +
//             "'PT901171923088378888591',\n" +
//             "'PT948171938365874745775',\n" +
//             "'PT605171938369218560271',\n" +
//             "'PT274171953943755216281',\n" +
//             "'PT221171963023828251586',\n" +
//             "'PT907171963353180528970',\n" +
//             "'PT712171964796539256157',\n" +
//             "'PT842171964921347523839',\n" +
//             "'PT507171975178009267749',\n" +
//             "'PT627171979255795374544'\n)" ));
//
//     for(Order order : orderList){
//         List<OrderDetail> platOrderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
//         LinkedList<CommCalculateResult> commList = new LinkedList<>();
//
//         productCommChain.orderSuccessCalculateAmt(order,platOrderDetailList,commList);
//     }



    }
}
