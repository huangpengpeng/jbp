package com.jbp.front;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.AcctSerialResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.InvitationScoreFlowService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.UserCapaService;
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

import java.util.Date;
import java.util.List;

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

        MerchantService merchantService = run.getBean(MerchantService.class);
        LztAcctService lztAcctService = run.getBean(LztAcctService.class);
        LztService lztService = run.getBean(LztService.class);

        LztAcct lztAcct = lztAcctService.getByUserId("gz0002");

        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        Date now = DateTimeUtils.getNow();
        String dateStart = DateTimeUtils.format(DateTimeUtils.addDays(now, -10), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        String endStart = DateTimeUtils.format(now, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        AcctSerialResult result = lztService.queryAcctSerial(payInfo.getOidPartner(), payInfo.getPriKey(), "gz0002",
                LianLianPayConfig.UserType.getCode(lztAcct.getUserType()), dateStart, endStart, null, "2");


        System.out.println("ok");

    }


}
