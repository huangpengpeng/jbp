package com.jbp.front;

import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.service.service.PayCallbackService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.agent.WalletService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(JbpFrontApplication.class, args);
        Environment bean = run.getBean(Environment.class);
//
//        WalletService walletService = run.getBean(WalletService.class);
//        walletService.init();

//        PayService payService = run.getBean(PayService.class);
//        payService.payAfterProcessingTemp(
//                "PT371171136628874380221"
//        );

        System.out.println("spring.datasource.url="+ bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

//        PayCallbackService payCallbackService = run.getBean(PayCallbackService.class);
//        String str = "{\"accp_txno\":\"2024032423682932\",\"oid_partner\":\"402401020000015944\",\"orderInfo\":{\"total_amount\":\"200.00\",\"txn_seqno\":\"CZ181171126647837894714\",\"txn_time\":\"20240324154758\"},\"payeeInfo\":[{\"amount\":\"200.00\",\"payee_id\":\"system_user_c_01\",\"payee_type\":\"USER\"}],\"ret_code\":\"0000\",\"ret_msg\":\"交易成功\",\"txn_status\":\"TRADE_WAIT_PAY\",\"txn_type\":\"GENERAL_CONSUME\"}";
//
//        final String s = payCallbackService.lianLianPayCallback(JSONObject.parseObject(str, QueryPaymentResult.class));



    }

}
