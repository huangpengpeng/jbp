package com.jbp.admin;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.admin.task.user.UserCapaTask;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.model.user.User;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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
        System.out.println("ok");
        UserCapaXsService userCapaXsService = run.getBean(UserCapaXsService.class);

//        userCapaXsService.riseCapaXs(95);
//
//        StringRedisTemplate stringRedisTemplate = run.getBean(StringRedisTemplate.class);
//        String s = stringRedisTemplate.opsForValue().get("refreshUserCapa");
//        stringRedisTemplate.opsForValue().set("refreshUserCapa","1");
//        stringRedisTemplate.delete("refreshUserCapa");
//        s = stringRedisTemplate.opsForValue().get("refreshUserCapa");
        System.out.println("ok");
    }

}
