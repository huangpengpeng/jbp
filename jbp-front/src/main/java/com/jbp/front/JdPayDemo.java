package com.jbp.front;







import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.vo.JdPayCreateOrderRequest;
import com.jbp.common.jdpay.vo.JdPayCreateOrderResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JdPayDemo {

    private JdPayCreateOrderRequest request;
    private JdPayCreateOrderResponse response;

    public void createOrder(JdPay jdPay) {
        try {
            response = jdPay.createOrder(initRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String resultCode = response.getResultCode();
        String outTradeNo = response.getOutTradeNo();
        System.out.println(resultCode);
        System.out.println(outTradeNo);

    }

    private JdPayCreateOrderRequest initRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());
        request = new JdPayCreateOrderRequest();
        request.setOutTradeNo(now+"paysign");
        request.setTradeType("GEN");
        //模板号
//        request.setTemplateNo("testJiou002");
        request.setTradeAmount("10");
        request.setCreateDate(now);
        request.setTradeExpiryTime("6000");
        request.setTradeSubject("京东Plus续费会员");
        request.setTradeRemark("京东Plus续费会员");
        request.setClientType("H5");
        request.setCurrency("CNY");
        request.setUserIp("127.0.0.1");
        request.setBizTp("");
        request.setReturnParams("");
        request.setGoodsInfo("");
        request.setUserId("qmk008");
        request.setNotifyUrl("http://172.25.64.59:8004/jdPay/tradeNotify");
        request.setSignNotifyUrl("http://172.25.64.59:8004/jdPay/signNotify");
        request.setPageBackUrl("http://172.25.64.59:8004/jdPay/pageCallBack");
        request.setRiskInfo("{\"orderEid\":\"100d8559084433e1b94\",\"rentTotal\":\"10.0\",\"rent\":\"3000\",\"paymentType\":\"1000200360002\",\"contractTime\":\"2024-04-10\",\"acctRegTime\":\"\",\"rentalTerm\":\"7\",\"orderIp\":\"114.241.136.1\",\"deposit\":\"3000\",\"startTime\":\"2024-04-10\",\"endTime\":\"2024-11-30\",\"depositType\":\"1\",\"orderAccount\":\"17835\"}");

//        JdPayDivisionAccount divisionAccount = new JdPayDivisionAccount();
//        List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList = new ArrayList<JdPayDivisionAccountTradeInfo>();
//        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoOne = new JdPayDivisionAccountTradeInfo();
//        divisionAccountTradeInfoOne.setMerchantNo(divisionSubMerchantNo1);
//        divisionAccountTradeInfoOne.setOutTradeNo(now+"_1");
//        divisionAccountTradeInfoOne.setTradeAmount("5");
//        divisionAccountTradeInfoList.add(divisionAccountTradeInfoOne);
//        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoTwo = new JdPayDivisionAccountTradeInfo();
//        divisionAccountTradeInfoTwo.setMerchantNo(divisionSubMerchantNo2);
//        divisionAccountTradeInfoTwo.setOutTradeNo(now+"_2");
//        divisionAccountTradeInfoTwo.setTradeAmount("5");
//        divisionAccountTradeInfoList.add(divisionAccountTradeInfoTwo);
//        divisionAccount.setDivisionAccountTradeInfoList(divisionAccountTradeInfoList);
//        request.setDivisionAccount( GsonUtil.toJson(divisionAccount));
//

        return request;
    }
}
