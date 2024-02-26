package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductAttrValue;
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
    @Autowired
    private CrmebConfig crmebConfig;
    @Resource
    private ProductService productService;
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

        // 物流对象
        Map<String, List<ProductMaterials>> materialsMap = Maps.newConcurrentMap();

        // 导出对象
        List<OrderExcelVo> voList = CollUtil.newArrayList();
        for (Order order : orderList) {
            // 商品详情
            List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
            // 商户详情
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
            // 用户详情
            User user = userMap.get(order.getId());
            // 订单商品
            List<OrderDetail> orderDetailsList = orderDetailMap.get(order.getOrderNo());
            // 循环设置
            for (OrderDetail orderDetail : orderDetailsList) {
                BigDecimal payPrice = (orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                Integer attrValueId = orderDetail.getAttrValueId();
                ProductAttrValue attr = productAttrValueService.getById(attrValueId);
                List<ProductMaterials> materialsList = materialsMap.get(attr.getBarCode());
                if(CollectionUtils.isEmpty(materialsList)){
                    materialsList = productMaterialsService.getByBarCode(attr.getBarCode());
                    materialsMap.put(attr.getBarCode(), materialsList);
                }
                // 物料总价
                BigDecimal materialsTotalPrice = BigDecimal.ZERO;
                for (ProductMaterials materials : materialsList) {
                    materialsTotalPrice = materialsTotalPrice.add(materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity())));
                }
                // 物料信息
                for (ProductMaterials materials : materialsList) {
                    BigDecimal price = materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity()));
                    BigDecimal realPrice = payPrice.multiply(price.divide(materialsTotalPrice, 10, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
                    // 组装
                    OrderExcelVo vo = new OrderExcelVo();
                    vo.setType(getOrderType(order.getType()));
                    vo.setOrderNo(order.getOrderNo());
                    vo.setMerName(order.getMerId() > 0 ? merchantMap.get(order.getMerId()).getName() : "平台商");
                    vo.setUid(order.getUid());
                    vo.setUserAccount(userMap.get(order.getUid()).getAccount());
                    vo.setPayUserAccount(userMap.get(order.getPayUid()).getAccount());
                    vo.setPayPrice(order.getPayPrice().subtract(order.getPayPostage()).toPlainString());
                    vo.setPayPostage(order.getPayPostage().toPlainString());




                }
            }


            for (int i = 0; i < orderDetails.size(); i++) {
                // 订单详情表
                OrderDetail orderDetail = orderDetails.get(i);
                BigDecimal payPrice = (orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                ProductAttrValue productAttrValue = productAttrValueService.getById(orderDetail.getAttrValueId());
                // 产品物料对应仓库的编码
                List<ProductMaterials> productMaterialsList = productMaterialsService.getByBarCode(productAttrValue != null ? productAttrValue.getBarCode() : null);
                BigDecimal totalPrice = BigDecimal.ZERO;
                for (ProductMaterials productMaterials : productMaterialsList) {
                    totalPrice = totalPrice.add(productMaterials.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterials.getMaterialsQuantity())));
                }
                for (ProductMaterials productMaterials : productMaterialsList) {
                    BigDecimal price = productMaterials.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterials.getMaterialsQuantity()));
                    BigDecimal realPrice = payPrice.multiply(price.divide(totalPrice, 10, BigDecimal.ROUND_DOWN));
                    OrderExcelVo vo = new OrderExcelVo();
                    vo.setType(getOrderType(order.getType()));
                    vo.setOrderNo(order.getOrderNo());
                    vo.setMerName(order.getMerId() > 0 ? merchantMap.get(order.getMerId()).getName() : "");

                    vo.setUserAccount(userMap.get(order.getUid()).getAccount());
                    vo.setUid(order.getUid());
                    vo.setPayPrice(order.getPayPrice().toString());
                    vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
//                    vo.setPayType(getOrderPayType(order.getPayType()));
                    vo.setPayChannel(getOrderPayChannel(order.getPayChannel()));
                    vo.setStatus(getOrderStatus(order.getStatus()));
                    vo.setRefundStatus(getOrderRefundStatus(order.getRefundStatus()));
                    vo.setCreateTime(CrmebDateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    vo.setProductName(productMaterials.getMaterialsName());
                    vo.setWarehouseCoding(productMaterials.getMaterialsCode());
//                    vo.setProductPrice(realPrice);
                    vo.setRealName(merchantOrder.getRealName());
                    vo.setMaterialsQuantity(productMaterialsList.size() * productMaterials.getMaterialsQuantity());
                    vo.setUserAddress(merchantOrder.getUserAddress());
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
        aliasMap.put("uid", "用户id");
        aliasMap.put("userAccount", "用户账号");
        aliasMap.put("payPrice", "实际支付金额");
        aliasMap.put("paidStr", "支付状态");
        aliasMap.put("payType", "支付方式");
        aliasMap.put("payChannel", "支付渠道");
        aliasMap.put("status", "订单状态");
        aliasMap.put("refundStatus", "退款状态");
        aliasMap.put("createTime", "创建时间");
        aliasMap.put("productName", "商品名称");
        aliasMap.put("warehouseCoding", "仓库编码");
        aliasMap.put("materialsQuantity", "数量");
        aliasMap.put("productPrice", "商品单价");
        aliasMap.put("realName", "收货人");
        aliasMap.put("userAddress", "收货详情地址");


        return ExportUtil.exportExcel(fileName, "订单导出", voList, aliasMap);
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
        }
        return payTypeStr;
    }
}

