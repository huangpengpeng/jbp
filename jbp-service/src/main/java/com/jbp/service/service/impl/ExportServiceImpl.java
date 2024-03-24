package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.Materials;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.OrderExcelInfoVo;
import com.jbp.common.vo.OrderExcelVo;
import com.jbp.service.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private WalletConfigService walletConfigService;
    @Resource
    private MerchantOrderService merchantOrderService;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public OrderExcelInfoVo exportOrder(OrderSearchRequest request) {
        if (StringUtils.isEmpty(request.getOrderNo()) && StringUtils.isEmpty(request.getPlatOrderNo()) && StringUtils.isEmpty(request.getDateLimit()) && StringUtils.isEmpty(request.getStatus()) && ObjectUtils.isEmpty(request.getType())) {
            throw new CrmebException("请选择一个查询条件");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }

        Integer id = 0;
        List<OrderExcelVo> voList = CollUtil.newArrayList();
        Map<String, String> walletDeductionMap = Maps.newConcurrentMap();
        do {
            List<Order> orderList = orderService.findExportList(request, id);
            if (CollectionUtils.isEmpty(orderList)) {
                break;
            }
            // 商户 用户 订单 数据准备
            List<Integer> merIdList = orderList.stream().filter(e -> e.getMerId() > 0).map(Order::getMerId).distinct().collect(Collectors.toList());
            List<Integer> userIdList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
            List<Integer> payUserIdList = orderList.stream().map(Order::getPayUid).distinct().collect(Collectors.toList());
            userIdList.addAll(payUserIdList);
            userIdList = userIdList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());

            List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
            Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
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
                    BigDecimal payPrice = (orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                    List<Materials> materialsList = orderDetail.getMaterialsList();
                    // 没有设置物料 默认原始商品信息即可
                    if (CollectionUtils.isEmpty(materialsList)) {
                        materialsList = Lists.newArrayList();
                        Materials materials = new Materials(orderDetail.getProductName(), 1, payPrice, orderDetail.getBarCode(), payPrice);
                        materialsList.add(materials);
                    }
                    // 物料总价
                    BigDecimal materialsTotalPrice = BigDecimal.ZERO;
                    for (Materials materials : materialsList) {
                        materialsTotalPrice = materialsTotalPrice.add(materials.getPrice().multiply(BigDecimal.valueOf(materials.getQuantity())));
                    }
                    // 物料信息
                    for (Materials materials : materialsList) {
                        // 组装订单
                        OrderExcelVo vo = new OrderExcelVo();
                        vo.setType(order.getOrderType());
                        vo.setOrderNo(order.getOrderNo());
                        vo.setMerName(order.getMerId() > 0 ? merchantMap.get(order.getMerId()).getName() : "平台商");
                        if (order.getUid() != null) {
                            vo.setUid(order.getUid());
                            vo.setUserAccount(userMap.get(order.getUid()).getAccount());
                        }
                        vo.setPayUserAccount(userMap.get(order.getPayUid()).getAccount());
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
                        BigDecimal price = materials.getPrice().multiply(BigDecimal.valueOf(materials.getQuantity()));
                        BigDecimal materialsPrice = payPrice.multiply(price.divide(materialsTotalPrice, 10, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
                        vo.setMaterialsName(materials.getName());
                        vo.setMaterialsCode(materials.getCode());
                        vo.setMaterialsQuantity(orderDetail.getPayNum() * materials.getQuantity());
                        vo.setMaterialsPrice(materialsPrice);

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

        OrderExcelInfoVo vo = new OrderExcelInfoVo();
        LinkedHashMap<String, String> head = new LinkedHashMap<String, String>();
        head.put("orderDetailId", "订单详情ID");
        head.put("type", "订单类型");
        head.put("orderNo", "订单号");
        head.put("merName", "商户名称");
        head.put("uid", "用户ID");
        head.put("userAccount", "下单账号");
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
        head.forEach((k,v)->{
            JSONObject json = new JSONObject();
            json.put("k", k);
            json.put("v", v);
            array.add(json);
        });
        vo.setHead(array);
        vo.setList(voList);
        return vo;
    }
}

