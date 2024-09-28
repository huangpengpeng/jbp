package com.jbp.front;







import com.alibaba.fastjson.JSONObject;
import com.jbp.common.jdpay.enums.SceneTypeEnum;
import com.jbp.common.jdpay.sdk.JdPay;
import com.jbp.common.jdpay.util.GsonUtil;
import com.jbp.common.jdpay.vo.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdPayDemo {

    private JdPayCreateOrderRequest request;
    private JdPayCreateOrderResponse response;

    private JdPayAggregateCreateOrderRequest aggregateRequest;
    private JdPayAggregateCreateOrderResponse aggregateResponse;



    /**
     * {"currency":"CNY","originalOutTradeNo":"20240925161841","outTradeNo":"R_20240925161841",
     * "resultCode":"0000","resultDesc":"成功",
     * "tradeAmount":"10","tradeNo":"202409251648342013720555601128","tradeStatus":"ACSU"}
     * @param jdPay
     */
    public void refund(JdPay jdPay) {
        JdPayRefundRequest q = new JdPayRefundRequest();
        q.setOriginalOutTradeNo("20240925161841");
        q.setOutTradeNo("R_20240925161841");
        q.setCurrency("CNY");
        q.setTradeAmount("10");
        q.setTradeDate("20240925161841");

        JdPayDivisionAccountRefund divisionAccountRefund = new JdPayDivisionAccountRefund();

        List<JdPayDivisionAccountRefundInfo> divisionAccountRefundInfoList = new ArrayList<JdPayDivisionAccountRefundInfo>();
        JdPayDivisionAccountRefundInfo divisionAccountRefundInfoOne = new JdPayDivisionAccountRefundInfo();
        divisionAccountRefundInfoOne.setMerchantNo("134592065004");
        divisionAccountRefundInfoOne.setOutTradeNo("R_20240925161841"+"_1");
        divisionAccountRefundInfoOne.setTradeAmount("5");
        divisionAccountRefundInfoOne.setOriginalOutTradeNo( "20240925161841_1" );
        divisionAccountRefundInfoList.add(divisionAccountRefundInfoOne);
        JdPayDivisionAccountRefundInfo divisionAccountRefundInfoTwo = new JdPayDivisionAccountRefundInfo();
        divisionAccountRefundInfoTwo.setMerchantNo("134592065006");
        divisionAccountRefundInfoTwo.setOutTradeNo("R_20240925161841"+"_2");
        divisionAccountRefundInfoTwo.setTradeAmount("5");
        divisionAccountRefundInfoTwo.setOriginalOutTradeNo( "20240925161841_2" );
        divisionAccountRefundInfoList.add(divisionAccountRefundInfoTwo);
        divisionAccountRefund.setDivisionAccountRefundInfoList(divisionAccountRefundInfoList);
        q.setDivisionAccountRefund(GsonUtil.toJson(divisionAccountRefund));

        try {
            JdPayRefundResponse refund = jdPay.refund(q);
            System.out.println(JSONObject.toJSONString(refund));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {"finishDate":"20240925161948","outTradeNo":"20240925161841","payTool":"ALIPAY","resultCode":"0000","resultDesc":"成功","returnParams":"\"\"",
     * "tradeAmount":"10","tradeNo":"202409251618432017410859201128","tradeStatus":"FINI","tradeType":"AGGRE_QR","userId":"qmk008"}
     * @param jdPay
     */
    public void queryOrder(JdPay jdPay) {
        JdPayQueryOrderRequest q = new JdPayQueryOrderRequest();
        q.setOutTradeNo("20240925161841");
        try {
            JdPayQueryOrderResponse jdPayQueryOrderResponse = jdPay.queryOrder(q);
            System.out.println(JSONObject.toJSONString(jdPayQueryOrderResponse));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public void createAggregateOrder(JdPay jdPay) {
        try {
            aggregateResponse = jdPay.aggregateCreateOrder(initRequest2());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String resultCode = aggregateResponse.getResultCode();
        String outTradeNo = aggregateResponse.getOutTradeNo();
        System.out.println(resultCode);
        System.out.println(outTradeNo);

    }

    /**
     * 接口返回参数:{"tradeNo":"202409251618432017410859201128","resultCode":"0000",
     * "qrCode":"https://3.cn/25yONN-U","outTradeNo":"20240925161841","resultDesc":"成功"}
     * @return
     */
    private JdPayAggregateCreateOrderRequest initRequest2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());
        aggregateRequest = new JdPayAggregateCreateOrderRequest();
        aggregateRequest.setOutTradeNo(now+"");
        aggregateRequest.setTradeType("AGGRE_QR");
        aggregateRequest.setTradeAmount("10");
        aggregateRequest.setCreateDate(now);
        aggregateRequest.setTradeExpiryTime("6000");
        aggregateRequest.setTradeSubject("京东Plus续费会员");
        aggregateRequest.setTradeRemark("京东Plus续费会员");
        aggregateRequest.setCurrency("CNY");
        aggregateRequest.setUserIp("127.0.0.1");
        aggregateRequest.setBizTp("");
        aggregateRequest.setReturnParams("");
        aggregateRequest.setGoodsInfo("");
        aggregateRequest.setUserId("qmk008");
        aggregateRequest.setNotifyUrl("http://172.25.64.59:8004/jdPay/tradeNotify");
//        request.setPageBackUrl("http://172.25.64.59:8004/jdPay/pageCallBack");
        aggregateRequest.setPageBackUrl("http://www.baidu.com");

        aggregateRequest.setRiskInfo("");
        aggregateRequest.setSceneType(SceneTypeEnum.ONLINE_PC.getCode());

        JdPayDivisionAccount divisionAccount = new JdPayDivisionAccount();

        List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList = new ArrayList<JdPayDivisionAccountTradeInfo>();
        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoOne = new JdPayDivisionAccountTradeInfo();
        divisionAccountTradeInfoOne.setMerchantNo("134592065004");
        divisionAccountTradeInfoOne.setOutTradeNo(now+"_1");
        divisionAccountTradeInfoOne.setTradeAmount("5");
        divisionAccountTradeInfoList.add(divisionAccountTradeInfoOne);

        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoTwo = new JdPayDivisionAccountTradeInfo();
        divisionAccountTradeInfoTwo.setMerchantNo("134592065006");
        divisionAccountTradeInfoTwo.setOutTradeNo(now+"_2");
        divisionAccountTradeInfoTwo.setTradeAmount("5");
        divisionAccountTradeInfoList.add(divisionAccountTradeInfoTwo);
        divisionAccount.setDivisionAccountTradeInfoList(divisionAccountTradeInfoList);



        divisionAccount.setVersion( "V2" );
        aggregateRequest.setDivisionAccount(GsonUtil.toJson(divisionAccount));
        return aggregateRequest;
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

        JdPayDivisionAccount divisionAccount = new JdPayDivisionAccount();
        List<JdPayDivisionAccountTradeInfo> divisionAccountTradeInfoList = new ArrayList<JdPayDivisionAccountTradeInfo>();
        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoOne = new JdPayDivisionAccountTradeInfo();
        divisionAccountTradeInfoOne.setMerchantNo("134592065004");
        divisionAccountTradeInfoOne.setOutTradeNo(now+"_1");
        divisionAccountTradeInfoOne.setTradeAmount("5");
        divisionAccountTradeInfoList.add(divisionAccountTradeInfoOne);
        JdPayDivisionAccountTradeInfo divisionAccountTradeInfoTwo = new JdPayDivisionAccountTradeInfo();
        divisionAccountTradeInfoTwo.setMerchantNo("134592065006");
        divisionAccountTradeInfoTwo.setOutTradeNo(now+"_2");
        divisionAccountTradeInfoTwo.setTradeAmount("5");
        divisionAccountTradeInfoList.add(divisionAccountTradeInfoTwo);
        divisionAccount.setDivisionAccountTradeInfoList(divisionAccountTradeInfoList);
        request.setDivisionAccount(GsonUtil.toJson(divisionAccount));


        return request;
    }
}
