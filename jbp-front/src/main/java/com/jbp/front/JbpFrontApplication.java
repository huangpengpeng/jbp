package com.jbp.front;

import com.jbp.common.constants.RedisConstants;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.utils.RedisUtil;
import com.jbp.front.service.LoginService;
import com.jbp.service.service.AsyncService;
import com.jbp.service.service.PayService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.service.agent.UserRelationFlowService;
import com.jbp.service.service.agent.UserRelationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //去掉数据源
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class}) //去掉数据源
@ComponentScan(basePackages = {"com.jbp", "com.jbp.front"})
@MapperScan(basePackages = {"com.jbp.**.dao"})
public class JbpFrontApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JbpFrontApplication.class, args);
//        AsyncService bean = run.getBean(AsyncService.class);
//        bean.orderPaySuccessSplit("PT807170910667612573741");
//        PayService payService = run.getBean(PayService.class);
//        payService.payAfterProcessingTemp("PT807170910667612573741");

        UserInvitationService userInvitationService = run.getBean(UserInvitationService.class);
        UserInvitationFlowService userInvitationFlowService = run.getBean(UserInvitationFlowService.class);
//        List<UserInvitation> noFlowList = userInvitationService.getNoFlowList();
//        for (UserInvitation userInvitation : noFlowList) {
//            userInvitationFlowService.refreshFlowAndTeam(userInvitation.getUId());
//        }

//        userInvitationFlowService.clear(95);

//        UserRelationService userRelationService = run.getBean(UserRelationService.class);
//        UserRelationFlowService userRelationFlowService = run.getBean(UserRelationFlowService.class);
//        List<UserRelation> noFlowList = userRelationService.getNoFlowList();
//        for (UserRelation userRelation : noFlowList) {
//            userRelationFlowService.refresh(userRelation.getUId());
//        }

    }
}
