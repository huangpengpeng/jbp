package com.jbp.front;

import com.beust.jcommander.internal.Lists;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.request.agent.ClearingRequest;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingUserService;
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
        Environment bean = run.getBean(Environment.class);
        System.out.println("spring.datasource.url=" + bean.getProperty("spring.datasource.url"));
        System.out.println("启动完成");

//        ClearingFinalService clearingFinalService = run.getBean(ClearingFinalService.class);
//        ClearingRequest req = new ClearingRequest();
//        req.setCommType(16);
//        req.setCommName("扩展佣金");
//        req.setStartTime("20240401");
//        req.setEndTime("20240410");
//        req.setIfImportUser(true);
//
//        List<ClearingUserImportDto> userList = Lists.newArrayList();
//        ClearingUserImportDto dto4 = new ClearingUserImportDto();
//        dto4.setAccount("FNY43376638");
//        dto4.setWeight(BigDecimal.valueOf(4));
//        dto4.setLevel(1L);
//        dto4.setLevelName("默认");
//        userList.add(dto4);
//        ClearingUserImportDto dto1 = new ClearingUserImportDto();
//        dto1.setAccount("FNY86978955");
//        dto1.setWeight(BigDecimal.valueOf(1));
//        dto1.setLevel(1L);
//        dto1.setLevelName("默认");
//        userList.add(dto1);
//        ClearingUserImportDto dto12 = new ClearingUserImportDto();
//        dto12.setAccount("FNY22233559");
//        dto12.setWeight(BigDecimal.valueOf(12));
//        dto12.setLevel(1L);
//        dto12.setLevelName("默认");
//        userList.add(dto12);
//        req.setUserList(userList);
//        clearingFinalService.syncOneKeyClearing(req);

    }
}
