package com.jbp.front;


import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.vo.JdPayAggregateCreateOrderResponse;
import com.jbp.common.jdpay.vo.JdPayRefundResponse;
import com.jbp.common.jdpay.vo.JdPaySendCommissionResponse;
import com.jbp.common.jdpay.vo.JdPayToPersonalWalletResponse;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.JdPayService;
import com.jbp.service.service.KqPayService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.impl.WalletServiceImpl;
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

import java.math.BigDecimal;
import java.util.Date;

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
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(JbpFrontApplication.class, args);
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

//        JdPayService jdPayService = run.getBean(JdPayService.class);
//        JdPay jdPay = run.getBean(JdPay.class);
//        new  JdPayDemo().createOrder(jdPay);
//        JdPaySendCommissionResponse response = jdPayService.sendCommission("CZ508172844134328134756", BigDecimal.valueOf(10));
//        System.out.println(JSONObject.toJSONString(response));

//        String merchantTradeNo = StringUtils.N_TO_10("TEST_");
//        JdPayToPersonalWalletResponse response = jdPayService.payToPersonalWallet(merchantTradeNo, "o*AAS0Jh_sIymSidiI44QlwgMIZmY2NFdqUb_DfR8Gvox58SPQi-4", BigDecimal.valueOf(1), "测试打款");
//        System.out.println(JSONObject.toJSONString(response));
//        run.getBean(WalletServiceImpl.class).init();
    }
}
