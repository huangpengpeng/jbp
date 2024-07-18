package com.jbp.admin;

import com.alibaba.fastjson.JSONObject;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.captcha.util.RandomUtils;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.params.PapAgreeQueryParams;
import com.jbp.common.lianlian.result.BindCardH5ApplyResult;
import com.jbp.common.lianlian.result.LztPapAgreeApplyResult;
import com.jbp.common.lianlian.result.LztTransferResult;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.vo.WeChatMiniAuthorizeVo;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.WechatService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztPayChannelService;
import com.jbp.service.service.agent.LztSalaryTransferService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.service.impl.LianLianPayServiceImpl;
import com.jbp.service.service.impl.WechatServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;


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

//        DegreePayService degreePayService = run.getBean(DegreePayService.class);
//        LztAcctService lztAcctService = run.getBean(LztAcctService.class);
//        LianLianPayService lianLianPayService = run.getBean(LianLianPayService.class);
//        LztPayChannelService lztPayChannelService = run.getBean(LztPayChannelService.class);
//        BindCardH5ApplyResult bindCardH5ApplyResult = run.getBean(LianLianPayService.class).bindCardH5Apply("jiangming", "INNERUSER", "CHANGE_BIND_CARD", "LZT_M_20240716_11113",
//                "https://join.jubaopeng.cc");
//        LztAcct lztAcct = lztAcctService.getByUserId("ruicirenli");
//        LztPayChannel payChannel = lztPayChannelService.getByMer(lztAcct.getMerId(), lztAcct.getPayChannelType());
//        LztPapAgreeApplyResult lztPapAgreeApplyResult = lianLianPayService.papAgreeApply(payChannel.getPartnerId(), payChannel.getPriKey(), lztAcct.getUserId(), null);
//        System.out.println(JSONObject.toJSONString(lztPapAgreeApplyResult));


//        PapAgreeQueryParams params  = new PapAgreeQueryParams(LLianPayDateUtils.getTimestamp(), payChannel.getPartnerId(), "ruicirenli", "LZT_PA_99170906256415");
//        degreePayService.papAgreeQuery( params, payChannel.getPriKey());

//        LztAcctService lztAcctService = run.getBean(LztAcctService.class);
//        LztAcct lztAcct = lztAcctService.getByUserId("ruicirenli");
//        LztSalaryTransferService lztSalaryTransferService = run.getBean(LztSalaryTransferService.class);
//        lztSalaryTransferService.send(lztAcct, 24L, "60.186.153.50");
    }

}
