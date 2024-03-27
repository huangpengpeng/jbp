package com.jbp.admin.controller.publicly;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.annotation.CustomResponseAnnotation;
import com.jbp.common.encryptapi.EncryptIgnore;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.user.User;
import com.jbp.common.request.ErpOrderShipSyncRequest;
import com.jbp.common.request.OrderSendRequest;
import com.jbp.common.request.SplitOrderSendDetailRequest;
import com.jbp.common.response.ErpOrderGoodVo;
import com.jbp.common.response.ErpOrderShipWaitVo;
import com.jbp.common.utils.AddressUtil;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.SignUtil;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.ProductMaterialsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("api/publicly/order")
@Api(tags = "订单ERP控制器")
@CustomResponseAnnotation
@EncryptIgnore
public class OrderPullController {

    @Resource
    private TeamUserService teamUserService;
    @Resource
    private ProductService productService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ProductMaterialsService productMaterialsService;
    @Resource
    private MerchantOrderService merchantOrderService;
    @Autowired
    private Environment environment;

    @ApiOperation(value = "待发货订单列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/shipWait", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ErpOrderShipWaitVo> shipWait(String appKey, String timeStr, String method, String sign) {
        validSign(appKey, timeStr, method, sign);
        List<ErpOrderShipWaitVo> list = Lists.newArrayList();
        List<Order> waitShip = orderService.getWaitPullList();
        if (CollectionUtils.isEmpty(waitShip)) {
            return list;
        }
        if (CollectionUtils.isEmpty(waitShip)) {
            return list;
        }
        for (Order order : waitShip) {
            User user = userService.getById(order.getUid());
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<ErpOrderGoodVo> orderGoodVoList = Lists.newArrayList();

            for (OrderDetail orderGoods : orderDetailList) {
                Product product = productService.getById(orderGoods.getProductId());
                List<ProductMaterials> productMaterials = productMaterialsService.getByBarCode(orderGoods.getMerId(), orderGoods.getBarCode());
                BigDecimal goodsPrice = orderGoods.getPayPrice().subtract(orderGoods.getFreightFee());
                if (CollectionUtils.isEmpty(productMaterials)) {
                    ErpOrderGoodVo orderGoodVo = new ErpOrderGoodVo(orderGoods.getProductName(),
                            orderGoods.getPayNum(), product.getUnitName(), orderGoods.getBarCode(),
                            goodsPrice.divide(BigDecimal.valueOf(orderGoods.getPayNum()), 4, BigDecimal.ROUND_DOWN),
                            orderGoods.getWalletDeductionFee().divide(BigDecimal.valueOf(orderGoods.getPayNum()), 4, BigDecimal.ROUND_DOWN));
                    orderGoodVoList.add(orderGoodVo);
                } else {
                    BigDecimal total = BigDecimal.ZERO;
                    for (ProductMaterials productMaterial : productMaterials) {
                        total = total.add(productMaterial.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterial.getMaterialsQuantity())));
                    }

                    for (ProductMaterials productMaterial : productMaterials) {
                        Integer payNum = orderGoods.getPayNum() * productMaterial.getMaterialsQuantity();
                        BigDecimal orgTotal = productMaterial.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterial.getMaterialsQuantity()));

                        BigDecimal divide = BigDecimal.ZERO;
                        if (ArithmeticUtils.gt(total, BigDecimal.ZERO) && ArithmeticUtils.gt(orgTotal, BigDecimal.ZERO)) {
                            divide = orgTotal.divide(total, 4, BigDecimal.ROUND_DOWN);
                        }

                        BigDecimal price = BigDecimal.ZERO;
                        if (ArithmeticUtils.gt(divide, BigDecimal.ZERO) && ArithmeticUtils.gt(goodsPrice, BigDecimal.ZERO)) {
                            price = goodsPrice.multiply(divide).divide(BigDecimal.valueOf(payNum), 4, BigDecimal.ROUND_DOWN);
                        }
                        BigDecimal walletDeductionFee = BigDecimal.ZERO;
                        if (ArithmeticUtils.gt(divide, BigDecimal.ZERO) && ArithmeticUtils.gt(orderGoods.getWalletDeductionFee(), BigDecimal.ZERO)) {
                            walletDeductionFee = orderGoods.getWalletDeductionFee().multiply(divide).divide(BigDecimal.valueOf(payNum), 4, BigDecimal.ROUND_DOWN);
                        }
                        ErpOrderGoodVo orderGoodVo = new ErpOrderGoodVo(productMaterial.getMaterialsName(),
                                payNum, product.getUnitName(), productMaterial.getMaterialsCode(),
                                price, walletDeductionFee);
                        orderGoodVoList.add(orderGoodVo);
                    }
                }
            }
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
            TeamUser teamUser = teamUserService.getByUser(order.getUid());
            Map<String, String> address = AddressUtil.getAddress(merchantOrder.getUserAddress());

            String userPrefix = environment.getProperty("erp.userPrefix");
            userPrefix = StringUtils.isBlank(userPrefix) ? "" : userPrefix;
            ErpOrderShipWaitVo vo = new ErpOrderShipWaitVo(userPrefix + order.getUid(),
                    user.getAccount(), teamUser.getName(), user.getNickname(), user.getPhone(), order.getPlatOrderNo(),
                    DateTimeUtils.format(order.getPayTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN),
                    order.getTotalPostage(), order.getPayPrice(), order.getCouponPrice(),
                    order.getWalletDeductionFee(), orderGoodVoList, merchantOrder.getRealName(),
                    merchantOrder.getUserPhone(), address.get("province"), address.get("city"),  address.get("district"),  (address.get("street").equals("null") ? "" : address.get("street")) + address.get("detail"));
            list.add(vo);
        }
        return list;
    }

    @ApiOperation(value = "待发货回执", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/erp/order/shipWaitAck", produces = MediaType.APPLICATION_JSON_VALUE)
    public String shipWaitAck(String appKey, String timeStr, String method, String sign, @RequestBody List<String> ordersSnList) {
        validSign(appKey, timeStr, method, sign);
        if (CollectionUtils.isEmpty(ordersSnList)) {
            throw new RuntimeException("回执单号不能为空");
        }
        for (String s : ordersSnList) {
            List<Order> orderList = orderService.getByPlatOrderNo(s);
            if (CollectionUtils.isNotEmpty(orderList)) {
                for (Order order : orderList) {
                    order.setIfPull(true);
                    orderService.updateById(order);
                }
            }
        }
        return "SUCCESS";
    }


    @ApiOperation(value = "发货同步", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/erp/order/shipSync", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> shipSync(String appKey, String timeStr, String method, String sign,
                                 @RequestBody List<ErpOrderShipSyncRequest> shipSyncList) {
        validSign(appKey, timeStr, method, sign);
        if (CollectionUtils.isEmpty(shipSyncList)) {
            throw new RuntimeException("回执单号不能为空");
        }
        return send(shipSyncList);
    }

    public List<String> send(List<ErpOrderShipSyncRequest> shipSyncList) {
        List<String> ordersSnList = Lists.newArrayList();
        for (ErpOrderShipSyncRequest shipSync : shipSyncList) {
            List<Order> orderList = orderService.getByPlatOrderNo(shipSync.getOrdersSn());
            Order orders = CollectionUtils.isNotEmpty(orderList) ? orderList.get(0) : null;
            if (orders != null && orders.getStatus().equals(1)) {

                OrderSendRequest orderSendRequest = new OrderSendRequest();
                orderSendRequest.setOrderNo(orders.getOrderNo());
                orderSendRequest.setDeliveryType("express");
                orderSendRequest.setExpressCode(shipSync.getShipCode());
                orderSendRequest.setIsSplit(false);
                orderSendRequest.setExpressNumber(shipSync.getShipNo());
                orderSendRequest.setExpressRecordType(1);
                MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orders.getOrderNo());
                orderSendRequest.setToName(merchantOrder.getRealName());
                orderSendRequest.setToTel(merchantOrder.getUserPhone());
                orderSendRequest.setToAddr(merchantOrder.getUserAddress());
                List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orders.getOrderNo());
                List<SplitOrderSendDetailRequest> list = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetailList) {
                    SplitOrderSendDetailRequest splitOrderSendDetailRequest = new SplitOrderSendDetailRequest();
                    splitOrderSendDetailRequest.setNum(orderDetail.getPayNum());
                    splitOrderSendDetailRequest.setOrderDetailId(orderDetail.getId());
                    list.add(splitOrderSendDetailRequest);
                }
                orderSendRequest.setDetailList(list);
                orderSendRequest.setExpressTempId(shipSync.getShipNo());
                try {
                    orderService.send(orderSendRequest);
                    ordersSnList.add(shipSync.getOrdersSn());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ordersSnList;
    }


    private void validSign(String appKey, String timeStr, String method, String sign) {
        if (StringUtils.isAnyBlank(appKey, timeStr, method, sign)) {
            throw new RuntimeException("签名参数错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("appKey", appKey);
        map.put("timeStr", timeStr);
        map.put("method", method);
        String tagSign = SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map);
        if (!tagSign.equals(sign)) {
            throw new RuntimeException("签名错误");
        }
    }


    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("appKey", "fny");
        map.put("method", "shipWait");
        map.put("timeStr", "1703034721");
        // 11D8B138A75484F5E36A805AE3B3B2D2
        System.out.println(SignUtil.getSignToUpperCase("2e556e8f433dc3b9971aa21fa32458b8", map));
    }

}
