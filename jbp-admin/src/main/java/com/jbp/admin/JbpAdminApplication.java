package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;

import com.jbp.common.model.agent.UserOfflineSubsidy;
import com.jbp.common.model.city.CityRegion;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.CityRegionService;
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
//
//        UserOfflineSubsidyService userOfflineSubsidyService = run.getBean(UserOfflineSubsidyService.class, args);
//        CityRegionService cityRegionService = run.getBean(CityRegionService.class,args);
//        List<UserOfflineSubsidy> userOfflineSubsidyList = userOfflineSubsidyService.list();
//        for (UserOfflineSubsidy userOfflineSubsidy : userOfflineSubsidyList) {
//
//            if (StringUtils.isNotEmpty(userOfflineSubsidy.getProvince())){
//                CityRegion province = cityRegionService.getByRegionName(userOfflineSubsidy.getProvince(), 1, 1);
//                userOfflineSubsidy.setProvinceId(province != null ? province.getRegionId() : 0);
//                if (StringUtils.isNotEmpty(userOfflineSubsidy.getCity()) && province != null){
//                    CityRegion city = cityRegionService.getByRegionName(userOfflineSubsidy.getCity(), province.getRegionId(), 2);
//                     userOfflineSubsidy.setCityId(city != null ? city.getRegionId() : 0);
//                    if (StringUtils.isNotEmpty(userOfflineSubsidy.getArea()) && city != null){
//                        CityRegion area = cityRegionService.getByRegionName(userOfflineSubsidy.getArea(), city.getRegionId(), 3);
//                            userOfflineSubsidy.setAreaId(area != null ? area.getRegionId() : 0);
//                    }
//                }
//            }
//            userOfflineSubsidyService.updateById(userOfflineSubsidy);
//
//        }
//






    }
}
