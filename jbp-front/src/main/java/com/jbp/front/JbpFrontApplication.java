package com.jbp.front;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.beust.jcommander.internal.Lists;
import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.dto.BenefitDTO;
import com.jbp.common.yop.dto.ProductQualificationInfoDto;
import com.jbp.common.yop.dto.SnMultiChannelOpenAccountDTO;
import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.impl.YopServiceImpl;
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

        merRegister(yopService);

//        WithdrawCardQueryResult cardQueryResult = yopService.withdrawCardQuery("10090420584");
//        WithdrawCardBindParams params = new WithdrawCardBindParams();
//        params.setMerchantNo("10090420584");
//        params.setAccountNo("120924643310006");
//        params.setBankCardType("ENTERPRISE_ACCOUNT");
//        params.setBankCode("CMBCHINA");
////        params.setBranchCode("102161000113");
//
//         WithdrawCardBindResult withdrawCardBindResult = yopService.withdrawCardBind(params);
//        System.out.println(JSONObject.toJSONString(withdrawCardBindResult));
//
//        WithdrawCardModifyParams params = new WithdrawCardModifyParams();
//        params.setMerchantNo("10090420584");
//        params.setBindId(cardQueryResult.getBankCardAccountList().get(0).getBindCardId());
//        params.setBankCardOperateType("CANCELLED");
//        params.setBankCode("CMBCHINA");
//        params.setBranchCode("120924643310001");
//        WithdrawCardModifyResult withdrawCardModifyResult = yopService.withdrawCardModify(params);
//        System.out.println(JSONObject.toJSONString(withdrawCardModifyResult));

//        yopService.withdrawCardQuery("10090339599");

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
//        openBank2(yopService);


//        RegisterQueryResult result = yopService.registerQuery("LZT_RS_103702359635243");

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
        AccountTransferOrderResult transferResult = yopService.transferB2bOrder(transferNo, "10089625822", "10090338239", "1", null);
        System.out.println(JSONObject.toJSONString(transferResult));
    }

    private static void register(YopService yopService) {
        String registerMicroRequestNo = StringUtils.N_TO_10("LZT_RS_");
        RegisterMicroResult registerMicroResult = yopService.registerMicro(registerMicroRequestNo, "冯开英", "429005199305060899", "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E6%AD%A3.jpg",
                "http://jwebmall.oss-cn-hangzhou.aliyuncs.com/%E5%8F%8D.jpg",
                "15871898210", "420000", "429000", "429005",
                "竹根滩镇黑流渡村一组", "6228480329262404075", "ABC", "http://fky.natapp1.cc/yop/registerMicro", "平台");

        System.out.println(JSONObject.toJSONString(registerMicroResult));
//        RegisterQueryResult result = yopService.registerQuery("LZT_RS_157329444997738");
//        System.out.println(JSONObject.toJSONString(result));
    }
    private static void merRegister(YopService yopService) {
        RegisterParams params = new RegisterParams();
        params.setRequestNo(StringUtils.N_TO_10("LZT_RS_"));
        params.setBusinessRole("PLATFORM_MERCHANT");// 平台商户

        // 商户资质信息
        String merchantSubjectInfo = "{ \"licenceUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/24/merchant-1719231297716-1af1fe28-4952-426a-9962-71ff9d1f5829-jPAMRTfGQeCKhUKhaOhW.jpg\", \"signName\":\"四川福能源生物科技有限公司浙江分公司\", \"signType\":\"ENTERPRISE\", \"licenceNo\":\"91330127MACW9AYK4B\", \"shortName\":\"福能源浙江分公司\" }";
        params.setMerchantSubjectInfo(merchantSubjectInfo);

        // 法人信息
        String merchantCorporationInfo = "{ \"legalName\":\"黄冰欢\", \"legalLicenceType\":\"ID_CARD\", \"legalLicenceNo\":\"350802198205191019\", \"legalLicenceFrontUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/24/merchant-1719231330228-c613ee52-28f8-421e-965c-7aca454dd136-IJNtYoNEvIXTlODoArJQ.jpg\", \"legalLicenceBackUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/24/merchant-1719231355078-ffc02232-7cbc-46ae-b63b-a1b3afca4006-siniPDLMLqfrhaqfwjQF.jpg\" }";
        params.setMerchantCorporationInfo(merchantCorporationInfo);

        // 联系人信息
        String merchantContactInfo="{ \"contactName\":\"麻晴露\", \"contactMobile\":\"15868875872\", \"contactEmail\":\"maqinglu@zjfny.com.cn\", \"contactLicenceNo\":\"33032619931023322X\" ,\"adminEmail\":\"maqinglu@zjfny.com.cn\",\"adminMobile\":\"15868875872\" }";
        params.setMerchantContactInfo(merchantContactInfo);

        // 地址信息
        String businessAddressInfo = "{ \"province\":\"330000\", \"city\":\"330100\", \"district\":\"330106\", \"address\":\"天目山路178号5楼506室\" }";
        params.setBusinessAddressInfo(businessAddressInfo);

        // 结算账户信息  招商银行股份有限公司广州机场路支行
        String settlementAccountInfo = "{ \"settlementDirection\":\"BANKCARD\", \"bankCode\":\"NBYH\", \"bankAccountType\":\"ENTERPRISE_ACCOUNT\", \"bankCardNo\":\"71280122000119057\" }";
        params.setSettlementAccountInfo(settlementAccountInfo);

        // 通知地址
        params.setNotifyUrl("http://fky.natapp1.cc/yop");

        // 开通产品资质不能为空
        params.setProductQualificationInfo(JacksonTool.toJsonString(new ProductQualificationInfoDto()));
        RegisterResult register = yopService.register(params);
        System.out.println(JSONObject.toJSONString(register));

    }




    private static void queryOpenBank(YopService yopService) {
//        BankAccountQueryResult result1 = yopService.bankAccountQuery("10090348841", "BO_90331316529100");
        BankAccountQueryResult result2 = yopService.bankAccountQuery("10090348912", "BO_78009978360390");
//        BankAccountQueryResult result3 = yopService.bankAccountQuery("10090343285", "BO_147300999316495");
//        BankAccountQueryResult result4 = yopService.bankAccountQuery("10090333170", "BO_188450832013472");

//        System.out.println(JSONObject.toJSONString(result1));
        System.out.println(JSONObject.toJSONString(result2));
//        System.out.println(JSONObject.toJSONString(result3));
//        System.out.println(JSONObject.toJSONString(result4));
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
        params.setMerchantNo("10090348841");

        params.setMerchantName("湖北省好聪聪生物科技有限公司");
        params.setOpenBankCode("SUNINGBANK_MULTICHANNEL");
        params.setOpenAccountType("ENTERPRISE");
        params.setOpenAccountType("INDIVIDUAL_BUSINESS_TYPE");
        params.setCertificateType("BUSINESS_LICENCE");
        params.setCertificateNo("91420684MADFELGF23");
        params.setNotifyUrl("https://applet.dys.ink/yop/ew");

        SnMultiChannelOpenAccountDTO dto = new SnMultiChannelOpenAccountDTO();
        if(params.getOpenAccountType().equals("ENTERPRISE")){
            dto.setBindCardType("PUBLIC_CARD");
            dto.setBindCardNo("42050164680800001338");
            dto.setBindBankCode("CCB"); //招商银行股份有限公司深圳南海支行
            dto.setBindAccountName("湖北省好聪聪生物科技有限公司");//
            dto.setBranchBankNo("105100000017");
        }

        dto.setSocialCreditCodeImageUrl("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/30/merchant-1717044664591-f4184bd5-d42e-44ad-b066-37aebf7d1edb-bKKpSZGtJTPrKAiaazPv.jpg");
        dto.setLegalCardImageFont("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/30/merchant-1717044684557-80f6dbaa-2f41-4493-b93e-03379e1138a3-LgIHQabVHKqkigraiNoN.jpg");
        dto.setLegalCardImageBack("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/30/merchant-1717044716846-659ed5bd-f07b-4dd9-b8eb-f1241e218249-VRgjcBRcrbrlIblqiWvk.jpg");


        dto.setOperatorName("徐静");
        dto.setMobileNo("13823668660");

        List<BenefitDTO> benefitList = Lists.newArrayList();
        BenefitDTO benefit = new BenefitDTO();
        benefit.setBenefitName("付丽珊");
        dto.setLegalMobileNo("15611823036");
        benefit.setBenefitIdType("ID_CARD");
        benefit.setBenefitIdNo("500236199907174727");
        benefit.setBenefitStartDate("20230901");
        benefit.setBenefitExpireDate("20330901");

        benefit.setBenefitImageFont(dto.getLegalCardImageFont());
        benefit.setBenefitImageBack(dto.getLegalCardImageBack());

        benefit.setBenefitAddress("重庆市奉节县吐祥镇大庄村1组322号");
        benefitList.add(benefit);

        dto.setBenefitDTOList(benefitList);
        params.setSnMultiChannelOpenAccountDTO(dto);

        BankAccountOpenResult result = yopService.bankAccountOpen(params);
        System.out.println(JSONObject.toJSONString(result));

    }

    private static void openBank2(YopService yopService) {
        // {"authType":"NO_AUTH","orderNo":"6dfaa3a38bf84ba7aca8228158ba10ab","requestNo":"BO_65808892927832","returnCode":"AM00000","status":"PROCESS"}
        BankAccountOpenParams params = new BankAccountOpenParams();
        String requestNo2 = StringUtils.N_TO_10("BO_");
        System.out.println(requestNo2);
        params.setRequestNo(requestNo2);
        params.setMerchantNo("10090348912");

        params.setMerchantName("宜城市恒聪生物科技有限公司");
        params.setOpenBankCode("SUNINGBANK_MULTICHANNEL");
        params.setOpenAccountType("ENTERPRISE");
        params.setOpenAccountType("INDIVIDUAL_BUSINESS_TYPE");
        params.setCertificateType("BUSINESS_LICENCE");
        params.setCertificateNo("91420684MADDPJRF5H");
        params.setNotifyUrl("https://applet.dys.ink/yop/ew");

        SnMultiChannelOpenAccountDTO dto = new SnMultiChannelOpenAccountDTO();
        if(params.getOpenAccountType().equals("ENTERPRISE")){
            dto.setBindCardType("PUBLIC_CARD");
            dto.setBindCardNo("42050164680800001329");
            dto.setBindBankCode("CCB"); //招商银行股份有限公司深圳南海支行
            dto.setBindAccountName("宜城市恒聪生物科技有限公司");//
            dto.setBranchBankNo("105528200662");
        }

        dto.setSocialCreditCodeImageUrl("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/31/merchant-1717120864221-3a8eba26-16d3-4aa4-8dcb-01c143ad3555-FMFpwmhFjzzWadjyraSr.jpg");
        dto.setLegalCardImageFont("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/30/merchant-1717044684557-80f6dbaa-2f41-4493-b93e-03379e1138a3-LgIHQabVHKqkigraiNoN.jpg");
        dto.setLegalCardImageBack("http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/05/30/merchant-1717044716846-659ed5bd-f07b-4dd9-b8eb-f1241e218249-VRgjcBRcrbrlIblqiWvk.jpg");


        dto.setOperatorName("徐静");
        dto.setMobileNo("13823668660");

        List<BenefitDTO> benefitList = Lists.newArrayList();
        BenefitDTO benefit = new BenefitDTO();
        benefit.setBenefitName("付丽珊");
        dto.setLegalMobileNo("15611823036");
        benefit.setBenefitIdType("ID_CARD");
        benefit.setBenefitIdNo("500236199907174727");
        benefit.setBenefitStartDate("20230901");
        benefit.setBenefitExpireDate("20330901");

        benefit.setBenefitImageFont(dto.getLegalCardImageFont());
        benefit.setBenefitImageBack(dto.getLegalCardImageBack());

        benefit.setBenefitAddress("重庆市奉节县吐祥镇大庄村1组322号");
        benefitList.add(benefit);

        dto.setBenefitDTOList(benefitList);
        params.setSnMultiChannelOpenAccountDTO(dto);

        BankAccountOpenResult result = yopService.bankAccountOpen(params);
        System.out.println(JSONObject.toJSONString(result));

    }


}
