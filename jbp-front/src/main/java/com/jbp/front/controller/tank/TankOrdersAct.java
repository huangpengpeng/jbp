package com.jbp.front.controller.tank;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.constants.SysConfigConstants;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.response.TankStoreListResponse;
import com.jbp.common.response.TankStoreRelationListResponse;
import com.jbp.common.result.CommonResult;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/front/tankOrder")
@Api(tags = "共享仓订单控制器")
public class TankOrdersAct {


    @Resource
    private TankOrdersService tankOrdersService;
    @Resource
    private TankEquipmentNumberService tankEquipmentNumberService;
    @Resource
    private TankStoreRelationService tankStoreRelationService;
    @Resource
    private TankStoreService tankStoreService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TankOrdersAct.class);

    @Autowired
    private Environment environment;


    @ApiOperation(value = "充值下单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/addOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<TankOrders> addOrder(Integer number, Long storeUserId) {


       String json = systemConfigService.getValueByKey(SysConfigConstants.GXC_RECHARGE_NUMBER);

        JSONArray validArray = JSONArray.parseArray(json);
        BigDecimal payPrice = BigDecimal.ZERO;
        for (Object object : validArray) {
            JSONObject validJSON = (JSONObject) object;
            Integer number1 = validJSON.getInteger("number");
            if (number.equals(number1)) {
                payPrice = validJSON.getBigDecimal("price");
            }
        }

        TankOrders tankOrders = new TankOrders();
        tankOrders.setUserId(userService.getInfo().getId().longValue());
        tankOrders.setStoreUserId(storeUserId);
        tankOrders.setOrderSn(CrmebUtil.getOrderNo(OrderConstants.GXC_ORDER_PREFIX));
        tankOrders.setStatus("未支付");
        tankOrders.setNumber(number);
        tankOrders.setPayPrice(payPrice);
        tankOrders.setCreatedTime(new Date());
        tankOrdersService.save(tankOrders);
        return CommonResult.success(tankOrders);
    }

    @ApiOperation(value = "充值次数选择", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getNumberList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> getNumberList(BigDecimal score) {
        String json = systemConfigService.getValueByKey(SysConfigConstants.GXC_RECHARGE_NUMBER);
        return CommonResult.success(json);
    }


    @ApiOperation(value = "舱主关联店主列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getTankStoreRelationList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<TankStoreRelationListResponse>> getTankStoreRelation() {
        return CommonResult.success(tankStoreRelationService.getRelationList());
    }


    @ApiOperation(value = "店主门店列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @RequestMapping(value = "/getTankStoreList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<List<TankStoreListResponse>> getTankStoreList(Integer storeUserId) {

        return CommonResult.success(tankStoreService.getStoreList(storeUserId));
    }


    @ApiOperation(value = "充值次数成功回调", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/callapi", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<String> callapi(@RequestParam Map<String, Object> params, @ApiIgnore HttpServletResponse response,
                                        ModelMap model) throws Exception {
        try {
            JSONObject queryJSON = JSONObject.parseObject(JSONObject.toJSONString(params));

            String orderNo = queryJSON.getString("orderNo");
            if (StringUtils.isBlank(orderNo)) {
                return CommonResult.success("SUCCESS");
            }

            TankOrders tankOrders = tankOrdersService.getOrderSn(orderNo);
            if (StringUtils.equals(tankOrders.getStatus(), "已支付")) {
                return CommonResult.success("SUCCESS");
            }

            String url = environment.getProperty("gxc.url");
            Map<String, String> queryMap = Maps.newLinkedHashMap();
            queryMap.put("orderNo", orderNo);
            String responseText = "{}";
            URIBuilder uriBuilder = new URIBuilder(url);
            {
                logger.info("uriBuilder:{}", uriBuilder.toString());
            }
            HttpRequest httpRequest = HttpRequest.get(uriBuilder.toString()).query(queryMap);
            HttpResponse httpResponse = httpRequest.send();
            responseText = StringUtils.toString(httpResponse.bodyBytes(), "utf-8");
            {
                logger.info("responseText:{}", responseText);
            }
            JSONObject orderJSON = JSONObject.parseObject(responseText);
            Boolean ifSuccess = orderJSON.getBoolean("ifSuccess");
            {
                logger.info("ifSuccess:{}", ifSuccess);
            }

            synchronized (orderNo.intern()) {
                if (ifSuccess) {

                    tankOrders.setStatus("已支付");
                    tankOrders.setPayTime(new Date());
                    tankOrdersService.updateById(tankOrders);
                    tankEquipmentNumberService.increase(tankOrders.getStoreUserId(), tankOrders.getNumber(), tankOrders.getOrderSn(), null);

                    //发放佣金
                    //      fuxiao(tankOrders);
                    //    bole(tankOrders);

                }
            }

            return CommonResult.success("SUCCESS");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return CommonResult.success("SUCCESS");
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

//
//    //伯乐  团队奖
//    public void bole(TankOrders tankOrders) {
//
//
//        Config jinConfig = configMng.getByName("紧缩订单号");
//
//        TankStoreRelation tankStoreRelation = tankStoreRelationService.getStoreUserId(tankOrders.getStoreUserId());
//        if (tankStoreRelation == null) {
//            return;
//        }
//        User createUser = userMng.get(tankStoreRelation.getTankUserId());
//        if (createUser == null) {
//            return;
//        }
//        if (createUser.getParentId() == null) {
//            return;
//        }
//
//        BigDecimal mangeCommAmt = BigDecimal.ZERO;
//        JSONObject remarkJSON = new JSONObject();
//
//
//        BigDecimal orderAmt = tankOrders.getPayPrice();
//
//
//        User user = userMng.get(createUser.getParentId());
//        XsUserCapa xsUserCapa = null;
//        BigDecimal amtbl = BigDecimal.ZERO;
//        do {
//
//            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());
//
//            // 伯乐奖只有3星以上才有
//            if (xsUserCapa != null && (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3)) {
//                // 增加伯乐奖
//                if (ArithmeticUtils.gt(amtbl, BigDecimal.ZERO)) {
//                    {
//                        eachb(createUser, user, tankOrders, amtbl);
//                    }
//                    {
//                        remarkJSON.put("userId", user.getId());
//                        remarkJSON.put("username", user.getUsername());
//                        remarkJSON.put("amtbl", amtbl);
//                        remarkJSON.put("type", FundClearing.FundClearingType.教育培训费.toString());
//                        mangeCommAmt = mangeCommAmt.add(amtbl);
//                    }
//                    {
//                        amtbl = BigDecimal.ZERO;
//                    }
//                }
//            }
//
//            // 如果上级没到3星 伯乐奖则部分
//            // 条件满足 设置为0说明不紧缩，不设置为0 则金额存在 就会继续找下个满足条件的人，则叫紧缩
//            if ((jinConfig == null || !StringUtils.equals(jinConfig.getValue(), "1"))) {
//                amtbl = BigDecimal.ZERO;
//                {
//                    log.info("此订单没有参与紧缩：{}", tankOrders.getOrderSn());
//                }
//            } else {
//                log.info("此订单参与了紧缩：{}", tankOrders.getOrderSn());
//            }
//
//            // 增加管理奖 管理奖 是递归算差价 currentUserCapa 不等于空 最低要是董事
//            if (currentUserCapa != null) {
//                if (xsUserCapa == null || currentUserCapa.getXsCapaId() > xsUserCapa.getXsCapaId()) {
//
//                    XsCapa xsCapa = xsUserCapa == null ? null : xsCapaMng.get(xsUserCapa.getXsCapaId());
//                    XsCapa currentXsCapa = xsCapaMng.get(currentUserCapa.getXsCapaId());
//
//                    BigDecimal scale = currentXsCapa.getScale();
//                    if (xsCapa != null) {
//                        scale = currentXsCapa.getScale().subtract(xsCapa.getScale());
//                    }
//
//                    BigDecimal amt = orderAmt.multiply(scale.divide(new BigDecimal("100")));
//
//                    // 增加佣金
//                    fundClearingMng.add(user.getId(),
//                            "G" + tankOrders.getOrderSn() + user.getId(),
//                            createUser.getUsername() + "[充值共享仓次数]"
//                                    + FundClearing.FundClearingType.店务补贴.toString(),
//                            amt, DateTimeUtils.getNow(), tankOrders.getOrderSn(),
//                            DateTimeUtils.getNow(),
//                            FundClearing.FundClearingType.店务补贴.toString());
//
//                    remarkJSON.put("userId2", user.getId());
//                    remarkJSON.put("username2", user.getUsername());
//                    remarkJSON.put("amt", amt);
//                    remarkJSON.put("type2", FundClearing.FundClearingType.店务补贴.toString());
//                    mangeCommAmt = mangeCommAmt.add(amt);
//
//                    // 本次管理提成奖 设置为一下次的伯乐奖
//                    amtbl = amtbl.add(amt);
//                    xsUserCapa = currentUserCapa;
//                }
//            }
//
//            if (user.getParentId() == null) {
//                break;
//            }
//
//            user = userMng.get(user.getParentId());
//            {
//            }
//        } while (true);
//
//
//    }
//
//    protected void eachb(User createUser, User user, TankOrders order, BigDecimal amtb) {
//
//        BigDecimal amt = BigDecimal.ZERO;
//        {
//            amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
//            fundClearingMng.add(user.getId(), "B" + order.getOrderSn() + user.getId(),
//                    createUser.getUsername() + "[充值共享仓]"
//                            + FundClearing.FundClearingType.培训基金.toString(),
//                    amt, DateTimeUtils.getNow(), order.getOrderSn(),
//                    DateTimeUtils.getNow(),
//                    FundClearing.FundClearingType.培训基金.toString());
//        }
//        Integer J = 2;
//        do {
//            if (user.getParentId() == null) {
//                break;
//            }
//            if (J == 2) {
//                amt = amtb.multiply(new BigDecimal("0.15")).setScale(2, BigDecimal.ROUND_DOWN);
//            } else if (J >= 3 && J <= 4) {
//                amt = amtb.multiply(new BigDecimal("0.1")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            } else if (J >= 5 && J <= 10) {
//                amt = amtb.multiply(new BigDecimal("0.05")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            } else if (J >= 11 && J <= 18) {
//                amt = amtb.multiply(new BigDecimal("0.025")).setScale(2, BigDecimal.ROUND_DOWN);
//                ;
//            }
//            user = userMng.get(user.getParentId());
//            XsUserCapa currentUserCapa = xsUserCapaMng.getByUser(user.getId());
//            // 伯乐奖只有3星以上才有
//            if (currentUserCapa != null && currentUserCapa.getXsCapaId() >= 3) {
//                {
//                    fundClearingMng.add(user.getId(),
//                            "B" + order.getOrderSn() + user.getId() + "_" + J,
//                            createUser.getUsername() + "[充值共享仓次数]"
//                                    + FundClearing.FundClearingType.培训基金.toString(),
//                            amt, DateTimeUtils.getNow(), order.getOrderSn(),
//                            DateTimeUtils.getNow(),
//                            FundClearing.FundClearingType.培训基金.toString());
//                }
//                J++;
//            }
//        } while (J <= 18);
//    }

}
