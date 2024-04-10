package com.jbp.front.controller.tank;

import com.Jwebmall.core.entity.Config;
import com.Jwebmall.core.manager.ConfigMng;
import com.Jwebmall.order.entity.FundClearing;
import com.Jwebmall.order.manager.FundClearingMng;
import com.Jwebmall.plugins.xs.entity.XsCapa;
import com.Jwebmall.plugins.xs.entity.XsUserCapa;
import com.Jwebmall.plugins.xs.manager.XsCapaMng;
import com.Jwebmall.plugins.xs.manager.XsUserCapaMng;
import com.Jwebmall.tools.CacheKeyUtils;
import com.Jwebmall.user.entity.User;
import com.Jwebmall.user.manager.UserMng;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.common.api.ResponseForT;
import com.common.jdbc.template.TxMng;
import com.common.jdbc.template.UnifiedJDBCMng;
import com.common.util.ArithmeticUtils;
import com.common.util.DateTimeUtils;
import com.common.web.ResponseUtils;
import com.common.web.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class TankOrdersAct {


    @Resource
    private ConfigMng configMng;
    @Resource
    private TankOrdersMng tankOrdersMng;
    @Resource
    private TankEquipmentNumberMng tankEquipmentNumberMng;
    @Resource
    private XsCapaMng xsCapaMng;
    @Resource
    private TankStoreRelationMng tankStoreRelationMng;
    @Resource
    private UnifiedJDBCMng unifiedJDBCMng;
    @Autowired
    private TxMng txMng;
    @Resource
    private TankStoreMng tankStoreMng;
    @Resource
    private UserMng userMng;
    @Resource
    private FundClearingMng fundClearingMng;
    @Resource
    private XsUserCapaMng xsUserCapaMng;



    @ApiOperation(value = "充值下单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankOrder/addOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT addOrder(Integer number, Long storeUserId, @ApiIgnore ResponseForT response) {

        Config config = configMng.getByName("共享仓次数金额");
        JSONArray validArray = JSONArray.parseArray(config.getValue());
        BigDecimal payPrice = BigDecimal.ZERO;
        for (Object object : validArray) {
            JSONObject validJSON = (JSONObject) object;
            Integer number1 = validJSON.getInteger("number");
            if (number.equals(number1) ) {
                payPrice = validJSON.getBigDecimal("price");
            }
        }

        TankOrders tankOrders = tankOrdersMng.add(WebUtils.getIdForLogin(), storeUserId, com.common.util.StringUtils.N_TO_10("GXC"), TankOrders.Constants.未支付.toString(), number, payPrice, null, new Date());
        return response.SUCCESS(tankOrders);
    }

    @ApiOperation(value = "充值次数选择", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankOrder/getNumberList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getNumberList(BigDecimal score, @ApiIgnore ResponseForT response) {

        Config config = configMng.getByName("共享仓次数金额");
        return response.SUCCESS(config.getValue());
    }


    @ApiOperation(value = "舱主关联店主列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankOrder/getTankStoreRelationList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getTankStoreRelation(@ApiIgnore ResponseForT response) {


        List<?> list = unifiedJDBCMng.query(new String[]{"userId"},
                new Object[]{WebUtils.getIdForLogin()}, "舱主关联店主列表");

        return response.SUCCESS(list);
    }


    @ApiOperation(value = "删除店主", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankStoreRelation/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT delete(Long id, @ApiIgnore ResponseForT response) {

        TankStoreRelation tankStoreRelation = tankStoreRelationMng.getId(id);
        List<TankStore> tankStore = tankStoreMng.getStoreUserId(tankStoreRelation.getStoreUserId());
        if (!tankStore.isEmpty()) {
            throw new RuntimeException("店主存在门店，无法删除");
        }
        tankStoreRelationMng.delete(id);
        return response.SUCCESS();
    }


    @ApiOperation(value = "店主门店列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/tankOrder/getTankStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseForT getTankStoreList(Long storeUserId, @ApiIgnore ResponseForT response) {

        List<?> list = unifiedJDBCMng.query(new String[]{"storeUserId"},
                new Object[]{storeUserId}, "店主门店列表");

        return response.SUCCESS(list);
    }


    @ApiOperation(value = "充值次数成功回调", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/tankOrder/callapi", produces = MediaType.APPLICATION_JSON_VALUE)
    public void callapi(@RequestParam Map<String, Object> params, @ApiIgnore HttpServletResponse response,
                        ModelMap model) throws Exception {
        try {
            JSONObject queryJSON = JSONObject.parseObject(JSONObject.toJSONString(params));
            {
                log.info("/tankOrder/callapi {}", JSONObject.toJSONString(params));
            }
            String orderNo = queryJSON.getString("orderNo");
            if (StringUtils.isBlank(orderNo)) {
                {
                    log.info("/order/callapi 1  {}", JSONObject.toJSONString(params));
                }
                ResponseUtils.renderText(response, "SUCCESS");
                return;
            }

            TankOrders tankOrders = tankOrdersMng.getOrderSn(orderNo);
            if (StringUtils.equals(tankOrders.getStatus(), TankOrders.Constants.已支付.toString())) {
                ResponseUtils.renderText(response, "SUCCESS");
                return;
            }


            Config config = configMng.getByName("支付结果查询接口");
            String url = StringUtils.replace(config.getValue(), "{domain}", CacheKeyUtils.getDomainServer());
            Map<String, String> queryMap = Maps.newLinkedHashMap();
            queryMap.put("orderNo", orderNo);
            String responseText = "{}";
            if (config != null) {
                URIBuilder uriBuilder = new URIBuilder(url);
                {
                    log.info("uriBuilder:{}", uriBuilder.toString());
                }
                HttpRequest httpRequest = HttpRequest.get(uriBuilder.toString()).query(queryMap);
                HttpResponse httpResponse = httpRequest.send();
                responseText = StringUtils.toString(httpResponse.bodyBytes(), "utf-8");
            }
            {
                log.info("responseText:{}", responseText);
            }
            JSONObject orderJSON = JSONObject.parseObject(responseText);
            Boolean ifSuccess = orderJSON.getBoolean("ifSuccess");
            {
                log.info("ifSuccess:{}", ifSuccess);
            }

            synchronized (orderNo.intern()) {
                if (ifSuccess) {
                    txMng.doCall(new TxMng.VoidCall() {
                        @Override
                        public void handleEvet() {
                            tankOrders.setStatus(TankOrders.Constants.已支付.toString());
                            tankOrders.setPayTime(new Date());
                            tankOrdersMng.updateByUpdater(tankOrders);
                            tankEquipmentNumberMng.increase(tankOrders.getStoreUserId(), tankOrders.getNumber(), tankOrders.getOrderSn(),null);

                            //发放佣金
                            //      fuxiao(tankOrders);
                            bole(tankOrders);

                        }
                    });
                }
            }

            ResponseUtils.renderText(response, "SUCCESS");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


//    //复销奖
//    public void  fuxiao( TankOrders tankOrders){
//        Config config = configMng.getByName("重复消费积分商品");
//        Config jinConfig = configMng.getByName("紧缩订单号");
//        Config goodsConfig = configMng.getByName("复销奖资格商品");
//        Config config2 = configMng.getByName("复销奖资格金额");
//
//        if (com.common.util.StringUtils.isEmpty(month)) {
//            throw new RuntimeException("请选择月份");
//        }
//
//        if (config == null) {
//            throw new RuntimeException("重复消费积分商品没配置");
//        }
//
//
//        List<FundClearing> fundList2 = unifiedJDBCMng.query(new String[]{"month"},
//                new String[]{month}, "查询重复消费积分审核结算订单", FundClearing.class);
//        if (!fundList2.isEmpty()) {
//            throw new RuntimeException("已确认发放，无法重新生成");
//        }
//
//        List<FundClearing> fundList = unifiedJDBCMng.query(new String[]{"month"},
//                new String[]{month}, "查询重复消费积分结算订单", FundClearing.class);
//
//        if (!fundList.isEmpty()) {
//            for (FundClearing fundClearing : fundList) {
//                fundClearingMng.delete(fundClearing.getId());
//            }
//        }
//        List<String> goodsList = Arrays.asList(config.getValue().split(","));
//
//        List<Orders> orders = unifiedJDBCMng.query(new String[]{"gId", "month"},
//                new String[]{(config == null || config.getValue() == null) ? "" : config.getValue(), month}, "重复消费积分未结算订单",
//                Orders.class);
//
//        for (Orders order : orders) {
//
//            User createUser = userMng.get(order.getUserId());
//            if (createUser == null) {
//                continue;
//            }
//            if (createUser.getParentId() == null) {
//                continue;
//            }
//
//            List<OrderGoods> orderGoods = orderGoodsMng.getOrdersId(order.getId());
//
//            for (OrderGoods good : orderGoods) {
//                GoodsExt ext = extMng.getByGoods(good.getGoodsId());
//                if (BooleanUtils.toBoolean(ext.getAssist())) {
//                    continue;
//                }
//                if (!goodsList.contains(good.getGoodsId().toString())) {
//                    continue;
//                }
//
//                User user = userMng.get(createUser.getParentId());
//                Integer J = 1;
//                // 增加重复消费积分
//                do {
//                    BigDecimal price = new BigDecimal(good.getNumber()).multiply(good.getPrice());
//                    if (user == null) {
//                        break;
//                    }
//
//                    Boolean ifAddclearing = false;
//
//                    XsUserCapa xsUserCapa = xsUserCapaMng.getByUser(user.getId());
//                    if (xsUserCapa != null && xsUserCapa.getXsCapaId() >= 1) {
//                        //大于1星直接增加佣金
//                        ifAddclearing = true;
//                    } else {
//                        // 条件满足 设置为0说明不紧缩，不设置为0 则金额存在 就会继续找下个满足条件的人，则叫紧缩
//                        if ((jinConfig == null || !org.apache.commons.lang3.StringUtils.equals(jinConfig.getValue(), "1"))) {
//                            J++;
//                        } else {
//                            if (user.getParentId() == null) {
//                                break;
//                            }
//                            user = userMng.get(user.getParentId());
//                            continue;
//                        }
//                    }
//
//                    Config configGoodsScale = configMng.getByName("重复消费积分商品" + good.getGoodsId());
//                    if (configGoodsScale != null) {
//                        price = price.multiply(new BigDecimal(configGoodsScale.getValue()));
//                    } else {
//                        price = price.multiply(new BigDecimal(0.8));
//                    }
//
//                    //当月付款金额要大于199 才有资格获取复销奖
//                    Number orderPrice = unifiedJDBCMng.getNum(new String[]{"gId", "month","userId"},
//                            new String[]{(goodsConfig == null || goodsConfig.getValue() == null) ? "" : goodsConfig.getValue(), month,user.getId().toString()}, "用户当月购买复销奖商品");
//                    Number orderPrice2 = unifiedJDBCMng.getNum(new String[]{"month","userId"},
//                            new String[]{ month,user.getId().toString()}, "用户当月购买跨境复销奖商品");
//                    BigDecimal goodsPrice = new BigDecimal(orderPrice.doubleValue()).add(new BigDecimal(orderPrice2.doubleValue()));
//
//                    if (ifAddclearing && goodsPrice.compareTo(new BigDecimal(config2.getValue())) >= 0) {
//                        BigDecimal amt = price.multiply(new BigDecimal("0.01")).setScale(2, BigDecimal.ROUND_DOWN);
//                        fundClearingMng.add(user.getId(),
//                                "FG" + order.getOrderSn() + user.getId() + good.getSpecificationsNumCode() + "_" + J,
//                                createUser.getUsername() + month + "[" + good.getGoodsName() + "]"
//                                        + FundClearing.FundClearingType.重复消费积分.toString(),
//                                amt, DateTimeUtils.getNow(), order.getOrderSn(),
//                                null,
//                                FundClearing.FundClearingType.重复消费积分.toString());
//                        J++;
//                    }
//                    if (user.getParentId() == null) {
//                        break;
//                    }
//                    user = userMng.get(user.getParentId());
//
//                } while (J <= 18);
//            }
//


    //伯乐  团队奖
    public void bole(TankOrders tankOrders) {


//        fundClearingMng.add(null, tankOrders.getOrderSn(),
//                FundClearing.FundClearingType.货款.toString() + "[" + tankOrders.getOrderSn() + "]",
//                tankOrders.getPayPrice(), DateTimeUtils.getNow(), tankOrders.getOrderSn(),
//                DateTimeUtils.getNow(),
//                FundClearing.FundClearingType.货款.toString());

        Config jinConfig = configMng.getByName("紧缩订单号");

        TankStoreRelation tankStoreRelation = tankStoreRelationMng.getStoreUserId(tankOrders.getStoreUserId());
        if (tankStoreRelation == null) {
            return;
        }
        User createUser = userMng.get(tankStoreRelation.getTankUserId());
        if (createUser == null) {
            return;
        }
        if (createUser.getParentId() == null) {
            return;
        }

        BigDecimal mangeCommAmt = BigDecimal.ZERO;
        JSONObject remarkJSON = new JSONObject();


        BigDecimal orderAmt = tankOrders.getPayPrice();


        User user = userMng.get(createUser.getParentId());
        XsUserCapa xsUserCapa = null;
        BigDecimal amtbl = BigDecimal.ZERO;
        do {

            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());

            // 伯乐奖只有3星以上才有
            if (xsUserCapa != null && (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3)) {
                // 增加伯乐奖
                if (ArithmeticUtils.gt(amtbl, BigDecimal.ZERO)) {
                    {
                        eachb(createUser, user, tankOrders, amtbl);
                    }
                    {
                        remarkJSON.put("userId", user.getId());
                        remarkJSON.put("username", user.getUsername());
                        remarkJSON.put("amtbl", amtbl);
                        remarkJSON.put("type", FundClearing.FundClearingType.教育培训费.toString());
                        mangeCommAmt = mangeCommAmt.add(amtbl);
                    }
                    {
                        amtbl = BigDecimal.ZERO;
                    }
                }
            }

            // 如果上级没到3星 伯乐奖则部分
            // 条件满足 设置为0说明不紧缩，不设置为0 则金额存在 就会继续找下个满足条件的人，则叫紧缩
            if ((jinConfig == null || !StringUtils.equals(jinConfig.getValue(), "1"))) {
                amtbl = BigDecimal.ZERO;
                {
                    log.info("此订单没有参与紧缩：{}", tankOrders.getOrderSn());
                }
            } else {
                log.info("此订单参与了紧缩：{}", tankOrders.getOrderSn());
            }

            // 增加管理奖 管理奖 是递归算差价 currentUserCapa 不等于空 最低要是董事
            if (currentUserCapa != null) {
                if (xsUserCapa == null || currentUserCapa.getXsCapaId() > xsUserCapa.getXsCapaId()) {

                    XsCapa xsCapa = xsUserCapa == null ? null : xsCapaMng.get(xsUserCapa.getXsCapaId());
                    XsCapa currentXsCapa = xsCapaMng.get(currentUserCapa.getXsCapaId());

                    BigDecimal scale = currentXsCapa.getScale();
                    if (xsCapa != null) {
                        scale = currentXsCapa.getScale().subtract(xsCapa.getScale());
                    }

                    BigDecimal amt = orderAmt.multiply(scale.divide(new BigDecimal("100")));

                    // 增加佣金
                    fundClearingMng.add(user.getId(),
                            "G" + tankOrders.getOrderSn() + user.getId() ,
                            createUser.getUsername() + "[充值共享仓次数]"
                                    + FundClearing.FundClearingType.店务补贴.toString(),
                            amt, DateTimeUtils.getNow(), tankOrders.getOrderSn(),
                            DateTimeUtils.getNow() ,
                            FundClearing.FundClearingType.店务补贴.toString());

                    remarkJSON.put("userId2", user.getId());
                    remarkJSON.put("username2", user.getUsername());
                    remarkJSON.put("amt", amt);
                    remarkJSON.put("type2", FundClearing.FundClearingType.店务补贴.toString());
                    mangeCommAmt = mangeCommAmt.add(amt);

                    // 本次管理提成奖 设置为一下次的伯乐奖
                    amtbl = amtbl.add(amt);
                    xsUserCapa = currentUserCapa;
                }
            }

            if (user.getParentId() == null) {
                break;
            }

            user = userMng.get(user.getParentId());
            {
            }
        } while (true);


    }

    protected void eachb(User createUser, User user, TankOrders order, BigDecimal amtb) {

        BigDecimal amt = BigDecimal.ZERO;
        {
            amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
            fundClearingMng.add(user.getId(), "B" + order.getOrderSn() + user.getId() ,
                    createUser.getUsername() + "[充值共享仓]"
                            + FundClearing.FundClearingType.培训基金.toString(),
                    amt, DateTimeUtils.getNow(), order.getOrderSn(),
                    DateTimeUtils.getNow() ,
                    FundClearing.FundClearingType.培训基金.toString());
        }
        Integer J = 2;
        do {
            if (user.getParentId() == null) {
                break;
            }
            if (J == 2) {
                amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
            } else if (J >= 3 && J <= 4) {
                amt = amtb.multiply(new BigDecimal("0.1")).setScale(2, BigDecimal.ROUND_DOWN);
                ;
            } else if (J >= 5 && J <= 10) {
                amt = amtb.multiply(new BigDecimal("0.05")).setScale(2, BigDecimal.ROUND_DOWN);
                ;
            } else if (J >= 11 && J <= 18) {
                amt = amtb.multiply(new BigDecimal("0.025")).setScale(2, BigDecimal.ROUND_DOWN);
                ;
            }
            user = userMng.get(user.getParentId());
            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());
            // 伯乐奖只有3星以上才有
            if (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3) {
                {
                    fundClearingMng.add(user.getId(),
                            "B" + order.getOrderSn() + user.getId() + "_" + J,
                            createUser.getUsername() + "[充值共享仓次数]"
                                    + FundClearing.FundClearingType.培训基金.toString(),
                            amt, DateTimeUtils.getNow(), order.getOrderSn(),
                            DateTimeUtils.getNow() ,
                            FundClearing.FundClearingType.培训基金.toString());
                }
                J++;
            }
        } while (J <= 18);
    }

}
