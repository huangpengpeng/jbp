package com.jbp.front;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.yop.dto.BenefitDTO;
import com.jbp.common.yop.dto.ProductQualificationInfoDto;
import com.jbp.common.yop.dto.SnMultiChannelOpenAccountDTO;
import com.jbp.common.yop.params.BankAccountOpenParams;
import com.jbp.common.yop.params.RegisterParams;
import com.jbp.common.yop.params.WechatAlipayPayParams;
import com.jbp.common.yop.result.*;
import com.jbp.service.service.YopService;

import java.util.List;

public class YopDemo {


    public static void registerQuery(YopService yopService) {
        RegisterQueryResult result = yopService.registerQuery("LZT_RS_208930512546893");
        System.out.println(JSONObject.toJSONString(result));
    }

    private static void withdraw(YopService yopService) {
        String withdrawNo = StringUtils.N_TO_10("LZT_DW_");
        WithdrawCardQueryResult card = yopService.withdrawCardQuery("10090334402");
        WithdrawOrderResult withdrawResult = yopService.withdrawOrder("10089625822", "10090334402", withdrawNo, card.getBankCardAccountList().get(0).getBindCardId(), "1", "http://fky.natapp1.cc/yop/dw");

        System.out.println(JSONObject.toJSONString(withdrawResult));
    }

    public static void transfer(YopService yopService) {
        String transferNo = StringUtils.N_TO_10("LZT_NDF_");
        // 10090316790  5元最早账户
        // 10089625822
        AccountTransferOrderResult transferResult = yopService.transferB2bOrder(transferNo, "10090466492", "10090413808", "1", null);
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
    public static void merRegister(YopService yopService) {
        RegisterParams params = new RegisterParams();
        params.setRequestNo(StringUtils.N_TO_10("LZT_RS_"));
        params.setBusinessRole("PLATFORM_MERCHANT");// 平台商户

        // 商户资质信息
        String merchantSubjectInfo = "{ \"licenceUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/07/30/merchant-1722329053156-65ab6ab6-ad6a-498a-abd0-df08049d2f25-qDloQVozqINYVawSmmBM.jpg\", \"signName\":\"上海吾善科技有限公司\", \"signType\":\"ENTERPRISE\", \"licenceNo\":\"91310120MA1HWW3EXH\", \"shortName\":\"上海吾善科技有限公司\",  \"openAccountLicenceUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/08/19/merchant-1724065182842-506049b5-de1d-47ef-8b26-7d3a13263ce2-kgJVMFXLGiUEdBHpdhHJ.jpg\"}";
        params.setMerchantSubjectInfo(merchantSubjectInfo);

        // 法人信息
        String merchantCorporationInfo = "{ \"legalName\":\"杨卫红\", \"legalLicenceType\":\"ID_CARD\", \"legalLicenceNo\":\"610303197110071629\", \"legalLicenceFrontUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/07/30/merchant-1722329135903-4c7b5fde-aec6-455a-9396-69dcaf608bfd-alXfUsKrjGUOuVWMeMGH.jpg\", \"legalLicenceBackUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/07/30/merchant-1722329160273-7bc97f44-fa42-4ce5-815a-72df20797dcf-eewflZsCOvdPNnGOBMkj.jpg\" }";
        params.setMerchantCorporationInfo(merchantCorporationInfo);

        // 联系人信息
        String merchantContactInfo="{ \"contactName\":\"宋鑫\", \"contactMobile\":\"13811065970\", \"contactEmail\":\"20840414@qq.com\", \"contactLicenceNo\":\"210124197704250612\" ,\"adminEmail\":\"20840414@qq.com\",\"adminMobile\":\"13811065970\" }";
        params.setMerchantContactInfo(merchantContactInfo);

        // 地址信息
        String businessAddressInfo = "{ \"province\":\"310000\", \"city\":\"310100\", \"district\":\"310120\", \"address\":\"海坤路1号1幢\" }";
        params.setBusinessAddressInfo(businessAddressInfo);

        // 结算账户信息  招商银行股份有限公司广州机场路支行
        String settlementAccountInfo = "{ \"settlementDirection\":\"BANKCARD\", \"bankCode\":\"ABC\", \"bankAccountType\":\"ENTERPRISE_ACCOUNT\", \"bankCardNo\":\"03769000040035541\" }";
        params.setSettlementAccountInfo(settlementAccountInfo);

        // 通知地址
        params.setNotifyUrl("http://fky.natapp1.cc/yop");

        // 开通产品资质不能为空
        params.setProductQualificationInfo(JacksonTool.toJsonString(new ProductQualificationInfoDto()));
        RegisterResult register = yopService.register(params);
        System.out.println(JSONObject.toJSONString(register));

    }

    public static void merRegister2(YopService yopService) {
        RegisterParams params = new RegisterParams();
        params.setRequestNo(StringUtils.N_TO_10("LZT_RS_"));
        params.setBusinessRole("PLATFORM_MERCHANT");// 平台商户

        // 商户资质信息
        String merchantSubjectInfo = "{ \"licenceUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/25/merchant-1719308385706-4a4a0357-d70e-4e98-bfa2-2db4432196ab-QsuGRcQefGvYetgnxBnA.jpg\", \"signName\":\"福建众合创享健康管理有限公司\", \"signType\":\"ENTERPRISE\", \"licenceNo\":\"91350800MAC7GWB50Y\", \"shortName\":\"众合创享\" }";
        params.setMerchantSubjectInfo(merchantSubjectInfo);

        // 法人信息
        String merchantCorporationInfo = "{ \"legalName\":\"彭美勤\", \"legalLicenceType\":\"ID_CARD\", \"legalLicenceNo\":\"352601197105224020\", \"legalLicenceFrontUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/25/merchant-1719308449905-4fea2055-93e5-4ba2-b22c-58d957022682-mNcIYTIqAIMQdkWNdGZt.jpg\", \"legalLicenceBackUrl\":\"http://staticres.yeepay.com/jcptb-merchant-netinjt05/2024/06/25/merchant-1719308485303-4bd98e65-699e-4786-b26f-c22d97d90d35-WfBByekvNpgYlSzDNGfk.jpg\" }";
        params.setMerchantCorporationInfo(merchantCorporationInfo);

        // 联系人信息
        String merchantContactInfo="{ \"contactName\":\"吴健平\", \"contactMobile\":\"13850686330\", \"contactEmail\":\"105809899@qq.com\", \"contactLicenceNo\":\"350802198205025010\" ,\"adminEmail\":\"105809899@qq.com\",\"adminMobile\":\"13850686330\" }";
        params.setMerchantContactInfo(merchantContactInfo);

        // 地址信息
        String businessAddressInfo = "{ \"province\":\"350000\", \"city\":\"350800\", \"district\":\"350802\", \"address\":\"工业西路115号935室\" }";
        params.setBusinessAddressInfo(businessAddressInfo);

        // 结算账户信息  招商银行股份有限公司广州机场路支行
        String settlementAccountInfo = "{ \"settlementDirection\":\"BANKCARD\", \"bankCode\":\"ICBC\", \"bankAccountType\":\"ENTERPRISE_ACCOUNT\", \"bankCardNo\":\"1410090119900282930\" }";
        params.setSettlementAccountInfo(settlementAccountInfo);

        // 通知地址
        params.setNotifyUrl("http://fky.natapp1.cc/yop");

        // 开通产品资质不能为空
        params.setProductQualificationInfo(JacksonTool.toJsonString(new ProductQualificationInfoDto()));
        RegisterResult register = yopService.register(params);
        System.out.println(JSONObject.toJSONString(register));

    }


    public static void quickPay(YopService yopService){
        String orderId = StringUtils.N_TO_10("YB_");
        String s = yopService.quickPay("10090436581",  "10000", orderId, "0.1", "测试商品", "http://fky.natapp1.cc/yop", "", "http://fky.natapp1.cc/yop");
        System.out.println(s);
    }

    public static void wechatAliPay(YopService yopService){
        String orderId = StringUtils.N_TO_10("YB_");
        WechatAliPayPayResult result =yopService.wechatAlipayPay("10090436581", "10000",  orderId, "0.1", "测试商品",
                "http://fky.natapp1.cc/yop", "", "http://fky.natapp1.cc/yop", WechatAlipayPayParams.PAYWAY.USER_SCAN.name(),
                WechatAlipayPayParams.CHANNEL.ALIPAY.name(), "", "", "115.220.223.185");
        System.out.println(result);
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


    public static void selfSettle(YopService yopService) {
        String s = StringUtils.N_TO_10("S_");
        System.out.println(s);
        SelfSettleResult all = yopService.selfSettle("10090418105", s, "ALL");
        System.out.println(JSONObject.toJSONString(all));
    }
}
