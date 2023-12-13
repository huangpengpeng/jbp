package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.jbp.service.service.*;
import com.jbp.common.config.CrmebConfig;
import com.jbp.common.constants.*;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.record.BrowseRecord;
import com.jbp.common.model.record.UserVisitRecord;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.RedisUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 异步调用服务实现类
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
public class AsyncServiceImpl implements AsyncService {

    private final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Autowired
    private ProductService storeProductService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 商品详情统计
     *
     * @param proId 商品id
     * @param uid   用户uid
     */
    @Async
    @Override
    public void productDetailStatistics(Integer proId, Integer uid) {
        // 商品浏览量+1
        storeProductService.addBrowse(proId);
        // 商品浏览量统计(每日/商城)
        String dateStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        redisUtil.incrAndCreate(RedisConstants.PRO_PAGE_VIEW_KEY + dateStr);
        // 商品浏览量统计(每日/个体)
        redisUtil.incrAndCreate(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, dateStr, proId));
        if (uid.equals(0)) {
            return;
        }
        // 保存用户访问记录
        if (uid > 0) {
            UserVisitRecord visitRecord = new UserVisitRecord();
            visitRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
            visitRecord.setUid(uid);
            visitRecord.setVisitType(VisitRecordConstants.VISIT_TYPE_DETAIL);
            userVisitRecordService.save(visitRecord);

            BrowseRecord browseRecord = browseRecordService.getByUidAndProId(uid, proId);
            if (ObjectUtil.isNull(browseRecord)) {
                browseRecord = new BrowseRecord();
                browseRecord.setUid(uid);
                browseRecord.setProductId(proId);
                browseRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
                browseRecord.setCreateTime(DateUtil.date());
                browseRecordService.save(browseRecord);
            } else {
                browseRecord.setDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE));
                browseRecordService.myUpdate(browseRecord);
            }
        }

    }

    /**
     * 保存用户访问记录
     *
     * @param userId    用户id
     * @param visitType 访问类型
     */
    @Async
    @Override
    public void saveUserVisit(Integer userId, Integer visitType) {
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(DateUtil.date().toDateStr());
        visitRecord.setUid(userId);
        visitRecord.setVisitType(visitType);
        userVisitRecordService.save(visitRecord);
    }

    /**
     * 订单支付成功拆单处理
     * @param orderNo 订单号
     */
    @Async
    @Override
    public void orderPaySuccessSplit(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            logger.error("异步——订单支付成功拆单处理 | 订单不存在，orderNo: {}", orderNo);
            return;
        }

        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNo);
        if (CollUtil.isEmpty(merchantOrderList)) {
            logger.error("异步——订单支付成功拆单处理 | 商户订单信息不存在,orderNo: {}", orderNo);
            return;
        }
        Boolean execute;
        if (merchantOrderList.size() == 1) {
            // 单商户订单
            execute = oneMerchantOrderProcessing(order, merchantOrderList.get(0));
        } else {
            execute = manyMerchantOrderProcessing(order, merchantOrderList);
        }
        if (!execute) {
            logger.error("异步——订单支付成功拆单处理 | 拆单处理失败，orderNo: {}", orderNo);
            return;
        }
        // 添加支付成功redis队列
        logger.info("异步——订单支付成功拆单处理 | 拆单成功，加入后置处理队列");
        redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, order.getOrderNo());
    }

    /**
     * 访问用户个人中心记录
     * @param uid 用户id
     */
    @Async
    @Override
    public void visitUserCenter(Integer uid) {
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(DateUtil.date().toString("yyyy-MM-dd"));
        visitRecord.setUid(uid);
        visitRecord.setVisitType(VisitRecordConstants.VISIT_TYPE_CENTER);
        userVisitRecordService.save(visitRecord);
    }

    /**
     * 安装统计
     */
    @Async
    @Override
    public void installStatistics() {
        String isInstall = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_INSTALL_STATISTICS);
        if (StrUtil.isNotBlank(isInstall) && isInstall.equals("1")) {
            return;
        }
        String version = crmebConfig.getVersion();
        if (StrUtil.isBlank(version) || !(StrUtil.startWithIgnoreCase(version, "CRMEB"))) {
            return;
        }
        String apiUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(apiUrl) || !(StrUtil.startWithIgnoreCase(apiUrl, "http"))) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("host", apiUrl);
        map.put("version", version);
        map.put("https", "https");
        String result = HttpUtil.post("https://shop.crmeb.net/index.php/admin/server.upgrade_api/updatewebinfo", JSONObject.toJSONString(map));
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("status").equals(200)) {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_INSTALL_STATISTICS, "1");
        }
    }

    /**
     * 单商户订单处理
     * @param order 主订单
     * @param merchantOrder 商户订单
     */
    private Boolean oneMerchantOrderProcessing(Order order, MerchantOrder merchantOrder) {
        // 赠送积分积分处理：1.下单赠送积分
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        presentIntegral(merchantOrder, orderDetailList, order);

        // 生成新的商户订单
        Order newOrder = new Order();
        BeanUtils.copyProperties(order, newOrder);
        MerchantOrder newMerOrder = new MerchantOrder();
        BeanUtils.copyProperties(merchantOrder, newMerOrder);
        newOrder.setId(null);
        newOrder.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_MERCHANT));
        newOrder.setMerId(merchantOrder.getMerId());
        newOrder.setLevel(OrderConstants.ORDER_LEVEL_MERCHANT);
        newOrder.setPlatOrderNo(order.getOrderNo());
        newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
            newOrder.setStatus(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
        }
        newMerOrder.setId(null);
        newMerOrder.setOrderNo(newOrder.getOrderNo());
        List<OrderDetail> newOrderDetailList = orderDetailList.stream().map(e -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(e, orderDetail);
            orderDetail.setId(null);
            orderDetail.setOrderNo(newOrder.getOrderNo());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setIsDel(true);
        return transactionTemplate.execute(e -> {
            // 订单
            Boolean delete = orderService.paySplitDelete(order.getOrderNo());
            if (!delete) {
                logger.error("支付拆单失败，订单号:{}", order.getOrderNo());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            merchantOrderService.updateById(merchantOrder);
            if (order.getGainIntegral() > 0) {
                orderDetailService.updateBatchById(orderDetailList);
            }
            orderService.save(newOrder);
            merchantOrderService.save(newMerOrder);
            orderDetailService.saveBatch(newOrderDetailList);
            //订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, order.getOrderNo()));
            return Boolean.TRUE;
        });
    }

    /**
     * 多商户订单处理
     * @param order 主订单
     * @param merchantOrderList 商户订单列表
     */
    private Boolean manyMerchantOrderProcessing(Order order, List<MerchantOrder> merchantOrderList) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        // 赠送积分积分处理：1.下单赠送积分
        presentIntegral(merchantOrderList, orderDetailList, order);
        // 商户拆单
        List<Order> newOrderList = CollUtil.newArrayList();
        List<MerchantOrder> newMerchantOrderList = CollUtil.newArrayList();
        List<OrderDetail> newOrderDetailList = CollUtil.newArrayList();

        order.setIsDel(true);
        for (MerchantOrder merchantOrder : merchantOrderList) {
            Order newOrder = new Order();
            BeanUtils.copyProperties(order, newOrder);
            newOrder.setId(null);
            newOrder.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_MERCHANT));
            newOrder.setMerId(merchantOrder.getMerId());
            newOrder.setTotalNum(merchantOrder.getTotalNum());
            newOrder.setProTotalPrice(merchantOrder.getProTotalPrice());
            newOrder.setTotalPostage(merchantOrder.getTotalPostage());
            newOrder.setTotalPrice(merchantOrder.getTotalPrice());
            newOrder.setCouponPrice(merchantOrder.getCouponPrice());
            newOrder.setUseIntegral(merchantOrder.getUseIntegral());
            newOrder.setIntegralPrice(merchantOrder.getIntegralPrice());
            newOrder.setPayPrice(merchantOrder.getPayPrice());
            newOrder.setPayPostage(merchantOrder.getPayPostage());
            newOrder.setGainIntegral(merchantOrder.getGainIntegral());
            newOrder.setLevel(OrderConstants.ORDER_LEVEL_MERCHANT);
            newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
            newOrder.setPlatOrderNo(order.getOrderNo());
            newOrder.setIsDel(false);
            newOrder.setStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
            if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                newOrder.setStatus(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
            }
            MerchantOrder newMerchantOrder = new MerchantOrder();
            BeanUtils.copyProperties(merchantOrder, newMerchantOrder);
            newMerchantOrder.setId(null);
            newMerchantOrder.setOrderNo(newOrder.getOrderNo());
            List<OrderDetail> tempDetailList = orderDetailList.stream().filter(e -> e.getMerId().equals(merchantOrder.getMerId())).collect(Collectors.toList());
            tempDetailList.forEach(d -> {
                d.setId(null);
                d.setOrderNo(newOrder.getOrderNo());
            });
            newOrderList.add(newOrder);
            newMerchantOrderList.add(newMerchantOrder);
            newOrderDetailList.addAll(tempDetailList);
        }

        return transactionTemplate.execute(e -> {
            // 订单
            Boolean delete = orderService.paySplitDelete(order.getOrderNo());
            if (!delete) {
                logger.error("支付拆单失败，订单号:{}", order.getOrderNo());
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            merchantOrderService.updateBatchById(merchantOrderList);
            orderService.saveBatch(newOrderList);
            merchantOrderService.saveBatch(newMerchantOrderList);
            orderDetailService.saveBatch(newOrderDetailList);
            // 订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, order.getOrderNo()));
//            newOrderList.forEach(o -> orderStatusService.createLog(o.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, order.getOrderNo())));
            return Boolean.TRUE;
        });
    }

    /**
     * 赠送积分处理
     */
    private void presentIntegral(MerchantOrder merchantOrder, List<OrderDetail> orderDetailList, Order order) {
        //比例
        String integralRatioStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        if (StrUtil.isNotBlank(integralRatioStr) && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal integralBig = new BigDecimal(integralRatioStr);
            int giveIntegral = merchantOrder.getPayPrice().divide(integralBig, 0, BigDecimal.ROUND_DOWN).intValue();
            merchantOrder.setGainIntegral(giveIntegral);
            order.setGainIntegral(giveIntegral);
            if (giveIntegral > 0) {
                // 订单详情
                for (int i = 0; i < orderDetailList.size(); i++) {
                    OrderDetail orderDetail = orderDetailList.get(i);
                    if (orderDetailList.size() == (i + 1)) {
                        orderDetail.setGainIntegral(giveIntegral);
                    }
                    BigDecimal ratio = orderDetail.getPayPrice().divide(merchantOrder.getPayPrice(), 10, BigDecimal.ROUND_HALF_UP);
                    int integral = new BigDecimal(Integer.toString(giveIntegral)).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                    orderDetail.setGainIntegral(integral);
                    giveIntegral = giveIntegral - integral;
                }
            }
        }
    }

    /**
     * 赠送积分处理
     */
    private void presentIntegral(List<MerchantOrder> merchantOrderList, List<OrderDetail> orderDetailList, Order order) {
        int integral = 0;
        //比例
        String integralRatioStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        if (StrUtil.isNotBlank(integralRatioStr) && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (MerchantOrder merOrder : merchantOrderList) {
                BigDecimal integralBig = new BigDecimal(integralRatioStr);
                int giveIntegral = merOrder.getPayPrice().divide(integralBig, 0, BigDecimal.ROUND_DOWN).intValue();
                integral += giveIntegral;
                merOrder.setGainIntegral(giveIntegral);
                if (giveIntegral > 0) {
                    List<OrderDetail> detailList = orderDetailList.stream().filter(e -> e.getMerId().equals(merOrder.getMerId())).collect(Collectors.toList());
                    // 订单详情
                    for (int i = 0; i < detailList.size(); i++) {
                        OrderDetail orderDetail = detailList.get(i);
                        if (detailList.size() == (i + 1)) {
                            orderDetail.setGainIntegral(giveIntegral);
                        }
                        BigDecimal ratio = orderDetail.getPayPrice().divide(merOrder.getPayPrice(), 10, BigDecimal.ROUND_HALF_UP);
                        int detailIntegral = new BigDecimal(Integer.toString(giveIntegral)).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                        orderDetail.setGainIntegral(detailIntegral);
                        giveIntegral = giveIntegral - detailIntegral;
                    }
                }
            }
            if (integral > 0) {
                order.setGainIntegral(integral);
            }
        }
    }
}
