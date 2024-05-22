package com.jbp.front;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.dto.BenefitDTO;
import com.jbp.common.yop.dto.SnMultiChannelOpenAccountDTO;
import com.jbp.common.yop.params.BankAccountOpenParams;
import com.jbp.common.yop.params.OnlineBankOrderParams;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.impl.LztAcctOpenServiceImpl;
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
        System.out.println("ok");

        YopService yopService = run.getBean(YopService.class);
//        AccountBalanceQueryResult accountBalanceQueryResult = yopService.accountBalanceQuery("10090108498");

        //10090316790
        //10090316825
        //10090319762
        //10090320189

        String requestNo = StringUtils.N_TO_10("LZT_NDF_");
        System.out.println(requestNo);
//        AccountTransferOrderResult accountTransferOrderResult = yopService.transferB2bOrder(requestNo, "10090316790", "10090316825", "0.02", null);
//        AccountTransferOrderQueryResult accountTransferOrderQueryResult = yopService.transferB2bOrderQuery("10090108498", requestNo);

         BankAccountQueryResult bankAccountQueryResult = yopService.bankAccountQuery("10090328093", "BO_139168410173851");
         System.out.println(JSONObject.toJSONString(bankAccountQueryResult));

        System.out.println("111");
    }

    private static void openBank(YopService yopService) {
        // {"authType":"NO_AUTH","orderNo":"12eb0802e8b740e3b402762e275c8a1b","requestNo":"BO_139168410173851","returnCode":"AM00000","status":"PROCESS"}
        BankAccountOpenParams params = new BankAccountOpenParams();
        String requestNo2 = StringUtils.N_TO_10("BO_");
        System.out.println(requestNo2);
        params.setRequestNo(requestNo2);
        params.setMerchantNo("10090328093");
        params.setMerchantName("海口龙华郦冷琴百货店（个体工商户）");
        params.setOpenBankCode("SUNINGBANK_MULTICHANNEL");
        params.setOpenAccountType("INDIVIDUAL_BUSINESS_TYPE");
        params.setCertificateType("BUSINESS_LICENCE");
        params.setCertificateNo("92460000MADG8WF957");
        params.setNotifyUrl("https://applet.dys.ink/yop/ew");

        SnMultiChannelOpenAccountDTO dto = new SnMultiChannelOpenAccountDTO();

        dto.setSocialCreditCodeImageUrl("http://batchatx-dys.oss-cn-shenzhen.aliyuncs.com/a94d53847c8a4001a433a2d60402df4e");
        dto.setSocialCreditCodeImageUrl(yopService.upload(dto.getSocialCreditCodeImageUrl()));

        dto.setLegalCardImageFont("http://batchatx-dys.oss-cn-shenzhen.aliyuncs.com/c9081f5aa23742229d83b0d1fc25cf9c");
        dto.setLegalCardImageFont(yopService.upload(dto.getLegalCardImageFont()));

        dto.setLegalCardImageBack("http://batchatx-dys.oss-cn-shenzhen.aliyuncs.com/6631ec634cff45f8905774f2e978c118");
        dto.setLegalCardImageBack(yopService.upload(dto.getLegalCardImageBack()));

        dto.setLegalMobileNo("13543341989");
        dto.setOperatorName("徐静");
        dto.setMobileNo("13823668660");

        List<BenefitDTO> benefitList = Lists.newArrayList();
        BenefitDTO benefit = new BenefitDTO();
        benefit.setBenefitName("甘富权");
        benefit.setBenefitIdType("ID_CARD");
        benefit.setBenefitIdNo("440982198604243196");
        benefit.setBenefitStartDate("20130104");
        benefit.setBenefitExpireDate("20330104");
        benefit.setBenefitImageFont("http://batchatx-dys.oss-cn-shenzhen.aliyuncs.com/c9081f5aa23742229d83b0d1fc25cf9c");
        benefit.setBenefitImageFont(yopService.upload(benefit.getBenefitImageFont()));

        benefit.setBenefitImageBack("http://batchatx-dys.oss-cn-shenzhen.aliyuncs.com/6631ec634cff45f8905774f2e978c118");
        benefit.setBenefitImageBack(yopService.upload(benefit.getBenefitImageBack()));

        benefit.setBenefitAddress("广东省化州市合江镇新车村195号之一");
        benefitList.add(benefit);

        dto.setBenefitDTOList(benefitList);
        params.setSnMultiChannelOpenAccountDTO(dto);

        BankAccountOpenResult result = yopService.bankAccountOpen(params);
        System.out.println(JSONObject.toJSONString(result));
    }


}
