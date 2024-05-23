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
import com.yeepay.yop.sdk.security.DigitalEnvelopeUtils;
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

//          String withdrawNo = StringUtils.N_TO_10("LZT_WD_");
//         WithdrawCardQueryResult card = yopService.withdrawCardQuery("10090316825");
//        System.out.println(JSONObject.toJSONString(card));

//         WithdrawOrderResult withdrawOrderResult = yopService.withdrawOrder("10090316790", withdrawNo,
//                card.getBankCardAccountList().get(0).getBindCardId(), "0.2", "https://applet.dys.ink/yop/ew");
//
//         System.out.println(JSONObject.toJSONString(withdrawOrderResult));




        System.out.println("111");


    }

    private static void register(YopService yopService) {
        String registerMicroRequestNo = StringUtils.N_TO_10("LZT_RS_");
        RegisterMicroResult registerMicroResult = yopService.registerMicro(registerMicroRequestNo, "冯开英", "429005199305060899", "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E6%AD%A3.jpg",
                "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E5%8F%8D.jpg",
                "15871898210", "420000", "429000", "429005",
                "竹根滩镇黑流渡村一组", "6228480329262404075", "ABC", "http://fky.natapp1.cc/yop/registerMicro");

        System.out.println(JSONObject.toJSONString(registerMicroResult));


        RegisterQueryResult result = yopService.registerQuery("LZT_RS_157329444997738");
        System.out.println(JSONObject.toJSONString(result));
    }


    private static void queryOpenBank(YopService yopService) {
        BankAccountQueryResult bankAccountQueryResult = yopService.bankAccountQuery("10090328093", "BO_65808892927832");
        System.out.println(JSONObject.toJSONString(bankAccountQueryResult));
    }


    /**
     * 10090316790
     * 10090316825
     * 10090319762
     * 10090320189
     */
    private static void openBank(YopService yopService) {
        // {"authType":"NO_AUTH","orderNo":"6dfaa3a38bf84ba7aca8228158ba10ab","requestNo":"BO_65808892927832","returnCode":"AM00000","status":"PROCESS"}
        BankAccountOpenParams params = new BankAccountOpenParams();
        String requestNo2 = StringUtils.N_TO_10("BO_");
        System.out.println(requestNo2);
        params.setRequestNo(requestNo2);
        params.setMerchantNo("10090333092");
        params.setMerchantName("海口龙华郦冷琴百货店（个体工商户）");
        params.setOpenBankCode("SUNINGBANK_MULTICHANNEL");
        params.setOpenAccountType("INDIVIDUAL_BUSINESS_TYPE");
        params.setCertificateType("BUSINESS_LICENCE");
        params.setCertificateNo("92460000MADG8WF957");
        params.setNotifyUrl("https://applet.dys.ink/yop/ew");

        SnMultiChannelOpenAccountDTO dto = new SnMultiChannelOpenAccountDTO();

        dto.setSocialCreditCodeImageUrl("http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E8%90%A5%E4%B8%9A%E6%89%A7%E7%85%A7.png");
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
