package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.agent.WalletService;
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
        System.out.println("spring.datasource.url="+ bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

        WalletService walletService = run.getBean(WalletService.class);
        walletService.init();

//        FundClearingService fundClearingService = run.getBean(FundClearingService.class);
//        UserService userService = run.getBean(UserService.class);
//        List<FundClearing> list = fundClearingService.list();
//        for (FundClearing fundClearing : list) {
//            User user = userService.getById(fundClearing.getUid());
//            UserInfo userInfo = new UserInfo(user.getNickname(), user.getAccount());
//            fundClearing.setUserInfo(userInfo);
//            fundClearingService.updateById(fundClearing);
//        }
//
//        InvitationScoreService invitationScoreService = run.getBean(InvitationScoreService.class);
//        invitationScoreService.init();

//        PayService payService = run.getBean(PayService.class);

//        payService.payAfterProcessingTemp(
//                "PT882171122922518255967"
//        );



    }

}
