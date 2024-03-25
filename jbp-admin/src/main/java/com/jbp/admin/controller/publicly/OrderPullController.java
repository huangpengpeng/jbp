//package com.jbp.admin.controller.publicly;
//
//import com.beust.jcommander.internal.Lists;
//import com.jbp.common.annotation.CustomResponseAnnotation;
//import com.jbp.common.encryptapi.EncryptIgnore;
//import com.jbp.common.model.order.Order;
//import com.jbp.common.response.ErpOrderShipWaitVo;
//import com.jbp.common.utils.SignUtil;
//import com.jbp.service.service.OrderService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//@RestController
//@RequestMapping("api/publicly/order")
//@Api(tags = "订单ERP控制器")
//@CustomResponseAnnotation
//@EncryptIgnore
//public class OrderPullController {
//
//    @Resource
//    private OrderService orderService;
//
//    @ApiOperation(value = "待发货订单列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(value = "/shipWait", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<ErpOrderShipWaitVo> shipWait(String appKey, String timeStr, String method, String sign) {
//        validSign(appKey, timeStr, method, sign);
//        List<ErpOrderShipWaitVo> list = Lists.newArrayList();
//        List<Order> waitShip = orderService.getWaitPullList();
//        if (CollectionUtils.isEmpty(waitShip)) {
//            return list;
//        }
//        waitShip = waitShip.stream().filter(w -> BooleanUtils.isNotTrue(w.getIfErpSync())).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(waitShip)) {
//            return list;
//        }
//        for (Orders orders : waitShip) {
//            User user = userMng.get(orders.getUserId());
//            List<OrderGoods> orderGoodsList = orderGoodsMng.getOrdersId(orders.getId());
//            List<ErpOrderGoodVo> orderGoodVoList = Lists.newArrayList();
//
//            BigDecimal exchangeScore = orders.getExchangeScore() == null ? BigDecimal.ZERO : orders.getExchangeScore();
//            BigDecimal totalOrgPrice = BigDecimal.ZERO;
//            for (OrderGoods orderGoods : orderGoodsList) {
//                BigDecimal orgPrice = orderGoods.getOrgPrice().multiply(BigDecimal.valueOf(orderGoods.getNumber()));
//                totalOrgPrice = totalOrgPrice.add(orgPrice);
//            }
//            for (OrderGoods orderGoods : orderGoodsList) {
//                Goods goods = goodsMng.get(orderGoods.getGoodsId());
//                BigDecimal orgPrice = orderGoods.getOrgPrice().multiply(BigDecimal.valueOf(orderGoods.getNumber()));
//                BigDecimal divide = BigDecimal.ZERO;
//                BigDecimal ratio = BigDecimal.ZERO;
//                if (ArithmeticUtils.gt(totalOrgPrice, BigDecimal.ZERO) && ArithmeticUtils.gt(orgPrice, BigDecimal.ZERO)) {
//                    ratio = orgPrice.divide(totalOrgPrice, 10, BigDecimal.ROUND_DOWN);
//                }
//                if (ArithmeticUtils.gt(ratio, BigDecimal.ZERO) && ArithmeticUtils.gt(exchangeScore, BigDecimal.ZERO)){
//                    divide = ratio.multiply(exchangeScore).divide(BigDecimal.valueOf(orderGoods.getNumber()), 4, BigDecimal.ROUND_DOWN);
//                }
//                ErpOrderGoodVo orderGoodVo = new ErpOrderGoodVo(orderGoods.getGoodsName(),
//                        orderGoods.getNumber(), goods.getUnit(), orderGoods.getSpecificationsBarCode(), orderGoods.getPrice(), divide);
//                orderGoodVoList.add(orderGoodVo);
//            }
//
//            String groupName = "";
//            UserReflection userReflection = userReflectionMng.getByUser(orders.getUserId());
//            if (userReflection != null && userReflection.getLeaderId() != null) {
//                User leader = userMng.get(userReflection.getLeaderId());
//                groupName = leader.getGrouponName();
//            }
//            ErpOrderShipWaitVo vo = new ErpOrderShipWaitVo("A" + orders.getUserId(),
//                    user.getNumberCode(), groupName, user.getUsername(), user.getMobile(), orders.getOrderSn(),
//                    DateTimeUtils.format(orders.getPayTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN),
//                    orders.getFreightPrice(), orders.getPayPrice(), orders.getCouponPrice(),
//                    orders.getExchangeScore(), orderGoodVoList, orders.getReceiveName(),
//                    orders.getMobile(), orders.getProvince(), orders.getCity(), orders.getArea(), orders.getAddress());
//            list.add(vo);
//        }
//        return list;
//    }
//
//    @ApiOperation(value = "待发货回执", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(value = "/erp/order/shipWaitAck", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String shipWaitAck(String appKey, String timeStr, String method, String sign, @RequestBody List<String> ordersSnList) {
//        validSign(appKey, timeStr, method, sign);
//        if (CollectionUtils.isEmpty(ordersSnList)) {
//            throw new RuntimeException("回执单号不能为空");
//        }
//        for (String s : ordersSnList) {
//            Orders orders = ordersMng.getByOrderSn(s);
//            if (orders != null) {
//                orders.setIfErpSync(true);
//                ordersMng.updateByUpdater(orders);
//            }
//        }
//        return "SUCCESS";
//    }
//
//
//    @ApiOperation(value = "发货同步", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(value = "/erp/order/shipSync", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<String> shipSync(String appKey, String timeStr, String method, String sign, @RequestBody List<ErpOrderShipSyncRequest> shipSyncList) {
//        validSign(appKey, timeStr, method, sign);
//        if (CollectionUtils.isEmpty(shipSyncList)) {
//            throw new RuntimeException("回执单号不能为空");
//        }
//        List<String> ordersSnList = Lists.newArrayList();
//        for (ErpOrderShipSyncRequest shipSync : shipSyncList) {
//            Orders orders = ordersMng.getByOrderSn(shipSync.getOrdersSn());
//            if (orders != null && OrderUtils.build(orders).isShip()) {
//                orders.setShipName(shipSync.getShipName());
//                orders.setShipSn(shipSync.getShipNo());
//                orders.setShipTime(new Date());
//                orders.setStatus(OrderUtils.STATUS_SHIP);
//                ordersMng.updateByUpdater(orders);
//                ordersMng.affirmCommClearing(orders.getId());
//                List<OrderGoods> orderGoods = orderGoodsMng.getOrdersId(orders.getId());
//                String goods = "";
//                for (int i = 0; i < orderGoods.size(); i++) {
//                    goods = goods + orderGoods.get(i).getGoodsName() + ";";
//                }
//                messageMng.pushOrderShipMsg(orders.getUserId(), orders.getMobile(), orders.getOrderSn(),
//                        orders.getShipName(), orders.getShipSn());
//                ordersSnList.add(shipSync.getOrdersSn());
//            }else{
//                ordersSnList.add(shipSync.getOrdersSn());
//            }
//        }
//        return ordersSnList;
//    }
//
//    @ApiOperation(value = "获取加密字符串", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(value = "/erp/order/sign", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String sign(String appKey, String timeStr, String method) throws Exception {
//        Map<String, Object> map = new HashMap<>();
//        map.put("appKey", appKey);
//        map.put("method", method);
//        map.put("timeStr", timeStr);
//        return SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map);
//    }
//
//    private void validSign(String appKey, String timeStr, String method, String sign) {
//        if (StringUtils.isAnyBlank(appKey, timeStr, method, sign)) {
//            throw new RuntimeException("签名参数错误");
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("appKey", appKey);
//        map.put("timeStr", timeStr);
//        map.put("method", method);
//        String tagSign = SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map);
//        if (!tagSign.equals(sign)) {
//            throw new RuntimeException("签名错误");
//        }
//    }
//
//
//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("appKey", "fny");
//        map.put("method", "shipWait");
//        map.put("timeStr", "1703034721");
//        // 11D8B138A75484F5E36A805AE3B3B2D2
//        System.out.println(SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map));
//    }
//
//}
