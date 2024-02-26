package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.Materials;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.utils.*;
import com.jbp.common.vo.OrderExcelVo;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.ProductMaterialsService;
import org.apache.commons.collections4.CollectionUtils;
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
    private CrmebConfig crmebConfig;
    @Resource
    private ProductAttrValueService productAttrValueService;
    @Resource
    private ProductMaterialsService productMaterialsService;
    @Resource
    private MerchantOrderService merchantOrderService;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrder(OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }
        List<Order> orderList = orderService.findExportList(request);
        if (CollUtil.isEmpty(orderList)) {
            throw new CrmebException("没有可导出的数据！");
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
        List<OrderExcelVo> voList = CollUtil.newArrayList();
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
                    vo.setUid(order.getUid());
                    vo.setUserAccount(userMap.get(order.getUid()).getAccount());
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
                    List<ProductDeduction> walletDeductionList = orderDetail.getWalletDeductionList();
                    if (CollectionUtils.isNotEmpty(walletDeductionList)) {
                        walletDeductionList = walletDeductionList.stream().filter(w -> ArithmeticUtils.gt(w.getDeductionFee(), BigDecimal.ZERO)).collect(Collectors.toList());
                        StringBuilder walletDeductionStr = new StringBuilder();
                        for (ProductDeduction deduction : walletDeductionList) {
                            String format = String.format("积分名称:%s,抵扣金额:%s", deduction.getWalletName(), deduction.getDeductionFee());
                            walletDeductionStr.append(format);
                        }
                        vo.setProductWalletDeductionFeeStr(walletDeductionStr.toString());
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

        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        UploadUtil.setHzwServerPath((crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/"));

        // 文件名
        String fileName = "订单导出_".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("type", "订单类型");
        aliasMap.put("orderNo", "订单号");
        aliasMap.put("merName", "商户名称");
        aliasMap.put("uid", "用户ID");
        aliasMap.put("userAccount", "下单账号");
        aliasMap.put("payUserAccount", "付款账号");
        aliasMap.put("payPrice", "订单货款");
        aliasMap.put("payPostage", "订单运费");
        aliasMap.put("couponPrice", "订单优惠");
        aliasMap.put("walletDeductionFee", "订单抵扣");
        aliasMap.put("paidStr", "支付状态");
        aliasMap.put("status", "订单状态");
        aliasMap.put("createTime", "下单时间");
        aliasMap.put("refundStatus", "退款状态");
        aliasMap.put("orderPayType", "支付方式");
        aliasMap.put("payMethod", "支付方法");
        aliasMap.put("payChannel", "支付渠道");
        aliasMap.put("orderDetailId", "订单详情ID");
        aliasMap.put("productName", "商品名称");
        aliasMap.put("productBarCode", "商品编码");
        aliasMap.put("productQuantity", "商品数量");
        aliasMap.put("productPrice", "商品总价");
        aliasMap.put("productPostage", "商品运费");
        aliasMap.put("productCouponPrice", "商品优惠");
        aliasMap.put("productWalletDeductionFee", "商品抵扣");
        aliasMap.put("productWalletDeductionFeeStr", "商品抵扣明细");
        aliasMap.put("materialsName", "物料名称");
        aliasMap.put("materialsCode", "物料编码");
        aliasMap.put("materialsQuantity", "物料数量");
        aliasMap.put("materialsPrice", "物料总价");
        aliasMap.put("realName", "收货人");
        aliasMap.put("userPhone", "收货人手机");
        aliasMap.put("shippingType", "快递方式");
        aliasMap.put("userAddress", "收货地址");
        aliasMap.put("userRemark", "用户备注");
        aliasMap.put("merchantRemark", "商家备注");
        return ExportUtil.exportExcel(fileName, "订单导出", voList, aliasMap);
    }

}

