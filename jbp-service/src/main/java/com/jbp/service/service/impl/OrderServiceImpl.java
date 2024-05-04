package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.express.Express;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.*;
import com.jbp.common.model.system.SystemNotification;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserToken;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.utils.*;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.LogisticsResultVo;
import com.jbp.common.vo.MyRecord;
import com.jbp.common.vo.ProductMaterialsVo;
import com.jbp.service.dao.OrderDao;
import com.jbp.service.dao.OrderInvoiceDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.ProductMaterialsService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Resource
    private OrderDao dao;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private LogisticService logisticService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderInvoiceService orderInvoiceService;
    @Autowired
    private OrderInvoiceDetailService orderInvoiceDetailService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private TemplateMessageService templateMessageService;
    @Autowired
    private SystemNotificationServiceImpl systemNotificationService;
    @Autowired
    private MerchantPrintService merchantPrintService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private CapaService capaService;
    @Autowired
    private ProductMaterialsService productMaterialsService;
    @Autowired
    private OrderInvoiceDao orderInvoiceDao;

    @Override
    public String getOrderNo(String orderNo) {
        List<Order> list = getByPlatOrderNo(orderNo);
        if (CollUtil.isEmpty(list)) {
            return orderNo;
        }
        return list.get(0).getOrderNo();
    }

    @Override
    public String getPlatOrderNo(String orderNo) {
        Order order = getByOrderNo(orderNo);
        return StringUtils.isEmpty(order.getPlatOrderNo()) ? orderNo : order.getPlatOrderNo();
    }

    /**
     * 根据订单编号获取订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException("订单不存在");
        }
        return order;
    }

    @Override
    public Boolean updatePaid(String orderNo) {
        LambdaUpdateWrapper<Order> lqw = new LambdaUpdateWrapper<>();
        lqw.set(Order::getPaid, true);
        lqw.set(Order::getPayTime, DateUtil.date());
        lqw.set(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.eq(Order::getPaid, false);
        return update(lqw);
    }

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public Order getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException("订单不存在");
        }
        return order;
    }

    /**
     * 获取用户订单列表
     *
     * @param userId      用户id
     * @param status      订单状态（-1：全部，0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<Order> getUserOrderList(Integer userId, Integer status, PageParamRequest pageRequest) {
        Page<Order> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUid, userId);
        if (status >= 0) {
            if (status == 1) {
                lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
            } else {
                lqw.eq(Order::getStatus, status);
            }
        }
        lqw.lt(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
        lqw.eq(Order::getIsUserDel, false);
        lqw.eq(Order::getIsMerchantDel, false);
        lqw.in(Order::getType, OrderConstants.ORDER_TYPE_NORMAL, OrderConstants.ORDER_TYPE_SECKILL);
        lqw.eq(Order::getIsDel, false);
        lqw.orderByDesc(Order::getId);
        List<Order> orderList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, orderList);
    }

    /**
     * 获取用户订单列表V1.4
     *
     * @param userId  用户id
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<Order> getUserOrderList_v1_4(Integer userId, OrderFrontListRequest request) {
        Page<Order> page = PageHelper.startPage(request.getPage(), request.getLimit());
        if (StrUtil.isBlank(request.getKeywords())) {
            LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
            //代购订单
            if(request.getAgent() != null && request.getAgent()){
                lqw.eq(Order::getPayUid, userId);
                lqw.ne(Order::getUid, userId);
            } else {
                lqw.eq(Order::getUid, userId);
            }
            if (request.getStatus() >= 0) {
                if (request.getStatus() == 1) {
                    lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
                } else {
                    lqw.eq(Order::getStatus, request.getStatus());
                }
            }
            lqw.lt(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
            lqw.eq(Order::getIsUserDel, false);
            lqw.eq(Order::getIsMerchantDel, false);
            lqw.in(Order::getType, OrderConstants.ORDER_TYPE_NORMAL, OrderConstants.ORDER_TYPE_SECKILL);
            lqw.eq(Order::getIsDel, false);
            lqw.orderByDesc(Order::getId);
            List<Order> orderList = dao.selectList(lqw);
            return CommonPage.copyPageInfo(page, orderList);
        }
        Map<String, Object> searchMap = new HashMap<>();
        String keywords = URLUtil.decode(request.getKeywords());
        searchMap.put("keywords", keywords);
        searchMap.put("status", request.getStatus());
        searchMap.put("userId", userId);
        List<Order> orderList = dao.findFrontList(searchMap);
        return CommonPage.copyPageInfo(page, orderList);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @param isUser  是否用户取消
     * @return Boolean
     */
    @Override
    public Boolean cancel(String orderNo, Boolean isUser) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getCancelStatus, isUser ? OrderConstants.ORDER_CANCEL_STATUS_USER : OrderConstants.ORDER_CANCEL_STATUS_SYSTEM);
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_CANCEL);
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getPaid, false);
        wrapper.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
        return update(wrapper);
    }

    /**
     * 商户端后台分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantOrderPageResponse> getMerchantAdminPage(OrderSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        Page<Order> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getOrderNo, Order::getUid, Order::getPayPrice, Order::getPayType, Order::getPaid, Order::getStatus, Order::getRefundStatus,
                Order::getIsUserDel, Order::getCancelStatus, Order::getType, Order::getCreateTime, Order::getLevel);
        lqw.eq(Order::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.like(Order::getOrderNo, URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(Order::getType, request.getType());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            getRequestTimeWhere(lqw, request.getDateLimit());
        }
        getMerchantStatusWhere(lqw, request.getStatus());
        lqw.eq(Order::getIsMerchantDel, false);
        lqw.eq(Order::getLevel, OrderConstants.ORDER_LEVEL_MERCHANT);
        lqw.orderByDesc(Order::getId);
        List<Order> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        List<Integer> uidList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        List<MerchantOrderPageResponse> pageResponses = orderList.stream().map(e -> {
            MerchantOrderPageResponse pageResponse = new MerchantOrderPageResponse();
            BeanUtils.copyProperties(e, pageResponse);
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(e.getOrderNo());
            pageResponse.setShippingType(merchantOrder.getShippingType());
            pageResponse.setUserRemark(merchantOrder.getUserRemark());
            pageResponse.setMerRemark(merchantOrder.getMerchantRemark());
            pageResponse.setNickName(userMap.get(e.getUid()).getNickname());
            pageResponse.setIsLogoff(userMap.get(e.getUid()).getIsLogoff());
            return pageResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(startPage, pageResponses);
    }

    /**
     * 获取商户端订单各状态数量
     *
     * @param dateLimit 时间参数
     */
    @Override
    public OrderCountItemResponse getMerchantOrderStatusNum(String dateLimit, String supplyName) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        OrderCountItemResponse response = new OrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_ALL, systemAdmin.getMerId(), supplyName));
        // 未支付订单
        response.setUnPaid(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_UNPAID, systemAdmin.getMerId(), supplyName));
        // 未发货订单
        response.setNotShipped(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED, systemAdmin.getMerId(), supplyName));
        // 待收货订单
        response.setSpike(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_SPIKE, systemAdmin.getMerId(), supplyName));
        // 已收货订单
        response.setReceiving(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING, systemAdmin.getMerId(), supplyName));
        // 交易完成订单
        response.setComplete(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE, systemAdmin.getMerId(), supplyName));
        // 已退款订单
        response.setRefunded(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED, systemAdmin.getMerId(), supplyName));
        // 已删除订单
        response.setDeleted(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_DELETED, systemAdmin.getMerId(), supplyName));
        // 待核销订单
        response.setVerification(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION, systemAdmin.getMerId(), supplyName));
        return response;
    }

    /**
     * 订单详情（PC）
     *
     * @param orderNo 订单编号
     * @return OrderAdminDetailResponse
     */
    @Override
    public OrderAdminDetailResponse adminDetail(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Order order = getByOrderNo(orderNo);
        if (order.getIsMerchantDel() || !order.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("未找到对应订单信息");
        }
        OrderAdminDetailResponse orderAdminDetailResponse = new OrderAdminDetailResponse();
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        BeanUtils.copyProperties(merchantOrder, orderAdminDetailResponse);
        BeanUtils.copyProperties(order, orderAdminDetailResponse);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        List<OrderInfoFrontDataResponse> orderInfoList = orderDetailList.stream().map(e -> {
            OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(e, dataResponse);
            return dataResponse;
        }).collect(Collectors.toList());
        orderAdminDetailResponse.setOrderDetailList(orderInfoList);

        // 用户信息
        User user = userService.getById(order.getUid());
        orderAdminDetailResponse.setNikeName(user.getNickname());
        orderAdminDetailResponse.setPhone(user.getPhone());
        orderAdminDetailResponse.setIsLogoff(user.getIsLogoff());
        if (merchantOrder.getClerkId() > 0) {
            SystemAdmin clerkAdmin = systemAdminService.getById(merchantOrder.getClerkId());
            orderAdminDetailResponse.setClerkName(clerkAdmin.getRealName());
        }
        return orderAdminDetailResponse;
    }

    /**
     * 发货
     *
     * @param request 发货参数
     * @return Boolean
     */
    @Override
    public Boolean send(OrderSendRequest request) {
        if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
            if (ObjectUtil.isNull(request.getExpressRecordType())) {
                throw new CrmebException("请选择发货记录类型");
            }
            validateExpressSend(request);
        }
        Order order = getByOrderNo(request.getOrderNo());
        if (order.getIsUserDel() || order.getIsMerchantDel()) {
            throw new CrmebException("订单已删除");
        }
        if (!order.getLevel().equals(OrderConstants.ORDER_LEVEL_MERCHANT)) {
            throw new CrmebException("订单等级异常，不是商户订单");
        }
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_SHIPPING) || order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING))) {
            throw new CrmebException("订单不处于待发货状态");
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException("订单已退款无法发货");
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(request.getOrderNo());
        if (!merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
            throw new CrmebException("订单非发货类型订单");
        }
        if (!request.getIsSplit()) {
            if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_FICTITIOUS)) {
                return virtual(order.getOrderNo());
            }
            if (request.getDeliveryType().equals(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS)) {
                return sendExpress(request, order, merchantOrder);
            }
        }
        if (CollUtil.isEmpty(request.getDetailList())) {
            throw new CrmebException("拆单发货详情不能为空");
        }
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<Integer> detailIdList = detailRequestList.stream().map(SplitOrderSendDetailRequest::getOrderDetailId).distinct().collect(Collectors.toList());
        if (detailRequestList.size() != detailIdList.size()) {
            throw new CrmebException("有重复的发货单详情");
        }
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(request.getOrderNo());
        detailRequestList.forEach(detailRequest -> {
            if (detailRequest.getNum() < 1) {
                throw new CrmebException("订单详情发货数量不能小于1");
            }
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detailRequest.getOrderDetailId())).findAny().orElse(null);
            if (ObjectUtil.isNull(orderDetail)) {
                throw new CrmebException("订单详情ID不对应");
            }
            if (orderDetail.getPayNum() - orderDetail.getDeliveryNum() - detailRequest.getNum() < 0) {
                throw new CrmebException("超出可发货数量,请重新选择数量");
            }
        });

        // 拆单发货
        return splitSendExpress(request, order, merchantOrder, orderDetailList);
    }

    /**
     * 批量发放
     *
     * @param sendRequestList
     * @return
     */
    @Override
    public Boolean batchSend(List<OrderSendRequest> sendRequestList) {
        for (OrderSendRequest vo : sendRequestList) {
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(vo.getOrderNo());

            Order order = getByOrderNo(vo.getOrderNo());
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(vo.getOrderNo());
            if (order.getIsUserDel() || order.getIsMerchantDel()) {
                throw new CrmebException("订单已删除");
            }
            if (!order.getLevel().equals(OrderConstants.ORDER_LEVEL_MERCHANT)) {
                throw new CrmebException("订单等级异常，不是商户订单");
            }
            if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_SHIPPING) || order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING))) {
                throw new CrmebException("订单不处于待发货状态");
            }
            if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
                throw new CrmebException("订单已退款无法发货");
            }
            if (!merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
                throw new CrmebException("订单非发货类型订单");
            }
            if (CollUtil.isEmpty(vo.getDetailList())) {
                throw new CrmebException("拆单发货详情不能为空");
            }

            List<SplitOrderSendDetailRequest> detailRequestList = vo.getDetailList();
            detailRequestList.forEach(detailRequest -> {
                if (detailRequest.getNum() < 1) {
                    throw new CrmebException("订单详情发货数量不能小于1");
                }
                OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detailRequest.getOrderDetailId())).findAny().orElse(null);
                if (ObjectUtil.isNull(orderDetail)) {
                    throw new CrmebException("订单详情ID不对应");
                }
                if (orderDetail.getPayNum() - orderDetail.getDeliveryNum() - detailRequest.getNum() < 0) {
                    throw new CrmebException("超出可发货数量,请重新选择数量");
                }
            });
            splitSendExpress(vo, order, merchantOrder, orderDetailList);
        }
        return true;
    }

    /**
     * 小票打印
     *
     * @param orderNo 订单编号
     * @return 打印结果
     */
    @Override
    public void printReceipt(String orderNo) {
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(merchantOrder.getMerId())) {
            throw new CrmebException("订单不存在");
        }
        List<MerchantOrder> orders = new ArrayList<>();
        orders.add(merchantOrder);
        merchantPrintService.printReceipt(orders, 1);
        ;
    }

    /**
     * 商户删除订单
     *
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean merchantDeleteByOrderNo(String orderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Order order = getByOrderNoAndMerId(orderNo, systemAdmin.getMerId());
        if (!order.getIsUserDel()) {
            throw new CrmebException("用户未删除订单时，无法删除");
        }
        order.setIsMerchantDel(true);
        return updateById(order);
    }

    /**
     * 商户备注订单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean merchantMark(OrderRemarkRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Order order = getByOrderNoAndMerId(request.getOrderNo(), systemAdmin.getMerId());
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
        merchantOrder.setMerchantRemark(request.getRemark());
        return merchantOrderService.updateById(merchantOrder);
    }

    /**
     * 订单收货
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        wrapper.set(Order::getReceivingTime, DateUtil.date());
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        return update(wrapper);
    }

    /**
     * 平台端后台分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformOrderPageResponse> getPlatformAdminPage(OrderSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Order> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getMerId,Order::getPayTime, Order::getOrderNo, Order::getPlatOrderNo, Order::getPlatform, Order::getUid, Order::getPayUid, Order::getPayPrice, Order::getPayType, Order::getPaid, Order::getStatus,
                Order::getRefundStatus, Order::getIsUserDel, Order::getIsMerchantDel, Order::getCancelStatus, Order::getLevel, Order::getType, Order::getCreateTime);
        if(StringUtils.isNotEmpty(request.getSupplyName())){
            List<String> orderNoList = orderDetailService.getOrderNoList4SupplyName(request.getSupplyName());
            if(!CollectionUtils.isEmpty(orderNoList)){
                lqw.eq(Order::getOrderNo, orderNoList);
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            lqw.eq(Order::getMerId, request.getMerId());
        }
        if (StrUtil.isNotBlank(request.getPayTime())) {
            getPayTimeWhere(lqw,request.getPayTime());
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.and((wrapper) -> {
                wrapper.eq(Order::getOrderNo, request.getOrderNo())
                        .or().eq(Order::getPlatOrderNo, request.getOrderNo());
            });
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(Order::getType, request.getType());
        }
        if (ObjectUtil.isNotEmpty(request.getUid())) {
            lqw.eq(Order::getUid, request.getUid());
        }
        if (ObjectUtil.isNotEmpty(request.getPayUid())) {
            lqw.eq(Order::getPayUid, request.getPayUid());
        }
        if (!CollectionUtils.isEmpty(request.getUidList())) {
            lqw.in(Order::getUid, request.getUidList());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            getRequestTimeWhere(lqw, request.getDateLimit());
        }
        getMerchantStatusWhere(lqw, request.getStatus());
        lqw.orderByDesc(Order::getId);
        List<Order> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        List<Integer> uidList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
        List<Integer> payUidList = orderList.stream().map(Order::getPayUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        Map<Integer, User> payUserMap = userService.getUidMapList(payUidList);
        List<Integer> merIdList = orderList.stream().map(Order::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = Maps.newConcurrentMap();
        if (CollectionUtils.isEmpty(merIdList)) {
            merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        }
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).collect(Collectors.toList());
        Map<String, OrderExt> orderNoMapList = orderExtService.getOrderNoMapList(orderNoList);
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNoList);
        Map<String, List<MerchantOrder>> merchantOrderMap = FunctionUtil.valueMap(merchantOrderList, MerchantOrder::getOrderNo);

        Map<Integer, Merchant> finalMerchantMap = merchantMap;
        List<PlatformOrderPageResponse> pageResponses = orderList.stream().map(e -> {
            PlatformOrderPageResponse pageResponse = new PlatformOrderPageResponse();
            BeanUtils.copyProperties(e, pageResponse);
            MerchantOrder merchantOrder = merchantOrderMap.get(e.getOrderNo()).get(0);
            pageResponse.setShippingType(merchantOrder.getShippingType());
            pageResponse.setUserRemark(merchantOrder.getUserRemark());
            pageResponse.setMerRemark(merchantOrder.getMerchantRemark());
            if(StringUtils.isNotEmpty(pageResponse.getPlatOrderNo())){
                pageResponse.setOrderNo(pageResponse.getPlatOrderNo());
            }
            User user = userMap.get(e.getUid());
            if (user != null) {
                pageResponse.setUid(user.getId());
                pageResponse.setUAccount(user.getAccount());
                pageResponse.setNickName(user.getNickname());
                pageResponse.setIsLogoff(user.getIsLogoff());
                pageResponse.setUPhone(user.getPhone());
            }
            User payUser = payUserMap.get(e.getPayUid());
            if (payUser != null) {
                pageResponse.setPayUid(payUser.getId());
                pageResponse.setPayAccount(payUser.getAccount());
                pageResponse.setPayNickName(payUser.getNickname());
                pageResponse.setPayPhone(user != null ? user.getPhone() : "");
            }
            //设置下单前等级
            OrderExt orderExt = orderNoMapList.get(merchantOrder.getOrderNo());
            if (orderExt != null) {
                if (ObjectUtil.isNotEmpty(orderExt.getCapaId())) {
                    Capa capa = capaService.getById(orderExt.getCapaId());
                    pageResponse.setCapaName(capa != null ? capa.getName() : "");
                }
                //设置成功后等级
                if (ObjectUtil.isNotEmpty(orderExt.getSuccessCapaId())) {
                    Capa successCapa = capaService.getById(orderExt.getSuccessCapaId());
                    pageResponse.setSuccessCapaName(successCapa != null ? successCapa.getName() : "");
                }
            }
            Merchant merchant = finalMerchantMap.get(e.getMerId());
            if (merchant != null) {
                pageResponse.setMerName(merchant.getName());
            } else {
                pageResponse.setMerName("平台");
            }
            return pageResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(startPage, pageResponses);
    }

    /**
     * 获取平台端订单各状态数量
     *
     * @param dateLimit 时间参数
     */
    @Override
    public OrderCountItemResponse getPlatformOrderStatusNum(String dateLimit, String supplyName) {
        OrderCountItemResponse response = new OrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_ALL, 0, supplyName));
        // 未支付订单
        response.setUnPaid(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_UNPAID, 0,  supplyName));
        // 未发货订单
        response.setNotShipped(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED, 0,  supplyName));
        // 待收货订单
        response.setSpike(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_SPIKE, 0,  supplyName));
        // 交易完成订单
        response.setComplete(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE, 0,  supplyName));
        // 已退款订单
        response.setRefunded(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED, 0,  supplyName));
        // 已删除订单
        response.setDeleted(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_DELETED, 0,  supplyName));
        // 待核销订单
        response.setVerification(getCount(dateLimit, OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION, 0,  supplyName));
        return response;
    }

    /**
     * 订单详情（平台）
     *
     * @param orderNo 订单编号
     * @return PlatformOrderAdminDetailResponse
     */
    @Override
    public PlatformOrderAdminDetailResponse platformInfo(String orderNo, String supplyName) {
        Order order = getByOrderNo(orderNo);
        PlatformOrderAdminDetailResponse response = new PlatformOrderAdminDetailResponse();
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        BeanUtils.copyProperties(merchantOrder, response);
        BeanUtils.copyProperties(order, response);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        if(StringUtils.isNotEmpty(supplyName)){
            List<String> barCodeList = productMaterialsService.getBarCodeList4Supply(supplyName);
            if(!CollectionUtils.isEmpty(barCodeList)){
                orderDetailList = orderDetailList.stream().filter(o->barCodeList.contains(o.getBarCode())).collect(Collectors.toList());
            }
        }

        List<OrderInfoFrontDataResponse> orderInfoList = orderDetailList.stream().map(e -> {
            OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
            BeanUtils.copyProperties(e, dataResponse);
            return dataResponse;
        }).collect(Collectors.toList());
        response.setOrderDetailList(orderInfoList);
        // 用户信息
        if(order.getUid() != null){
            User user = userService.getById(order.getUid());
            response.setNikeName(user.getNickname());
            response.setPhone(user.getPhone());
            response.setIsLogoff(user.getIsLogoff());
        }
        if (merchantOrder.getClerkId() > 0) {
            SystemAdmin clerkAdmin = systemAdminService.getById(merchantOrder.getClerkId());
            response.setClerkName(clerkAdmin.getRealName());
        }
        if (order.getMerId() > 0) {
            Merchant merchant = merchantService.getById(order.getMerId());
            response.setMerName(merchant.getName());
            response.setMerIsSelf(merchant.getIsSelf());
        }
        return response;
    }

    /**
     * 获取订单快递信息(商户端)
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfoByMerchant(Integer invoiceId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        OrderInvoice orderInvoice = orderInvoiceService.getById(invoiceId);
        if (ObjectUtil.isNull(orderInvoice)) {
            throw new CrmebException("发货单不存在");
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderInvoice.getOrderNo());
        if (ObjectUtil.isNull(merchantOrder) || !admin.getMerId().equals(merchantOrder.getMerId())) {
            throw new CrmebException("订单不存在");
        }
        return logisticService.info(orderInvoice.getTrackingNumber(), null, Optional.ofNullable(orderInvoice.getExpressCode()).orElse(""), merchantOrder.getUserPhone());
    }

    /**
     * 获取订单快递信息
     *
     * @param invoiceId 发货单ID
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(Integer invoiceId) {
        OrderInvoice orderInvoice = orderInvoiceService.getById(invoiceId);
        if (ObjectUtil.isNull(orderInvoice)) {
            throw new CrmebException("发货单不存在");
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderInvoice.getOrderNo());
        return logisticService.info(orderInvoice.getTrackingNumber(), null, Optional.ofNullable(orderInvoice.getExpressCode()).orElse(""), merchantOrder.getUserPhone());
    }

    /**
     * 核销码核销订单
     *
     * @param verifyCode 核销码
     * @return 核销结果
     */
    @Override
    public Boolean verificationOrderByCode(String verifyCode) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantOrder merchantOrder = merchantOrderService.getOneByVerifyCode(verifyCode);
        List<MerchantOrder> merchantOrderListForPrint = CollUtil.newArrayList();
        if (ObjectUtil.isNull(merchantOrder)) {
            throw new CrmebException("请选择正确的核销码");
        }
        Order order = getByOrderNo(merchantOrder.getOrderNo());
        if (!admin.getMerId().equals(order.getMerId())) {
            throw new CrmebException("请选择正确的核销码");
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION)) {
            throw new CrmebException("订单已核销");
        }
        order.setStatus(OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        order.setReceivingTime(DateUtil.date());
        merchantOrder.setClerkId(admin.getId());
        merchantOrderListForPrint.add(merchantOrder);
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(order);
            orderDetailService.takeDelivery(order.getOrderNo());
            merchantOrderService.updateById(merchantOrder);
            // 打印小票 op=1 为方法调用这里也就是支付后自动打印小票的场景
            logger.info("小票打印开始调用");
            merchantPrintService.printReceipt(merchantOrderListForPrint, 3);
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, order.getOrderNo());
        }
        return execute;
    }

    /**
     * 通过日期获取商品交易件数
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderProductNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(total_num), 0) as total_num");
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        Order order = dao.selectOne(wrapper);
        return order.getTotalNum();
    }

    /**
     * 通过日期获取商品交易成功件数
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderSuccessProductNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(total_num), 0) as total_num");
        wrapper.eq("paid", 1);
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        Order order = dao.selectOne(wrapper);
        return order.getTotalNum();
    }

    /**
     * 通过日期获取订单数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getOrderNumByDate(Integer merId, String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        if (merId > 0) {
            wrapper.eq("mer_id", merId);
        }
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 通过日期获取支付订单金额
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPayOrderAmountByDate(Integer merId, String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("pay_price");
        wrapper.eq("paid", 1);
        if (merId > 0) {
            wrapper.eq("mer_id", merId);
        }
        wrapper.eq("is_del", false);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        List<Order> orderList = dao.selectList(wrapper);
        if (CollUtil.isEmpty(orderList)) {
            return BigDecimal.ZERO;
        }
        return orderList.stream().map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 订单细节详情列表
     *
     * @param orderNo 订单号
     * @return 订单细节详情列表
     */
    @Override
    public List<OrderDetail> getDetailList(String orderNo) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        getByOrderNoAndMerId(orderNo, admin.getMerId());
        return orderDetailService.getShipmentByOrderNo(orderNo);
    }

    /**
     * 获取订单发货单列表(商户端)
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceListByMerchant(String orderNo) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        getByOrderNoAndMerId(orderNo, admin.getMerId());
        return getInvoiceList(orderNo);
    }

    /**
     * 获取订单发货单列表
     *
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public List<OrderInvoiceResponse> getInvoiceList(String orderNo) {
        return orderInvoiceService.findByOrderNo(orderNo);
    }

    /**
     * 获取可以自动完成的订单
     *
     * @param autoCompleteDay 自动完成订单天数
     * @return 可以自动完成的订单列表
     */
    @Override
    public List<Order> findCanCompleteOrder(Integer autoCompleteDay) {
        DateTime autoCompleteDate = DateUtil.offsetDay(DateUtil.date(), -autoCompleteDay);
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getId, Order::getUid, Order::getOrderNo);
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        lqw.eq(Order::getLevel, OrderConstants.ORDER_LEVEL_MERCHANT);
        lqw.le(Order::getReceivingTime, autoCompleteDate);
        return dao.selectList(lqw);
    }

    /**
     * 按订单号批量完成订单
     *
     * @param orderNoList 订单号列表
     * @return Boolean
     */
    @Override
    public Boolean batchCompleteByOrderNo(List<String> orderNoList) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_COMPLETE);
        wrapper.in(Order::getOrderNo, orderNoList);
        return update(wrapper);
    }

    /**
     * 获取订单数量（订单状态， 用户id）
     *
     * @param status 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）
     * @param userId 用户ID
     * @return 订单数量
     */
    @Override
    public Integer getCountByStatusAndUid(Integer status, Integer userId) {
        if (status < 0) {
            return 0;
        }
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getUid, userId);
        lqw.eq(Order::getIsDel, false);
        if (OrderConstants.ORDER_STATUS_WAIT_SHIPPING.equals(status)) {
            lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
        } else {
            lqw.eq(Order::getStatus, status);
        }
        lqw.ne(Order::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        return dao.selectCount(lqw);
    }

    /**
     * 通过日期获取支付订单数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getPayOrderNumByDate(String date) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.eq("paid", 1);
        wrapper.eq("is_del", 0);
        wrapper.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 获取推广订单总金额
     *
     * @param orderNoList 订单编号列表
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList) {
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.select(Order::getPayPrice);
        lqw.in(Order::getOrderNo, orderNoList);
        List<Order> orderList = dao.selectList(lqw);
        return orderList.stream().map(Order::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 订单拆单删除
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean paySplitDelete(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getIsDel, true);
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.eq(Order::getIsDel, false);
        return update(wrapper);
    }

    @Override
    public Boolean registerOrder(String orderNo, Integer uid) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getUid, uid);
        wrapper.eq(Order::getOrderNo, orderNo);
        wrapper.last(" and  uid is null");
        return update(wrapper);
    }

    /**
     * 通过原始单号获取订单列表
     *
     * @param orderNo 原始单号
     * @return 订单列表
     */
    @Override
    public List<Order> getByPlatOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getPlatOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 判断用户是否存在待处理订单
     * 待发货、部分发货、待核销
     *
     * @param uid 用户id
     */
    @Override
    public Boolean isExistPendingOrderByUid(Integer uid) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.select(Order::getId);
        lqw.eq(Order::getUid, uid);
        lqw.eq(Order::getIsDel, 0);
        lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING, OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
        lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(order);
    }

    /**
     * 获取待收货订单
     *
     * @param sendTime 发货时间
     * @return List
     */
    @Override
    public List<Order> findAwaitTakeDeliveryOrderList(String sendTime) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
        lqw.le(Order::getUpdateTime, sendTime);
        lqw.eq(Order::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取待发货订单数量
     *
     * @return Integer
     */
    @Override
    public Integer getNotShippingNum(Integer merId, String  supplyName) {
        return getCount("", OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED, merId,  supplyName);
    }

    /**
     * 获取待核销订单数量
     *
     * @return Integer
     */
    @Override
    public Integer getAwaitVerificationNum(Integer merId, String  supplyName) {
        return getCount("", OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION, merId,  supplyName);
    }

    /**
     * 获取用户购买的商品数量
     *
     * @param uid         用户ID
     * @param proId       商品ID
     * @param productType 商品类型
     */
    @Override
    public Integer getProductNumCount(Integer uid, Integer proId, Integer productType) {
        return dao.getProductNumCount(uid, proId, productType);
    }

    /**
     * 获取某一天的所有数据
     *
     * @param merId 商户id，0为所有商户
     * @param date  日期：年-月-日
     * @return List
     */
    @Override
    public List<Order> findPayByDate(Integer merId, String date) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(Order::getMerId, merId);
        }
        lqw.eq(Order::getPaid, 1);
        lqw.ne(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
        lqw.eq(Order::getIsDel, 0);
        lqw.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取导出订单列表
     *
     * @param request 请求参数
     */
    @Override
    public List<Order> findExportList(OrderSearchRequest request,Integer id) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            lqw.eq(Order::getMerId, request.getMerId());
        }
        if(StringUtils.isNotEmpty(request.getSupplyName())){
            List<String> orderNoList = orderDetailService.getOrderNoList4SupplyName(request.getSupplyName());
            if (!CollectionUtils.isEmpty(orderNoList)) {
                lqw.in(Order::getOrderNo, orderNoList);
            }
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.and((wrapper) -> {
                wrapper.eq(Order::getOrderNo, request.getOrderNo())
                        .or().eq(Order::getPlatOrderNo, request.getOrderNo());
            });
        }
        if (StrUtil.isNotBlank(request.getPayTime())) {
            getPayTimeWhere(lqw,request.getPayTime());
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(Order::getType, request.getType());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            getRequestTimeWhere(lqw, request.getDateLimit());
        }
        getMerchantStatusWhere(lqw, request.getStatus());
        lqw.gt(Order::getId, id);
        lqw.orderByAsc(Order::getId);
        lqw.last("LIMIT 1000");
        return dao.selectList(lqw);
    }

    @Override
    public List<Order> getWaitPayList(int intervalMinutes) {
        Date now = DateTimeUtils.getNow();
        Date start = DateTimeUtils.addMinutes(now, -intervalMinutes);
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
        lqw.eq(Order::getLevel, OrderConstants.ORDER_LEVEL_PLATFORM);
        lqw.eq(Order::getIsDel, false);
        lqw.eq(Order::getPaid, false);
        lqw.ge(Order::getCreateTime, start);
        lqw.le(Order::getCreateTime, now);
        return list(lqw);
    }

    @Override
    public List<Order> getWaitPullList() {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        lqw.eq(Order::getPaid, true);
        lqw.eq(Order::getIfPull, false);
        lqw.eq(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_NOT_APPLY);
        lqw.eq(Order::getLevel, OrderConstants.ORDER_LEVEL_MERCHANT);
        lqw.eq(Order::getIsDel, false);
        return list(lqw);
    }

    @Override
    public Order getLastOne(Integer uid, String platform) {
        return getOne(new QueryWrapper<Order>().lambda()
                .eq(Order::getUid, uid)
                .eq(Order::getPaid, true)
                .eq(Order::getLevel, 0)
                .eq(Order::getRefundStatus, 0)
                .eq(StringUtils.isEmpty(platform), Order::getPlatform, "报单")
                .orderByDesc(Order::getId)
                .last(" limit 1"));
    }

    @Override
    public List<Order> getSuccessList(Date startTime, Date endTime) {
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getPaid, true).ge(Order::getPayTime, startTime).lt(Order::getPayTime, endTime);
        List<Order> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        // 退款商户订单过滤出来
        List<String> refundList = list.stream().filter(o -> Integer.valueOf(1).equals(o.getLevel()) && !Integer.valueOf(0).equals(o.getRefundStatus())).map(Order::getPlatOrderNo).collect(Collectors.toList());
        //  支付成功的平台订单 商户订单没有退款
        List<Order> platList = list.stream().filter(o -> Integer.valueOf(0).equals(o.getLevel()) && !refundList.contains(o.getOrderNo())).collect(Collectors.toList());
        return CollectionUtils.isEmpty(platList) ? Lists.newArrayList() : platList;
    }

    @Override
    public Integer getGoodsPrice(String goodsIds) {
        return dao.getGoodsPirce(goodsIds,userService.getUserId());
    }

    /**
     * 根据订单编号获取订单
     *
     * @param orderNo 订单编号
     * @param merId   商户ID
     * @return Order
     * @Author 莫名
     * @Date 2022/10/8 11:40
     */
    private Order getByOrderNoAndMerId(String orderNo, Integer merId) {
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        lqw.eq(Order::getOrderNo, orderNo);
        Boolean ifPlatform = 0 == merId;
        if (BooleanUtils.isNotTrue(ifPlatform)) {
            lqw.eq(Order::getMerId, merId);
        }
        lqw.last(" limit 1");
        Order order = dao.selectOne(lqw);
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException("订单不存在");
        }
        return order;
    }

    /**
     * 拆单发货
     *
     * @param request         发货参数
     * @param order           订单
     * @param merchantOrder   订单商户信息
     * @param orderDetailList 订单详情列表鸟
     */
    private Boolean splitSendExpress(OrderSendRequest request, Order order, MerchantOrder merchantOrder, List<OrderDetail> orderDetailList) {
        Express express = expressService.getByCode(request.getExpressCode());
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException("未找到快递公司");
        }
        // 筛选需要发货的订单详情
        List<SplitOrderSendDetailRequest> detailRequestList = request.getDetailList();
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        detailRequestList.forEach(detail -> {
            OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getId().equals(detail.getOrderDetailId())).findAny().orElse(null);
            orderDetail.setDeliveryNum(orderDetail.getDeliveryNum() + detail.getNum());
            OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
            BeanUtils.copyProperties(orderDetail, invoiceDetail);
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setCreateTime(DateUtil.date());
            invoiceDetail.setUpdateTime(DateUtil.date());
            orderInvoiceDetailList.add(invoiceDetail);
            orderDetailUpdateList.add(orderDetail);
        });
        String trackingNumber = request.getExpressNumber();
        if (request.getExpressRecordType().equals(2)) { // 电子面单
            List<String> productNameList = orderInvoiceDetailList.stream().map(OrderInvoiceDetail::getProductName).collect(Collectors.toList());
            String cargo = String.join(",", productNameList);
            trackingNumber = expressDump(request, merchantOrder, express, cargo);
        }
        // 生成发货单
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setExpressCode(express.getCode());
        invoice.setExpressName(express.getName());
        invoice.setTrackingNumber(trackingNumber);
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS);
        order.setStatus(OrderConstants.ORDER_STATUS_PART_SHIPPING);

        String message = OrderStatusConstants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}", express.getName()).replace("{deliveryCode}", trackingNumber);
        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS_SPLIT_OLD, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("快递拆单发货失败！" + order.getOrderNo());
        List<OrderDetail> detailList = orderDetailService.getByOrderNo(order.getOrderNo());
        long count = detailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
        if (count <= 0) {
            order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
            updateById(order);
        }

        SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.DELIVER_GOODS_MARK);
        // 发送消息通知
        pushMessageOrder(order, payNotification, invoice);
        return execute;
    }


    /**
     * 快递发货
     *
     * @param request       发货参数
     * @param order         订单信息
     * @param merchantOrder 订单商户信息
     */
    private Boolean sendExpress(OrderSendRequest request, Order order, MerchantOrder merchantOrder) {
        //快递公司信息
        Express express = expressService.getByCode(request.getExpressCode());
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException("未找到快递公司");
        }
        // 筛选需要发货的订单详情
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        List<OrderInvoiceDetail> orderInvoiceDetailList = new ArrayList<>();
        List<OrderDetail> orderDetailUpdateList = new ArrayList<>();
        orderDetailList.forEach(od -> {
            if (od.getPayNum() > od.getDeliveryNum()) {
                OrderInvoiceDetail invoiceDetail = new OrderInvoiceDetail();
                BeanUtils.copyProperties(od, invoiceDetail);
                invoiceDetail.setNum(od.getPayNum() - od.getDeliveryNum());
                invoiceDetail.setCreateTime(DateUtil.date());
                invoiceDetail.setUpdateTime(DateUtil.date());
                orderInvoiceDetailList.add(invoiceDetail);
                od.setDeliveryNum(od.getPayNum());
                orderDetailUpdateList.add(od);
            }
        });
        if (CollUtil.isEmpty(orderInvoiceDetailList)) {
            throw new CrmebException("订单没有需要发货的商品");
        }
        String trackingNumber = request.getExpressNumber();
        if (request.getExpressRecordType().equals(2)) { // 电子面单
            List<String> productNameList = orderInvoiceDetailList.stream().map(OrderInvoiceDetail::getProductName).collect(Collectors.toList());
            String cargo = String.join(",", productNameList);
            trackingNumber = expressDump(request, merchantOrder, express, cargo);
        }

        // 生成发货单
        OrderInvoice invoice = new OrderInvoice();
        invoice.setMerId(order.getMerId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUid(order.getUid());
        invoice.setExpressCode(express.getCode());
        invoice.setExpressName(express.getName());
        invoice.setTrackingNumber(trackingNumber);
        invoice.setTotalNum(orderInvoiceDetailList.stream().mapToInt(OrderInvoiceDetail::getNum).sum());

        merchantOrder.setDeliveryType(OrderConstants.ORDER_DELIVERY_TYPE_EXPRESS);
        order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);

        String message = OrderStatusConstants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}", express.getName()).replace("{deliveryCode}", trackingNumber);

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(order);
            merchantOrderService.updateById(merchantOrder);
            orderDetailService.updateBatchById(orderDetailUpdateList);
            orderInvoiceService.save(invoice);
            orderInvoiceDetailList.forEach(e -> e.setInvoiceId(invoice.getId()));
            orderInvoiceDetailService.saveBatch(orderInvoiceDetailList);
            //订单记录增加
            orderStatusService.createLog(request.getOrderNo(), OrderStatusConstants.ORDER_STATUS_EXPRESS, message);
            return Boolean.TRUE;
        });

        if (!execute) throw new CrmebException("快递发货失败！");
        // 发送消息通知
        SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.DELIVER_GOODS_MARK);
        pushMessageOrder(order, payNotification, invoice);
        return execute;
    }

    /**
     * 电子面单
     *
     * @param request       发货参数
     * @param merchantOrder 订单商户信息
     * @param express       物流公司
     * @param cargo         打印物品名称
     * @return 快递单号
     */
    private String expressDump(OrderSendRequest request, MerchantOrder merchantOrder, Express express, String cargo) {
        String configExportOpen = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_EXPORT_OPEN);
        if (!configExportOpen.equals("1")) {// 电子面单未开启
            throw new CrmebException("请先开启电子面单");
        }
        MyRecord record = new MyRecord();
        record.set("com", express.getCode());// 快递公司编码
        record.set("to_name", merchantOrder.getRealName());// 收件人
        record.set("to_tel", merchantOrder.getUserPhone());// 收件人电话
        record.set("to_addr", merchantOrder.getUserAddress());// 收件人详细地址
        record.set("from_name", request.getToName());// 寄件人
        record.set("from_tel", request.getToTel());// 寄件人电话
        record.set("from_addr", request.getToAddr());// 寄件人详细地址
        record.set("temp_id", request.getExpressTempId());// 电子面单模板ID
        String siid = systemConfigService.getValueByKeyException("config_export_siid");
        record.set("siid", siid);// 云打印机编号
        record.set("count", merchantOrder.getTotalNum());// 商品数量

        //获取购买商品名称
        if (StrUtil.isBlank(cargo)) {
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(merchantOrder.getOrderNo());
            List<String> productNameList = orderDetailList.stream().map(OrderDetail::getProductName).collect(Collectors.toList());
            record.set("cargo", String.join(",", productNameList));// 物品名称
        } else {
            record.set("cargo", cargo);// 物品名称
        }
        if (express.getPartnerId()) {
            record.set("partner_id", express.getAccount());// 电子面单月结账号(部分快递公司必选)
        }
        if (express.getPartnerKey()) {
            record.set("partner_key", express.getPassword());// 电子面单密码(部分快递公司必选)
        }
        if (express.getNet()) {
            record.set("net", express.getNetName());// 收件网点名称(部分快递公司必选)
        }

        MyRecord myRecord = onePassService.expressDump(record);
        return myRecord.getStr("kuaidinum");
    }

    /**
     * 虚拟发货
     *
     * @param orderNo 订单编号
     */
    private Boolean virtual(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
        wrapper.eq(Order::getOrderNo, orderNo);
        Boolean execute = transactionTemplate.execute(e -> {
            update(wrapper);
            merchantOrderService.virtual(orderNo);
            orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_DELIVERY_VI, OrderStatusConstants.ORDER_LOG_MESSAGE_DELIVERY_VI);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("虚拟发货失败");
        }
        return execute;
    }

    /**
     * 校验快递发货参数
     *
     * @param request 发货参数
     */
    private void validateExpressSend(OrderSendRequest request) {
        if (request.getExpressRecordType().equals(1)) {
            if (StrUtil.isBlank(request.getExpressNumber())) throw new CrmebException("请填写快递单号");
            return;
        }
        if (StrUtil.isBlank(request.getExpressCode())) throw new CrmebException("请选择快递公司");
        if (StrUtil.isBlank(request.getExpressTempId())) throw new CrmebException("请选择电子面单");
        if (StrUtil.isBlank(request.getToName())) throw new CrmebException("请填写寄件人姓名");
        if (StrUtil.isBlank(request.getToTel())) throw new CrmebException("请填写寄件人电话");
        if (StrUtil.isBlank(request.getToAddr())) throw new CrmebException("请填写寄件人地址");
    }

    /**
     * 获取订单总数
     *
     * @param dateLimit 时间端
     * @param status    String 状态
     * @return Integer
     */
    private Integer getCount(String dateLimit, String status, Integer merId, String supplyName) {
        //总数只计算时间
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(supplyName)) {
            List<String> orderNoList = orderDetailService.getOrderNoList4SupplyName(supplyName);
            if (!CollectionUtils.isEmpty(orderNoList)) {
                lqw.in(Order::getOrderNo, orderNoList);
            }
        }
        if (merId > 0) {
            lqw.eq(Order::getMerId, merId);
            lqw.eq(Order::getIsMerchantDel, false);
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            getRequestTimeWhere(lqw, dateLimit);
        }
        getMerchantStatusWhere(lqw, status);
        return dao.selectCount(lqw);
    }

    /**
     * 根据订单状态获取where条件(商户端)
     *
     * @param lqw    LambdaQueryWrapper<Order> 表达式
     * @param status 订单状态（all 全部； 未支付 unPaid； 未发货 notShipped；待收货 spike；已完成 complete；已退款:refunded；已删除:deleted
     */
    private void getMerchantStatusWhere(LambdaQueryWrapper<Order> lqw, String status) {
        if (StrUtil.isBlank(status)) {
            return;
        }
        switch (status) {
            case OrderConstants.MERCHANT_ORDER_STATUS_ALL: //全部
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_UNPAID: //未支付
                lqw.eq(Order::getPaid, false);
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_PAY);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_NOT_SHIPPED: //未发货
                lqw.in(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_SHIPPING, OrderConstants.ORDER_STATUS_PART_SHIPPING);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_SPIKE: //待收货
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_RECEIVING: //已收货
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_COMPLETE: //交易完成
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_COMPLETE);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION: //待核销
                lqw.eq(Order::getStatus, OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.ne(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_REFUNDED: //已退款
                lqw.eq(Order::getPaid, true);
                lqw.eq(Order::getCancelStatus, OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
                lqw.eq(Order::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_REFUND);
                lqw.eq(Order::getIsUserDel, false);
                break;
            case OrderConstants.MERCHANT_ORDER_STATUS_DELETED: //已删除
                lqw.eq(Order::getIsUserDel, true);
                break;
        }
        lqw.eq(Order::getIsDel, false);
    }

    /**
     * 获取request的where条件
     *
     * @param lqw       LambdaQueryWrapper<Order> 表达式
     * @param dateLimit 时间区间参数
     */
    private void getRequestTimeWhere(LambdaQueryWrapper<Order> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(Order::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }
    private void getPayTimeWhere(LambdaQueryWrapper<Order> lqw, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        lqw.between(Order::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }


    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(Order order, SystemNotification notification, OrderInvoice invoice) {
        if (!order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC) && notification.getIsWechat().equals(1)) {
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            /**
             * {{first.DATA}}
             * 订单编号：{{keyword1.DATA}}
             * 物流公司：{{keyword2.DATA}}
             * 物流单号：{{keyword3.DATA}}
             * {{remark.DATA}}
             */
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            temMap.put("keyword1", order.getOrderNo());
            temMap.put("keyword2", invoice.getExpressName());
            temMap.put("keyword3", invoice.getExpressCode());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            templateMessageService.pushTemplateMessage(notification.getWechatId(), temMap, userToken.getToken());
            return;
        }
        if (order.getPayChannel().equals(PayConstants.PAY_CHANNEL_WECHAT_MINI) && notification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return;
            }
            // 组装数据
            // 注释部分为丰享汇小程序
//        temMap.put("character_string1", storeOrder.getOrderId());
//        temMap.put("name3", storeOrder.getDeliveryName());
//        temMap.put("character_string4", storeOrder.getDeliveryId());
//        temMap.put("thing7", "您的订单已发货");
            // 放开部分为二码秦川小程序
            temMap.put("character_string7", order.getOrderNo());
            temMap.put("thing1", invoice.getExpressName());
            temMap.put("character_string2", invoice.getExpressCode());
            temMap.put("thing11", "您的订单已发货");
            templateMessageService.pushMiniTemplateMessage(notification.getRoutineId(), temMap, userToken.getToken());
        }
    }


}

