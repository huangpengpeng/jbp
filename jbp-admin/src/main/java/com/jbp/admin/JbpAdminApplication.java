package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.model.agent.Oldcapaxs;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.user.User;
import com.jbp.service.condition.CapaXsInvitationLine2Handler;
import com.jbp.service.condition.ConditionChain;
import com.jbp.service.service.OldcapaxsService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaXsService;
import com.jbp.service.service.agent.UserCapaXsService;
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

//        ConditionChain capaXsInvitationLine2Handler = run.getBean(ConditionChain.class);
//
//        CapaXsService capaXsService = run.getBean(CapaXsService.class);
//        CapaXs capaXs =capaXsService.getById(6);
//        // 升级条件
//        List<RiseCondition> conditionList = capaXs.getConditionList();
//        for(RiseCondition riseCondition :conditionList){
//
//              capaXsInvitationLine2Handler.isOk(1002644,riseCondition);
//
//
//        }

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








    }

}
