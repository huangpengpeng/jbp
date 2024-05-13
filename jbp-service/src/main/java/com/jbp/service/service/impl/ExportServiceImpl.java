package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.excel.OrderExcel;
import com.jbp.common.excel.OrderShipmentExcel;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.model.user.User;
import com.jbp.common.request.OrderSearchRequest;
import com.jbp.common.response.OrderInvoiceResponse;
import com.jbp.common.utils.*;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.ProductMaterialsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
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
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private OrderService orderService;

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
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private CapaService capaService;
    @Resource
    private OssService ossService;
    @Resource
    private OrderInvoiceService orderInvoiceService;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrderShipment(OrderSearchRequest request) {
        valid(request);

        List<Order> orderList = orderService.findExportList(request);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new CrmebException("未查询到订单数据");
        }
        log.info("订单导出订单数据查询完成...");

        // 订单用户
        List<Integer> userIdList = orderList.stream().filter(s -> s.getUid() != null).map(Order::getUid).distinct().collect(Collectors.toList());
        List<Integer> payUserIdList = orderList.stream().filter(s -> s.getUid() != null).map(Order::getPayUid).distinct().collect(Collectors.toList());
        userIdList.addAll(payUserIdList);
        userIdList = userIdList.stream().distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(userIdList);
        Map<Integer, TeamUser> teamMap = teamUserService.getUidMapList(userIdList);
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        log.info("订单导出用户数据查询完成...");

        // 订单详情
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
        Map<String, OrderExt> orderNoMapList = orderExtService.getOrderNoMapList(orderNoList);
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNoList);
        Map<String, MerchantOrder> merchantOrderMap = FunctionUtil.keyValueMap(merchantOrderList, MerchantOrder::getOrderNo);
        List<ProductMaterials> materialsList = productMaterialsService.list();
        Map<String, List<ProductMaterials>> materialsMap = FunctionUtil.valueMap(materialsList, ProductMaterials::getBarCode);

        log.info("订单导出详情数据查询完成...");
        LinkedList<OrderShipmentExcel> result = new LinkedList<>();
        // 导出对象
        for (Order order : orderList) {
            // 商户详情
            MerchantOrder merchantOrder = merchantOrderMap.get(order.getOrderNo());
            // 订单商品
            List<OrderDetail> orderDetailsList = orderDetailMap.get(order.getOrderNo());
            // 循环设置
            for (OrderDetail orderDetail : orderDetailsList) {
                // 获取物料信息
                BigDecimal payPrice = (orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()));
                List<ProductMaterials> productMaterials = materialsMap.get(orderDetail.getBarCode());
                if (CollectionUtils.isEmpty(productMaterials)) {
                    productMaterials = Lists.newArrayList();
                    ProductMaterials materials = new ProductMaterials(merchantOrder.getMerId(),
                            orderDetail.getBarCode(), orderDetail.getProductName(), 1,
                            payPrice.divide(BigDecimal.valueOf(orderDetail.getPayNum()), 4, BigDecimal.ROUND_DOWN), orderDetail.getBarCode(), request.getSupplyName());
                    productMaterials.add(materials);
                }
                // 物料总价
                BigDecimal materialsTotalPrice = BigDecimal.ZERO;
                for (ProductMaterials materials : productMaterials) {
                    materialsTotalPrice = materialsTotalPrice.add(materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity())));
                }

                // 物料信息
                for (ProductMaterials materials : productMaterials) {
                    // 组装订单
                    OrderShipmentExcel vo = new OrderShipmentExcel();
                    vo.setOrderDetailId(orderDetail.getId());
                    vo.setType(order.getOrderType());
                    vo.setPlatform(order.getPlatform());
                    vo.setPlatOrderNo(order.getOrderNo());
                    if (StringUtils.isNotEmpty(order.getPlatOrderNo())) {
                        vo.setPlatOrderNo(order.getPlatOrderNo());
                    }
                    vo.setUid(order.getUid());
                    if (order.getUid() != null) {
                        User user = userMap.get(order.getUid());
                        vo.setNickname(user != null ? user.getNickname() : "");
                        vo.setAccount(user != null ? user.getAccount() : "");
                        TeamUser teamUser = teamMap.get(order.getUid());
                        vo.setTeamName(teamUser != null ? teamUser.getName() : "");
                    }
                    if (order.getPayUid() != null) {
                        User user = userMap.get(order.getPayUid());
                        vo.setPayAccount(user != null ? user.getAccount() : "");
                    }
                    vo.setPayPrice(order.getPayPrice().subtract(order.getPayPostage()));
                    vo.setPayPostage(order.getPayPostage());
                    vo.setCouponPrice(order.getCouponPrice());
                    vo.setDeductionFee(order.getWalletDeductionFee());
                    vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
                    vo.setPayMethod(order.getPayMethod());
                    vo.setStatus(order.getOrderStatus());
                    vo.setRefundStatus(order.getOrderRefundStatus());
                    // 产品
                    vo.setProductName(orderDetail.getProductName());
                    vo.setProductBarCode(orderDetail.getBarCode());
                    vo.setProductQuantity(orderDetail.getPayNum());
                    vo.setProductPrice(payPrice);
                    vo.setProductPostage(orderDetail.getFreightFee());
                    vo.setProductCouponPrice(orderDetail.getCouponPrice());
                    vo.setProductDeductionFee(orderDetail.getWalletDeductionFee());
                    // 物料
                    vo.setMaterialsName(materials.getMaterialsName());
                    vo.setMaterialsCode(materials.getMaterialsCode());
                    vo.setMaterialsQuantity(orderDetail.getPayNum() * materials.getMaterialsQuantity());
                    BigDecimal price = materials.getMaterialsPrice().multiply(BigDecimal.valueOf(materials.getMaterialsQuantity()));
                    vo.setMaterialsPrice(BigDecimal.ZERO);
                    if (ArithmeticUtils.gt(materialsTotalPrice, BigDecimal.ZERO) && ArithmeticUtils.gt(payPrice, BigDecimal.ZERO)) {
                        BigDecimal materialsPrice = payPrice.multiply(price.divide(materialsTotalPrice, 10, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
                        vo.setMaterialsPrice(materialsPrice);
                    }
                    // 收货人
                    vo.setRealName(merchantOrder.getRealName());
                    vo.setUserPhone(merchantOrder.getUserPhone());
                    vo.setShippingType(1 == merchantOrder.getShippingType() ? "快递" : "自提");
                    vo.setProvince(merchantOrder.getProvince());
                    vo.setCity(merchantOrder.getCity());
                    vo.setDistrict(merchantOrder.getDistrict());
                    vo.setStreet(merchantOrder.getStreet());
                    vo.setAddress(merchantOrder.getAddress());
                    vo.setRemark(merchantOrder.getUserRemark());
                    vo.setMerchantRemark(merchantOrder.getMerchantRemark());
                    vo.setCreateTime(order.getCreateTime());
                    vo.setPayTime(order.getPayTime());
                    // 下单等级
                    OrderExt orderExt = orderNoMapList.get(merchantOrder.getOrderNo());
                    if (orderExt != null) {
                        if (ObjectUtil.isNotEmpty(orderExt.getCapaId())) {
                            Capa capa = capaMap.get(orderExt.getCapaId());
                            vo.setCapaName(capa != null ? capa.getName() : "");
                        }
                        //设置成功后等级
                        if (ObjectUtil.isNotEmpty(orderExt.getSuccessCapaId())) {
                            Capa capa = capaMap.get(orderExt.getCapaId());
                            vo.setSuccessCapaName(capa != null ? capa.getName() : "");
                        }
                    }
                    List<ProductDeduction> deductionList = orderDetail.getWalletDeductionList();
                    if (CollectionUtils.isNotEmpty(deductionList)) {
                        Map<Integer, ProductDeduction> deductionMap = FunctionUtil.keyValueMap(deductionList, ProductDeduction::getWalletType);
                        ProductDeduction gouwu = deductionMap.get(Integer.valueOf(1));
                        ProductDeduction jiangli = deductionMap.get(Integer.valueOf(2));
                        ProductDeduction huangou = deductionMap.get(Integer.valueOf(3));
                        ProductDeduction fuquan = deductionMap.get(Integer.valueOf(4));
                        vo.setGouwu(gouwu == null ? BigDecimal.ZERO : gouwu.getDeductionFee());
                        vo.setJiangli(jiangli == null ? BigDecimal.ZERO : jiangli.getDeductionFee());
                        vo.setHuangou(huangou == null ? BigDecimal.ZERO : huangou.getDeductionFee());
                        vo.setFuquan(fuquan == null ? BigDecimal.ZERO : fuquan.getDeductionFee());
                    }
                    // 保存
                    result.add(vo);
                }
            }
        }
        String s = ossService.uploadXlsx(result, OrderShipmentExcel.class, "订单物料" + DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        log.info("订单发货导出下载地址:" + s);
        return s;
    }

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrder(OrderSearchRequest request) {
        valid(request);
        List<Order> orderList = orderService.findExportList(request);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new CrmebException("未查询到订单数据");
        }
        log.info("订单导出订单数据查询完成...");

        // 订单用户
        List<Integer> userIdList = orderList.stream().filter(s -> s.getUid() != null).map(Order::getUid).distinct().collect(Collectors.toList());
        List<Integer> payUserIdList = orderList.stream().filter(s -> s.getUid() != null).map(Order::getPayUid).distinct().collect(Collectors.toList());
        userIdList.addAll(payUserIdList);
        userIdList = userIdList.stream().distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(userIdList);
        Map<Integer, TeamUser> teamMap = teamUserService.getUidMapList(userIdList);
        Map<Long, Capa> capaMap = capaService.getCapaMap();
        log.info("订单导出用户数据查询完成...");

        // 订单详情
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
        Map<String, OrderExt> orderNoMapList = orderExtService.getOrderNoMapList(orderNoList);
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNoList);
        Map<String, MerchantOrder> merchantOrderMap = FunctionUtil.keyValueMap(merchantOrderList, MerchantOrder::getOrderNo);

        log.info("订单导出详情数据查询完成...");
        LinkedList<OrderExcel> result = new LinkedList<>();
        // 导出对象
        for (Order order : orderList) {
            // 商户详情
            MerchantOrder merchantOrder = merchantOrderMap.get(order.getOrderNo());
            // 订单商品
            List<OrderDetail> orderDetailsList = orderDetailMap.get(order.getOrderNo());
            // 组装订单
            OrderExcel vo = new OrderExcel();
            vo.setId(order.getId());
            vo.setType(order.getOrderType());
            vo.setPlatform(order.getPlatform());
            vo.setPlatOrderNo(order.getOrderNo());
            if (StringUtils.isNotEmpty(order.getPlatOrderNo())) {
                vo.setPlatOrderNo(order.getPlatOrderNo());
            }
            vo.setUid(order.getUid());
            if (order.getUid() != null) {
                User user = userMap.get(order.getUid());
                vo.setNickname(user != null ? user.getNickname() : "");
                vo.setAccount(user != null ? user.getAccount() : "");
                TeamUser teamUser = teamMap.get(order.getUid());
                vo.setTeamName(teamUser != null ? teamUser.getName() : "");
            }
            if (order.getPayUid() != null) {
                User user = userMap.get(order.getPayUid());
                vo.setPayAccount(user != null ? user.getAccount() : "");
            }
            vo.setPayPrice(order.getPayPrice().subtract(order.getPayPostage()));
            vo.setPayPostage(order.getPayPostage());
            vo.setCouponPrice(order.getCouponPrice());
            vo.setDeductionFee(order.getWalletDeductionFee());
            vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
            vo.setPayMethod(order.getPayMethod());
            vo.setStatus(order.getOrderStatus());
            vo.setRefundStatus(order.getOrderRefundStatus());
            // 产品
            vo.setProductInfo(getOrderProductInfo(orderDetailsList));
            // 收货人
            vo.setRealName(merchantOrder.getRealName());
            vo.setUserPhone(merchantOrder.getUserPhone());
            vo.setShippingType(1 == merchantOrder.getShippingType() ? "快递" : "自提");
            vo.setProvince(merchantOrder.getProvince());
            vo.setCity(merchantOrder.getCity());
            vo.setDistrict(merchantOrder.getDistrict());
            vo.setStreet(merchantOrder.getStreet());
            vo.setAddress(merchantOrder.getAddress());
            vo.setRemark(merchantOrder.getUserRemark());
            vo.setMerchantRemark(merchantOrder.getMerchantRemark());
            vo.setCreateTime(order.getCreateTime());

            vo.setPayTime(order.getPayTime());
            List<OrderInvoiceResponse> shopList=   orderInvoiceService.findByOrderNo(order.getOrderNo());
            vo.setShipTime(shopList.isEmpty() ? null : shopList.get(0).getCreateTime());
            // 下单等级
            OrderExt orderExt = orderNoMapList.get(merchantOrder.getOrderNo());
            if (orderExt != null) {
                if (ObjectUtil.isNotEmpty(orderExt.getCapaId())) {
                    Capa capa = capaMap.get(orderExt.getCapaId());
                    vo.setCapaName(capa != null ? capa.getName() : "");
                }
                //设置成功后等级
                if (ObjectUtil.isNotEmpty(orderExt.getSuccessCapaId())) {
                    Capa capa = capaMap.get(orderExt.getCapaId());
                    vo.setSuccessCapaName(capa != null ? capa.getName() : "");
                }
            }
            vo.setIfUserVerifyReceive("否");
            if (order.getIfUserVerifyReceive() != null && order.getIfUserVerifyReceive()) {
                vo.setIfUserVerifyReceive("是");
            }
            vo.setReceiveTime(order.getReceivingTime());
            if ("已取消".equals(vo.getStatus())) {
                vo.setCancelTime(order.getUpdateTime());
            }
            List<ProductDeduction> deductionList = order.getWalletDeductionList();
            if (CollectionUtils.isNotEmpty(deductionList)) {
                Map<Integer, ProductDeduction> deductionMap = FunctionUtil.keyValueMap(deductionList, ProductDeduction::getWalletType);
                ProductDeduction gouwu = deductionMap.get(Integer.valueOf(1));
                ProductDeduction jiangli = deductionMap.get(Integer.valueOf(2));
                ProductDeduction huangou = deductionMap.get(Integer.valueOf(3));
                ProductDeduction fuquan = deductionMap.get(Integer.valueOf(4));
                vo.setGouwu(gouwu == null ? BigDecimal.ZERO : gouwu.getDeductionFee());
                vo.setJiangli(jiangli == null ? BigDecimal.ZERO : jiangli.getDeductionFee());
                vo.setHuangou(huangou == null ? BigDecimal.ZERO : huangou.getDeductionFee());
                vo.setFuquan(fuquan == null ? BigDecimal.ZERO : fuquan.getDeductionFee());
            }
            // 保存
            result.add(vo);
        }
        String s = ossService.uploadXlsx(result, OrderExcel.class, "订单列表" + DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        log.info("订单列表导出下载地址:" + s);
        return s;
    }

    private static void valid(OrderSearchRequest request) {
        if (StringUtils.isEmpty(request.getOrderNo()) && StringUtils.isEmpty(request.getPlatOrderNo()) && StringUtils.isEmpty(request.getUaccount()) && StringUtils.isEmpty(request.getPayAccount())) {
            if (StringUtils.isEmpty(request.getDateLimit()) && StringUtils.isEmpty(request.getPayTime())) {
                throw new CrmebException("导出没指定【单号 下单账户  付款账户 】条件, 数据【创建时间】或者【付款时间】为必填二选一，且时间跨度不超出3个月");
            }
            if (StringUtils.isNotEmpty(request.getDateLimit())) {
                DateLimitUtilVo timeVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
                if (DateTimeUtils.addDays(DateTimeUtils.parseDate(timeVo.getStartTime()), 3).before(DateTimeUtils.parseDate(timeVo.getEndTime()))) {
                    throw new CrmebException("导出没指定【单号 下单账户  付款账户 】条件, 数据【创建时间】或者【付款时间】为必填二选一，且时间跨度不超出3个月");
                }
            }
            if (StringUtils.isNotEmpty(request.getPayTime())) {
                DateLimitUtilVo timeVo = CrmebDateUtil.getDateLimit(request.getPayTime());
                if (DateTimeUtils.addDays(DateTimeUtils.parseDate(timeVo.getStartTime()), 3).before(DateTimeUtils.parseDate(timeVo.getEndTime()))) {
                    throw new CrmebException("导出没指定【单号 下单账户  付款账户 】条件, 数据【创建时间】或者【付款时间】为必填二选一，且时间跨度不超出3个月");
                }
            }
        }
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
            case "wallet":
                payChannelStr = "积分支付";
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

