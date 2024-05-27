package com.jbp.front;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.beust.jcommander.internal.Lists;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.dto.BenefitDTO;
import com.jbp.common.yop.dto.SnMultiChannelOpenAccountDTO;
import com.jbp.common.yop.params.BankAccountOpenParams;
import com.jbp.common.yop.params.MerchantInfoModifyParams;
import com.jbp.common.yop.params.RegisterMicroH5Params;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.LztAcctService;
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

        yopService.withdrawCardQuery("10090339599");

//        String registerMicroH5No = StringUtils.N_TO_10("YOP_OPEN_");
//        RegisterMicroH5Params params = new RegisterMicroH5Params();
//        params.setParentMerchantNo("10089625822");
//        params.setMobile("15871898210");
//        params.setRequestNo(registerMicroH5No);
//        params.setNotifyUrl("http://fky.natapp1.cc/yop/"+registerMicroH5No);
//        params.setReturnUrl("http://fky.natapp1.cc/yop/re");


//         RegisterMicroH5Result registerMicroH5Result = yopService.registerMicroH5(params);

//        withdraw(yopService);

//        transfer(yopService);

//        register(yopService);

//        queryOpenBank(yopService);

//        openBank(yopService);


//         RegisterQueryResult result = yopService.registerQuery("LZT_RS_103702359635243");

//        yopService.registerQuery("BO_134099286059998");
//        BO_55378505558965

//        yopService.registerQuery("ZZRWQY20240523140812868542");

//        String requestNo = StringUtils.N_TO_10("YOP_M_");
//        System.out.println(requestNo);
//        MerchantInfoModifyParams params  = new MerchantInfoModifyParams();
//        params.setRequestNo(requestNo);
//        params.setMerchantNo("10090333215");
//        params.setNotifyUrl("http://fky.natapp1.cc/yop/re");
//
//        params.setMerchantSubjectInfo("{ \"licenceUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/24/merchant-1716548379983-6a9f13cd-3451-4a2f-8b06-99b24b15bbb3-mcskpoWJKsalRPyFGzzv.jpg\", \"signName\":\"深圳市千浪文化传媒有限公司\", \"licenceNo\":\"91440300MA5H6C9T1J\", \"shortName\":\"深圳市千浪文化传媒有限公司\" }");
//        params.setMerchantCorporationInfo("{ \"legalName\":\"龙海浪\", \"legalLicenceType\":\"ID_CARD\", \"legalLicenceNo\":\"45032219970904051X\", \"legalLicenceFrontUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/24/merchant-1716548054675-680f3b00-a7ec-47f5-8f97-e062eef6cc01-zCAxMSwpzhyIfSOtgwyP.jpg\", \"legalLicenceBackUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/24/merchant-1716548053805-d39a29de-cfbe-45dc-8a71-28a1207f0a8d-EjBGOUAsfBDmMqYAlOLR.jpg\" }");
//        MerchantInfoModifyResult merchantInfoModifyResult = yopService.merchantInfoModify(params);
//        System.out.println(JSONObject.toJSONString(merchantInfoModifyResult));

        System.out.println("111");
//        SqlRunner.db().selectList("select * from ")

    }

    private static void withdraw(YopService yopService) {
        String withdrawNo = StringUtils.N_TO_10("LZT_DW_");
        WithdrawCardQueryResult card = yopService.withdrawCardQuery("10090334402");
        WithdrawOrderResult withdrawResult = yopService.withdrawOrder("10089625822", "10090334402", withdrawNo, card.getBankCardAccountList().get(0).getBindCardId(), "1", "http://fky.natapp1.cc/yop/dw");

        System.out.println(JSONObject.toJSONString(withdrawResult));
    }

    private static void transfer(YopService yopService) {
        String transferNo = StringUtils.N_TO_10("LZT_NDF_");
        // 10090316790  5元最早账户
        // 10089625822
        AccountTransferOrderResult transferResult = yopService.transferB2bOrder(transferNo, "10090316790", "10090334402", "1", null);
        System.out.println(JSONObject.toJSONString(transferResult));
    }

    private static void register(YopService yopService) {
        String registerMicroRequestNo = StringUtils.N_TO_10("LZT_RS_");
        RegisterMicroResult registerMicroResult = yopService.registerMicro(registerMicroRequestNo, "冯开英", "429005199305060899", "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E6%AD%A3.jpg",
                "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E5%8F%8D.jpg",
                "15871898210", "420000", "429000", "429005",
                "竹根滩镇黑流渡村一组", "6228480329262404075", "ABC", "http://fky.natapp1.cc/yop/registerMicro");

        System.out.println(JSONObject.toJSONString(registerMicroResult));


//        RegisterQueryResult result = yopService.registerQuery("LZT_RS_157329444997738");
//        System.out.println(JSONObject.toJSONString(result));
    }


    private static void queryOpenBank(YopService yopService) {
        BankAccountQueryResult bankAccountQueryResult = yopService.bankAccountQuery("10090333215", "BO_49554926725273");
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
        params.setMerchantNo("10090333215");

        params.setMerchantName("深圳市千浪文化传媒有限公司");
        params.setOpenBankCode("SUNINGBANK_MULTICHANNEL");
        params.setOpenAccountType("INDIVIDUAL_BUSINESS_TYPE");
        params.setCertificateType("BUSINESS_LICENCE");
        params.setCertificateNo("91440300MA5H6C9T1J");
        params.setNotifyUrl("https://applet.dys.ink/yop/ew");

        SnMultiChannelOpenAccountDTO dto = new SnMultiChannelOpenAccountDTO();

        dto.setSocialCreditCodeImageUrl("http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E8%90%A5%E4%B8%9A%E6%89%A7%E7%85%A7.jpg");
        dto.setSocialCreditCodeImageUrl(yopService.upload(dto.getSocialCreditCodeImageUrl()));
        dto.setLegalCardImageFont("http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E6%B3%95%E4%BA%BA%E8%BA%AB%E4%BB%BD%E8%AF%81%E6%AD%A3%E9%9D%A2.jpg");

        //yopService.upload(dto.getLegalCardImageFont())


        dto.setLegalCardImageFont("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/24/merchant-1716548054675-680f3b00-a7ec-47f5-8f97-e062eef6cc01-zCAxMSwpzhyIfSOtgwyP.jpg");
        dto.setLegalCardImageBack("http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E6%B3%95%E4%BA%BA%E8%BA%AB%E4%BB%BD%E8%AF%81%E5%8F%8D%E9%9D%A2.jpg");
       // yopService.upload(dto.getLegalCardImageBack())
        dto.setLegalCardImageBack("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/24/merchant-1716548053805-d39a29de-cfbe-45dc-8a71-28a1207f0a8d-EjBGOUAsfBDmMqYAlOLR.jpg");
        dto.setLegalMobileNo("15678516978");

        dto.setOperatorName("徐静");
        dto.setMobileNo("13823668660");

        List<BenefitDTO> benefitList = Lists.newArrayList();
        BenefitDTO benefit = new BenefitDTO();
        benefit.setBenefitName("龙海浪");
        benefit.setBenefitIdType("ID_CARD");
        benefit.setBenefitIdNo("45032219970904051X");

        benefit.setBenefitStartDate("20140207");
        benefit.setBenefitExpireDate("20240207");

        benefit.setBenefitImageFont(dto.getLegalCardImageFont());
        benefit.setBenefitImageBack(dto.getLegalCardImageBack());

        benefit.setBenefitAddress("广西临桂县六塘镇罗塘村委会上村28号");
        benefitList.add(benefit);

        dto.setBenefitDTOList(benefitList);
        params.setSnMultiChannelOpenAccountDTO(dto);

        BankAccountOpenResult result = yopService.bankAccountOpen(params);
        System.out.println(JSONObject.toJSONString(result));

    }


}
