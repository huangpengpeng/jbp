package com.jbp.admin;

import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.order.Order;
import com.jbp.service.product.profit.UserWalletHandler;
import com.jbp.admin.task.order.OrderPayResultSyncTask;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.PayCallbackService;
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





//
//        PayCallbackService payCallbackService = run.getBean(PayCallbackService.class);
//        String str = "{\"oid_partner\":\"402401020000015944\",\"payerInfo\":[{\"amount\":\"9600.00\",\"method\":\"ALIPAY_NATIVE\",\"payer_type\":\"USER\",\"payer_id\":\"cn111171\"}],\"txn_type\":\"GENERAL_CONSUME\",\"payeeInfo\":[{\"amount\":\"9600.00\",\"payee_id\":\"system_user_c_01\",\"payee_type\":\"USER\"}],\"orderInfo\":{\"total_amount\":\"9600.00\",\"txn_seqno\":\"PT671171134918065489563\",\"txn_time\":\"20240325185858\"},\"chnl_txno\":\"2024032568843418\",\"txn_status\":\"TRADE_SUCCESS\",\"accounting_date\":\"20240325\",\"finish_time\":\"20240325190231\",\"accp_txno\":\"2024032526034746\"}";
//         QueryPaymentResult result = JSONObject.parseObject(str, QueryPaymentResult.class);
//        payCallbackService.lianLianPayCallback(result);
//






    }

}
