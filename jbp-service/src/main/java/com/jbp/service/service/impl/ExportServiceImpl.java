package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.OrderExcelInfoVo;
import com.jbp.common.vo.OrderExcelShipmentVo;
import com.jbp.common.vo.OrderExcelVo;
import com.jbp.common.vo.OrderShipmentExcelInfoVo;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.ProductMaterialsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ExcelServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Resource
    private MerchantOrderService merchantOrderService;
    @Resource
    private ProductMaterialsService productMaterialsService;
    @Resource
    private TeamUserService teamUserService;

    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     *
     */
    @Override
    public OrderShipmentExcelInfoVo exportOrderShipment(OrderSearchRequest request) {
        if (StringUtils.isEmpty(request.getOrderNo()) && StringUtils.isEmpty(request.getPlatOrderNo())
                && StringUtils.isEmpty(request.getDateLimit()) && StringUtils.isEmpty(request.getStatus())
                && ObjectUtils.isEmpty(request.getType())) {
            throw new CrmebException("请至少选择一个查询条件");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }

        Integer id = 0;
        List<OrderExcelShipmentVo> voList = CollUtil.newArrayList();
        Map<String, String> walletDeductionMap = Maps.newConcurrentMap();

        Map<String, List<ProductMaterials>> materialsMap = Maps.newConcurrentMap();
        do {
            List<Order> orderList = orderService.findExportList(request, id);
            if (CollectionUtils.isEmpty(orderList)) {
                break;
            }
            // 商户 用户 订单 数据准备
            List<Integer> userIdList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
            List<Integer> payUserIdList = orderList.stream().map(Order::getPayUid).distinct().collect(Collectors.toList());
            userIdList.addAll(payUserIdList);
            userIdList = userIdList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
            List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
            Map<Integer, User> userMap = userService.getUidMapList(userIdList);
            Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);

            // 导出对象
            for (Order order : orderList) {
                // 商户详情
                MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
                // 订单商品
                List<OrderDetail> orderDetailsList = orderDetailMap.get(order.getOrderNo());
                // 循环设置
                for (OrderDetail orderDetail : orderDetailsList) {
                    // 获取物料信息
                    BigDecimal payPrice = (orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                    List<ProductMaterials> productMaterials = materialsMap.get(orderDetail.getBarCode());
                    if (CollectionUtils.isEmpty(productMaterials)) {
                        productMaterials = productMaterialsService.getByBarCode(merchantOrder.getMerId(), orderDetail.getBarCode());
                        if (CollectionUtils.isEmpty(productMaterials)) {
                            productMaterials = Lists.newArrayList();
                            ProductMaterials materials = new ProductMaterials(merchantOrder.getMerId(),
                                    orderDetail.getBarCode(), orderDetail.getProductName(), 1, payPrice.divide(BigDecimal.valueOf(orderDetail.getPayNum()), 4, BigDecimal.ROUND_DOWN), orderDetail.getBarCode());
                            productMaterials.add(materials);
                        }
                        materialsMap.put(orderDetail.getBarCode(), productMaterials);
                    }

                    // 物料总价
                    BigDecimal materialsTotalPrice = BigDecimal.ZERO;
                    for (ProductMaterials materials : productMaterials) {
                        materialsTotalPrice = materialsTotalPrice.add(materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity())));
                    }
                    // 物料信息
                    for (ProductMaterials materials : productMaterials) {
                        // 组装订单
                        OrderExcelShipmentVo vo = new OrderExcelShipmentVo();
                        vo.setType(order.getOrderType());
                        vo.setOrderNo(order.getOrderNo());
                        if (StringUtils.isNotEmpty(order.getPlatOrderNo())) {
                            vo.setOrderNo(order.getPlatOrderNo());
                        }
                        vo.setPlatform(order.getPlatform());
                        if (order.getUid() != null) {
                            TeamUser teamUser = teamUserService.getByUser(order.getUid());
                            if (teamUser != null) {
                                vo.setTeam(teamUser.getName());
                            }
                        } else {
                            vo.setTeam("");
                        }
                        if (order.getUid() != null) {
                            vo.setUid(order.getUid());
                            vo.setUserAccount(userMap.get(order.getUid()).getAccount());
                        }
                        if (order.getPayUid() != null) {
                            vo.setPayUserAccount(userMap.get(order.getPayUid()).getAccount());
                        }
                        vo.setPayPrice(order.getPayPrice().subtract(order.getPayPostage()));
                        vo.setPayPostage(order.getPayPostage());
                        vo.setCouponPrice(order.getCouponPrice());
                        vo.setWalletDeductionFee(order.getWalletDeductionFee());
                        vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
                        vo.setOrderPayType(order.getOrderPayType());
                        vo.setPayMethod(order.getPayMethod());
                        vo.setPayChannel(order.getPayChannel());
                        vo.setStatus(order.getOrderStatus());
                        vo.setRefundStatus(order.getOrderRefundStatus());
                        // 原始产品
                        vo.setOrderDetailId(orderDetail.getId());
                        vo.setProductName(orderDetail.getProductName());
                        vo.setProductBarCode(orderDetail.getBarCode());
                        vo.setProductQuantity(orderDetail.getPayNum());
                        vo.setProductPrice(payPrice);
                        vo.setProductPostage(orderDetail.getFreightFee());
                        vo.setProductCouponPrice(orderDetail.getCouponPrice());
                        vo.setProductWalletDeductionFee(orderDetail.getWalletDeductionFee());
                        vo.setWalletDeductionList(orderDetail.getWalletDeductionList());
                        if (CollectionUtils.isNotEmpty(vo.getWalletDeductionList())) {
                            for (ProductDeduction deduction : vo.getWalletDeductionList()) {
                                walletDeductionMap.put(deduction.getWalletType().toString(), deduction.getWalletName());
                            }
                        }
                        // 物料信息
                        BigDecimal price = materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity()));
                        if (ArithmeticUtils.gt(materialsTotalPrice, BigDecimal.ZERO) && ArithmeticUtils.gt(payPrice, BigDecimal.ZERO)) {
                            BigDecimal materialsPrice = payPrice.multiply(price.divide(materialsTotalPrice, 10, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
                            vo.setMaterialsPrice(materialsPrice);
                        } else {
                            vo.setMaterialsPrice(BigDecimal.ZERO);
                        }
                        vo.setMaterialsName(materials.getMaterialsName());
                        vo.setMaterialsCode(materials.getMaterialsCode());
                        vo.setMaterialsQuantity(orderDetail.getPayNum() * materials.getMaterialsQuantity());

                        // 收货人
                        vo.setRealName(merchantOrder.getRealName());
                        vo.setUserPhone(merchantOrder.getUserPhone());
                        vo.setShippingType(1 == merchantOrder.getShippingType() ? "快递" : "自提");
                        vo.setUserRemark(merchantOrder.getUserRemark());
                        vo.setMerchantRemark(merchantOrder.getMerchantRemark());
                        vo.setUserAddress(merchantOrder.getUserAddress());

                        // 下单时间
                        vo.setCreateTime(CrmebDateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

                        // 保存
                        voList.add(vo);
                    }
                }
            }
            id = orderList.get(orderList.size() - 1).getId();
        } while (true);

        OrderShipmentExcelInfoVo vo = new OrderShipmentExcelInfoVo();
        LinkedHashMap<String, String> head = new LinkedHashMap<String, String>();
        head.put("orderDetailId", "订单详情ID");
        head.put("type", "订单类型");
        head.put("platform", "场景");
        head.put("orderNo", "单号");
        head.put("uid", "用户ID");
        head.put("userAccount", "下单账号");
        head.put("team", "团队");
        head.put("payUserAccount", "付款账号");
        head.put("payPrice", "货款");
        head.put("payPostage", "运费");
        head.put("couponPrice", "优惠");
        head.put("walletDeductionFee", "抵扣");
        head.put("paidStr", "支付状态");
        head.put("orderPayType", "支付方式");
        head.put("payMethod", "支付方法");
        head.put("payChannel", "支付渠道");
        head.put("status", "订单状态");
        head.put("refundStatus", "退款状态");

        head.put("productName", "商品名称");
        head.put("productBarCode", "商品编码");
        head.put("productQuantity", "商品数量");
        head.put("productPrice", "商品总价");

        head.put("materialsName", "物料名称");
        head.put("materialsCode", "物料编码");
        head.put("materialsQuantity", "物料数量");
        head.put("materialsPrice", "物料总价");

        head.put("realName", "收货人");
        head.put("userPhone", "收货人手机");
        head.put("shippingType", "配送方式");
        head.put("userAddress", "收货详情地址");
        head.put("userRemark", "用户备注");
        head.put("merchantRemark", "商户备注");
        head.put("createTime", "下单时间");

        head.put("productPostage", "商品运费");
        head.put("productCouponPrice", "商品优惠");
        head.put("productWalletDeductionFee", "商品抵扣");

        Map<String, String> sortedMap = walletDeductionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        sortedMap.forEach((k, v) -> {
            head.put(k, v);
        });
        JSONArray array = new JSONArray();
        head.forEach((k, v) -> {
            JSONObject json = new JSONObject();
            json.put("k", k);
            json.put("v", v);
            array.add(json);
        });
        vo.setHead(array);
        vo.setList(voList);
        return vo;
    }

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public OrderExcelInfoVo exportOrder(OrderSearchRequest request) {
        if (StringUtils.isEmpty(request.getOrderNo()) && StringUtils.isEmpty(request.getPlatOrderNo())
                && StringUtils.isEmpty(request.getDateLimit()) && StringUtils.isEmpty(request.getStatus())
                && ObjectUtils.isEmpty(request.getType())) {
            throw new CrmebException("请至少选择一个查询条件");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }
        Integer id = 0;
        List<OrderExcelVo> voList = CollUtil.newArrayList();
        do {
            List<Order> orderList = orderService.findExportList(request, id);
            if (CollectionUtils.isEmpty(orderList)) {
                break;
            }
            List<Integer> userIdList = orderList.stream().filter(s->s.getUid()!= null).map(Order::getUid).distinct().collect(Collectors.toList());
            List<Integer> payUserIdList = orderList.stream().filter(s->s.getPayUid()!= null).map(Order::getPayUid).distinct().collect(Collectors.toList());
            userIdList.addAll(payUserIdList);
            Set<Integer> userSet = userIdList.stream().collect(Collectors.toSet());
            List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
            Map<Integer, User> userMap = userService.getUidMapList(userSet.stream().collect(Collectors.toList()));
            Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
            for (Order order : orderList) {
                MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
                // 循环设置
                OrderExcelVo vo = new OrderExcelVo();
                vo.setType(getOrderType(order.getType()));
                vo.setOrderNo(StringUtils.isNotEmpty(order.getPlatOrderNo()) ? order.getPlatOrderNo() : order.getOrderNo());
                vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
                vo.setPayType(getOrderPayType(order.getPayType()));
                vo.setPayChannel(getOrderPayChannel(order.getPayChannel()));
                vo.setStatus(getOrderStatus(order.getStatus()));
                vo.setRefundStatus(getOrderRefundStatus(order.getRefundStatus()));
                vo.setWalletDeductionFee(order.getWalletDeductionFee());
                vo.setCreateTime(CrmebDateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                vo.setProductInfo(getOrderProductInfo(orderDetailMap.get(order.getOrderNo())));

                // 收货人
                vo.setRealName(merchantOrder.getRealName());
                vo.setUserPhone(merchantOrder.getUserPhone());
                vo.setShippingType(1 == merchantOrder.getShippingType() ? "快递" : "自提");
                vo.setUserRemark(merchantOrder.getUserRemark());
                vo.setMerchantRemark(merchantOrder.getMerchantRemark());
                vo.setUserAddress(merchantOrder.getUserAddress());
                vo.setPayPrice(order.getPayPrice().subtract(order.getPayPostage()));
                vo.setPayPostage(order.getPayPostage());
                vo.setCouponPrice(order.getCouponPrice());
                //设置场景
                vo.setPlatform(order.getPlatform());
                if (order.getUid() != null) {
                    TeamUser teamUser = teamUserService.getByUser(order.getUid());
                    if (teamUser != null) {
                        vo.setTeam(teamUser.getName());
                    }
                } else {
                    vo.setTeam("");
                }
                //设置团队
                if (order.getUid() != null) {
                    TeamUser teamUser = teamUserService.getByUser(order.getUid());
                    if (teamUser != null) {
                        vo.setTeam(teamUser.getName());
                    }
                } else {
                    vo.setTeam("");
                }
                //设置用id
                if (order.getUid() != null) {
                    vo.setUid(order.getUid());
                    vo.setUserNickname(userMap.get(order.getUid()) != null ? userMap.get(order.getUid()).getNickname() : "");
                    vo.setUserAccount(userMap.get(order.getUid()).getAccount());
                }
                //设置付款账号
                if (order.getPayUid() != null) {
                    vo.setPayUserAccount(userMap.get(order.getPayUid()).getAccount());
                }
                voList.add(vo);
            }
            id = orderList.get(orderList.size() - 1).getId();
        } while (true);

        OrderExcelInfoVo orderExcelInfoVo=new OrderExcelInfoVo();
        //自定义标题别名
        LinkedHashMap<String, String> head = new LinkedHashMap<String, String>();
        head.put("type", "订单类型");
        head.put("orderNo", "订单号");
        head.put("platform","场景");
        head.put("team","团队");
        head.put("uid","用户编号");
        head.put("userNickname", "用户昵称");
        head.put("userAccount","下单账号");
        head.put("payUserAccount","付款账号");
        head.put("payPostage","整单运费");
        head.put("couponPrice","整单优惠");
        head.put("payPrice", "整单金额");
        head.put("walletDeductionFee","整单抵扣");
        head.put("payType", "支付方式");
        head.put("payChannel", "支付渠道");
        head.put("paidStr", "支付状态");
        head.put("refundStatus", "退款状态");
        head.put("status", "订单状态");
        head.put("realName","收货人");
        head.put("userPhone","收货人手机");
        head.put("shippingType","配送方式");
        head.put("userAddress","收货详情地址");
        head.put("productInfo", "商品信息");
        head.put("userRemark","用户备注");
        head.put("merchantRemark","商户备注");
        head.put("createTime", "创建时间");
        JSONArray array = new JSONArray();
        head.forEach((k, v) -> {
            JSONObject json = new JSONObject();
            json.put("k", k);
            json.put("v", v);
            array.add(json);
        });
        orderExcelInfoVo.setHead(array);
        orderExcelInfoVo.setList(voList);
        return orderExcelInfoVo;
    }

    private String getOrderProductInfo(List<OrderDetail> orderDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
            stringBuilder.append(StrUtil.format("{}  {} * {}", orderDetail.getProductName(), orderDetail.getPayPrice(), orderDetail.getPayNum()));
            if ((i + 1) < orderDetails.size()) {
                stringBuilder.append("\r\n");
            }
        }
        return stringBuilder.toString();
    }

    private String getOrderType(Integer type) {
        String typeStr = "";
        switch (type) {
            case 0:
                typeStr = "普通";
                break;
            case 1:
                typeStr = "视频号";
                break;
            case 2:
                typeStr = "秒杀";
                break;
        }
        return typeStr;
    }

    private String getOrderRefundStatus(Integer refundStatus) {
        String refundStatusStr = "";
        switch (refundStatus) {
            case 0:
                refundStatusStr = "未退款";
                break;
            case 1:
                refundStatusStr = "申请中";
                break;
            case 2:
                refundStatusStr = "部分退款";
                break;
            case 3:
                refundStatusStr = "已退款";
                break;
        }
        return refundStatusStr;
    }

    private String getOrderStatus(Integer status) {
        String statusStr = "";
        switch (status) {
            case 0:
                statusStr = "待支付";
                break;
            case 1:
                statusStr = "待发货";
                break;
            case 2:
                statusStr = "部分发货";
                break;
            case 3:
                statusStr = "待核销";
                break;
            case 4:
                statusStr = "待收货";
                break;
            case 5:
                statusStr = "已收货";
                break;
            case 6:
                statusStr = "已完成";
                break;
            case 9:
                statusStr = "已取消";
                break;
        }
        return statusStr;
    }

    private String getOrderPayChannel(String payChannel) {
        String payChannelStr = "";
        switch (payChannel) {
            case "public":
                payChannelStr = "公众号";
                break;
            case "mini":
                payChannelStr = "小程序";
                break;
            case "h5":
                payChannelStr = "微信网页支付";
                break;
            case "yue":
                payChannelStr = "余额";
                break;
            case "wechatIos":
                payChannelStr = "微信Ios";
                break;
            case "wechatAndroid":
                payChannelStr = "微信Android";
                break;
            case "alipay":
                payChannelStr = "支付宝";
                break;
            case "alipayApp":
                payChannelStr = "支付宝App";
                break;
        }
        return payChannelStr;
    }

    private String getOrderPayType(String payType) {
        String payTypeStr = "";
        switch (payType) {
            case "weixin":
                payTypeStr = "微信支付";
                break;
            case "alipay":
                payTypeStr = "支付宝支付";
                break;
            case "yue":
                payTypeStr = "余额支付";
                break;
            case "wallet":
                payTypeStr = "积分支付";
                break;
            case "lianlian":
                payTypeStr = "连连支付";
                break;
            case "confirmPay":
                payTypeStr = "人工确认";
                break;
        }
        return payTypeStr;
    }
}

