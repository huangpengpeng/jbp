package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.controller.agent.LztAcctController;
import com.jbp.common.lianlian.result.AcctBalList;
import com.jbp.common.lianlian.result.LztQueryAcctInfoResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctApply;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.response.AliBankcardResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


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
        System.out.println("ok");
//        LztAcctController bean = run.getBean(LztAcctController.class);
//        Date now = DateTimeUtils.getNow();
//        String dateStart = DateTimeUtils.format(DateTimeUtils.addDays(now, -10), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
//        String endStart = DateTimeUtils.format(now, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
//        CommonResult<CommonPage<AcctBalList>> page = bean.serialPage("gz0002", dateStart, endStart, null, 1);

//        LztTransferMorepyeeService bean = run.getBean(LztTransferMorepyeeService.class);
////        final Map<String, Object> info = bean.info(4);
////        System.out.println("ok");

    }

}
