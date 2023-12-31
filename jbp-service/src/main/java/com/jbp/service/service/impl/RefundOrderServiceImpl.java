package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantAddress;
import com.jbp.common.model.order.*;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserBalanceRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.utils.WxPayUtil;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.common.vo.RefundOrderDetailOrderInfoVo;
import com.jbp.common.vo.WxRefundVo;
import com.jbp.service.dao.RefundOrderDao;
import com.jbp.service.service.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RefundOrderServiceImpl 接口实现
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
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderDao, RefundOrder> implements RefundOrderService {

    @Resource
    private RefundOrderDao dao;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantAddressService merchantAddressService;
    @Autowired
    private RefundOrderStatusService refundOrderStatusService;
    @Autowired
    private MerchantOrderService merchantOrderService;

    /**
     * 商户端退款订单分页列表
     *
     * @param request          查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantRefundOrderPageResponse> getMerchantAdminPage(RefundOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<RefundOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        QueryWrapper<RefundOrder> wrapper = Wrappers.query();
        wrapper.eq("mer_id", systemAdmin.getMerId());
        if (StrUtil.isNotEmpty(request.getRefundOrderNo())) {
            wrapper.eq("refund_order_no", request.getRefundOrderNo());
        }
        if (StrUtil.isNotEmpty(request.getOrderNo())) {
            wrapper.eq("order_no", request.getOrderNo());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            getRequestTimeWhere(wrapper, request.getDateLimit());
        }
        getStatusWhere(wrapper, request.getRefundStatus());
        wrapper.orderByDesc("id");
        List<RefundOrder> refundOrderList = dao.selectList(wrapper);
        if (CollUtil.isEmpty(refundOrderList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> uidList = refundOrderList.stream().map(RefundOrder::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        List<MerchantRefundOrderPageResponse> responseList = refundOrderList.stream().map(order -> {
            MerchantRefundOrderPageResponse response = new MerchantRefundOrderPageResponse();
            response.setRefundOrderNo(order.getRefundOrderNo());
            response.setOrderNo(order.getOrderNo());
            response.setUid(order.getUid());
            response.setRefundStatus(order.getRefundStatus());
            response.setRefundPrice(order.getRefundPrice());
            response.setCreateTime(order.getCreateTime());
            response.setMerRemark(order.getMerRemark());
            response.setAfterSalesType(order.getAfterSalesType());
            response.setUserNickName(userMap.get(order.getUid()).getNickname());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取商户端退款订单各状态数量
     *
     * @param dateLimit 时间参数
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getMerchantOrderStatusNum(String dateLimit) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return getOrderStatusNum(dateLimit, systemAdmin.getMerId());
    }

    /**
     * 备注退款单
     *
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean mark(RefundOrderRemarkRequest request) {
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }
        refundOrder.setMerRemark(request.getRemark());
        return updateById(refundOrder);
    }

    /**
     * 拒绝退款
     *
     * @param request 拒绝退款参数
     * @return Boolean
     */
    @Override
    public Boolean refundRefuse(OrderRefundAuditRequest request) {
        if (StrUtil.isEmpty(request.getReason())) {
            throw new CrmebException("请填写拒绝退款原因");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)) {
            throw new CrmebException("退款单状态异常");
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT);
        refundOrder.setRefundReason(request.getReason());

        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException("退款单关联的订单不存在");
        }
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(refundOrder);
            orderDetailService.updateById(orderDetail);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核拒绝");
            return Boolean.TRUE;
        });
        if (execute) {
            // 设置订单退款状态
            settingOrderStatus(order);
            orderService.updateById(order);
        }
        return execute;
    }

    /**
     * 退款
     * @param request 退款参数
     * @return Boolean
     */
    @Override
    public Boolean refund(OrderRefundAuditRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)) {
            throw new CrmebException("退款单状态异常");
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }


        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        if (!order.getPaid()) {
            throw new CrmebException("未支付无法退款");
        }
        if (refundOrder.getAfterSalesType().equals(2)) {
            if (ObjectUtil.isNull(request.getMerAddressId())) {
                throw new CrmebException("请选择商家退货地址");
            }
            MerchantAddress merchantAddress = merchantAddressService.getByIdException(request.getMerAddressId());
            refundOrder.setReceiver(merchantAddress.getReceiverName());
            refundOrder.setReceiverPhone(merchantAddress.getReceiverPhone());
            refundOrder.setReceiverAddressDetail(merchantAddress.getDetail());
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS);

            return transactionTemplate.execute(e -> {
                updateById(refundOrder);
                refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
                return Boolean.TRUE;
            });
        }

        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        BigDecimal refundPrice;
        if (orderDetail.getPayNum().equals(refundOrderInfo.getApplyRefundNum())) {
            // sku整退
            refundPrice = orderDetail.getPayPrice();
            orderDetail.setRefundPrice(refundPrice);
            refundOrderInfo.setRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
                refundOrderInfo.setRefundUseIntegral(orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
                refundOrderInfo.setRefundGainIntegral(orderDetail.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee());
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else if (orderDetail.getPayNum() == (orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum())) { // sku分退
            refundPrice = orderDetail.getPayPrice().subtract(orderDetail.getRefundPrice());
            // sku最后一部分退款
            orderDetail.setRefundPrice(orderDetail.getPayPrice());
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral() - orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().subtract(orderDetail.getRefundIntegralPrice()));
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral() - orderDetail.getRefundGainIntegral());
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().subtract(orderDetail.getRefundFirstBrokerageFee()));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().subtract(orderDetail.getRefundSecondBrokerageFee()));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().subtract(orderDetail.getRefundPlatCouponPrice()));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().subtract(orderDetail.getRefundFreightFee()));
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else {
            // sku非最后一部分退款
            BigDecimal ratio = new BigDecimal(refundOrderInfo.getApplyRefundNum()).divide(new BigDecimal(orderDetail.getPayNum()), 10, BigDecimal.ROUND_HALF_UP);
            refundPrice = orderDetail.getPayPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderDetail.setRefundPrice(orderDetail.getRefundPrice().add(refundPrice));
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);

            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundUseIntegral(orderDetail.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                orderDetail.setRefundGainIntegral(orderDetail.getRefundGainIntegral() + refundOrderInfo.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundFirstBrokerageFee()));
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee()));
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getRefundPlatCouponPrice().add(refundOrderInfo.getRefundPlatCouponPrice()));
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFreightFee(orderDetail.getRefundFreightFee().add(refundOrderInfo.getRefundFreightFee()));
            }
        }

        refundOrder.setRefundPrice(refundPrice);
        refundOrder.setMerchantRefundPrice(refundOrderInfo.getMerchantRefundPrice());
        refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
        refundOrder.setRefundIntegralPrice(refundOrderInfo.getRefundIntegralPrice());
        refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());
        refundOrder.setRefundFirstBrokerageFee(refundOrderInfo.getRefundFirstBrokerageFee());
        refundOrder.setRefundSecondBrokerageFee(refundOrderInfo.getRefundSecondBrokerageFee());
        refundOrder.setRefundPayType(order.getPayType());
        refundOrder.setRefundPlatCouponPrice(refundOrderInfo.getRefundPlatCouponPrice());
        refundOrder.setRefundFreightFee(refundOrderInfo.getRefundFreightFee());
        //退款
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                Order platOrder = orderService.getByOrderNo(order.getPlatOrderNo());
                wxRefund(order, refundOrder.getRefundOrderNo(), refundPrice, platOrder.getPayPrice());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！" + e.getMessage());
            }
        }
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                aliPayService.refund(order.getOrderNo(), refundOrder.getRefundOrderNo(), refundOrder.getRefundReasonWapExplain(), refundPrice);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("支付宝申请退款失败！" + e.getMessage());
            }
        }

        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        orderDetail.setRefundNum(orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum());

        User user = userService.getById(order.getUid());
        refundOrder.setRefundTime(DateUtil.date());
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateById(orderDetail);
            refundOrderInfoService.updateById(refundOrderInfo);
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
                if (refundOrder.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // 更新用户金额
                    userService.updateNowMoney(order.getUid(), refundOrder.getRefundPrice(), Constants.OPERATION_TYPE_ADD);
                    // 用户余额记录
                    UserBalanceRecord userBalanceRecord = new UserBalanceRecord();
                    userBalanceRecord.setUid(user.getId());
                    userBalanceRecord.setLinkId(refundOrder.getRefundOrderNo());
                    userBalanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
                    userBalanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                    userBalanceRecord.setAmount(refundOrder.getRefundPrice());
                    userBalanceRecord.setBalance(user.getNowMoney().add(refundOrder.getRefundPrice()));
                    userBalanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_ORDER_REFUND, refundOrder.getRefundPrice()));
                    userBalanceRecordService.save(userBalanceRecord);
                }
            }
            updateById(refundOrder);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
            return Boolean.TRUE;
        });
        if (execute) {
            settingOrderStatus(order);
            orderService.updateById(order);
            // 积分、佣金、优惠券等放入后置task中处理
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
            }
        }
        return execute;
    }

    /**
     * 微信退款
     * @param order 订单
     * @param refundOrderNo 退款单号
     * @param refundPrice 退款金额
     * @param totalPrice 订单支付总金额
     */
    private void wxRefund(Order order, String refundOrderNo, BigDecimal refundPrice, BigDecimal totalPrice) {
        String appId = "";
        String mchId = "";
        String signKey = "";
        String path = "";
        switch (order.getPayChannel()) {
            case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
            case PayConstants.PAY_CHANNEL_H5:// H5使用公众号的信息
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PUBLIC_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_CERTIFICATE_PATH);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_CERTIFICATE_PATH);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                path = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_CERTIFICATE_PATH);
                break;
        }
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        //统一下单数据
        WxRefundVo wxRefundVo = new WxRefundVo();
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(WxPayUtil.getNonceStr());
        wxRefundVo.setOut_trade_no(order.getOutTradeNo());
        wxRefundVo.setOut_refund_no(refundOrderNo);
        wxRefundVo.setTotal_fee(totalPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(refundPrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setNotify_url(apiDomain + PayConstants.WX_PAY_REFUND_NOTIFY_API_URI);
        String sign = WxPayUtil.getSign(wxRefundVo, signKey);
        wxRefundVo.setSign(sign);
        wechatService.payRefund(wxRefundVo, path);
    }

    /**
     * 设置订单状态
     *
     * @param order       订单
     */
    private void settingOrderStatus(Order order) {
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        long count = orderDetailList.stream().filter(e -> e.getRefundNum().equals(0)).count();
        if (count == orderDetailList.size()) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_NORMAL);
            return;
        }
        OrderDetail orderDetail = orderDetailList.stream().filter(e -> e.getApplyRefundNum() > 0).findAny().orElse(null);
        if (ObjectUtil.isNotNull(orderDetail)) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_APPLY);
            return;
        }
        long refundCount = orderDetailList.stream().filter(e -> e.getRefundNum().equals(e.getPayNum())).count();
        if (refundCount == orderDetailList.size()) {
            order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_ALL);
            return;
        }
        order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_PORTION);
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING)) {
            long counted = orderDetailList.stream().filter(e -> e.getPayNum() > (e.getDeliveryNum() + e.getRefundNum())).count();
            if (counted <= 0) {
                order.setStatus(OrderConstants.ORDER_STATUS_WAIT_RECEIPT);
            }
        }
    }

    @Override
    public RefundOrder getInfoException(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        RefundOrder refundOrder = dao.selectOne(lqw);
        if (ObjectUtil.isNull(refundOrder)) {
            throw new CrmebException("退款单不存在");
        }
        return refundOrder;
    }

    /**
     * 退款订单列表
     * @param request 搜索参数
     * @return List
     */
    @Override
    public PageInfo<RefundOrderResponse> getH5List(OrderAfterSalesSearchRequest request) {
        Integer userId = userService.getUserId();
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("userId", userId);
        if (request.getType().equals(0)) {
            map.put("refundStatusStr", "0,2,4,5");
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", URLUtil.decode(request.getKeywords()));
        }
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<RefundOrderResponse> list = dao.findSearchList(map);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 退款订单详情（移动端）
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(String refundOrderNo) {
        RefundOrderInfoResponse response = dao.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));
        return response;
    }

    /**
     * 商户端退款单详情响应对象
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getMerchantDetail(String refundOrderNo) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (!admin.getMerId().equals(refundOrder.getMerId())) {
            throw new CrmebException("退款单不存在");
        }
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);

        RefundOrderAdminDetailResponse response = new RefundOrderAdminDetailResponse();
        response.setRefundOrderNo(refundOrder.getRefundOrderNo());
        response.setAfterSalesType(refundOrder.getAfterSalesType());
        response.setRefundStatus(refundOrder.getRefundStatus());
        response.setRefundPrice(refundOrder.getRefundPrice());
        response.setPayPrice(refundOrderInfo.getPayPrice());

        response.setProductName(refundOrderInfo.getProductName());
        response.setImage(refundOrderInfo.getImage());
        response.setSku(refundOrderInfo.getSku());
        response.setPrice(refundOrderInfo.getPrice());
        response.setRefundTime(refundOrder.getRefundTime());
        response.setPayNum(refundOrderInfo.getPayNum());
        response.setRefundUseIntegral(refundOrder.getRefundUseIntegral());
        response.setRefundGainIntegral(refundOrder.getRefundGainIntegral());
        response.setRefundFirstBrokerageFee(refundOrder.getRefundFirstBrokerageFee());
        response.setRefundSecondBrokerageFee(refundOrder.getRefundSecondBrokerageFee());
        response.setRefundFreightFee(refundOrder.getRefundFreightFee());
        response.setApplyRefundNum(refundOrder.getTotalNum());

        response.setReturnGoodsType(refundOrder.getReturnGoodsType());
        response.setRefundReasonWap(refundOrder.getRefundReasonWap());
        response.setRefundReasonWapImg(refundOrder.getRefundReasonWapExplain());
        response.setRefundReasonWapExplain(refundOrder.getRefundReasonWapExplain());
        response.setReceiver(refundOrder.getReceiver());
        response.setReceiverPhone(refundOrder.getReceiverPhone());
        response.setReceiverAddressDetail(refundOrder.getReceiverAddressDetail());
        response.setExpressName(refundOrder.getExpressName());
        response.setTrackingNumber(refundOrder.getTrackingNumber());
        response.setTelephone(refundOrder.getTelephone());

        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));

        RefundOrderDetailOrderInfoVo orderInfoVo = getRefundOrderDetailOrderInfo(refundOrder.getOrderNo());
        response.setOrderInfoVo(orderInfoVo);
        return response;
    }

    /**
     * 获取退款单详情订单信息部分
     *
     * @param orderNo 订单编号
     */
    private RefundOrderDetailOrderInfoVo getRefundOrderDetailOrderInfo(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderNo);
        RefundOrderDetailOrderInfoVo orderInfoVo = new RefundOrderDetailOrderInfoVo();
        orderInfoVo.setOrderNo(orderNo);
        orderInfoVo.setUid(order.getUid());
        User user = userService.getById(order.getUid());
        orderInfoVo.setNickname(user.getNickname());
        orderInfoVo.setPhone(user.getPhone());
        orderInfoVo.setStatus(order.getStatus());
        orderInfoVo.setTotalNum(order.getTotalNum());
        orderInfoVo.setPaid(order.getPaid());
        orderInfoVo.setPayType(order.getPayType());
        orderInfoVo.setPayTime(order.getPayTime());
        orderInfoVo.setCreateTime(order.getCreateTime());
        if (order.getStatus() <= OrderConstants.ORDER_STATUS_WAIT_SHIPPING) {// 未发货
            orderInfoVo.setDeliveryNum(0);
        } else {
            if (!merchantOrder.getIsSplitDelivery()) {
                orderInfoVo.setDeliveryNum(merchantOrder.getTotalNum());
            } else {
                List<OrderDetail> orderDetailList = orderDetailService.getShipmentByOrderNo(orderNo);
                int deliveryNum = orderDetailList.stream().mapToInt(OrderDetail::getDeliveryNum).sum();
                orderInfoVo.setDeliveryNum(deliveryNum);
            }
        }

        orderInfoVo.setProTotalPrice(order.getProTotalPrice());
        orderInfoVo.setPayPrice(order.getPayPrice());
        orderInfoVo.setMerCouponPrice(order.getMerCouponPrice());
        orderInfoVo.setPlatCouponPrice(order.getPlatCouponPrice());
        orderInfoVo.setIntegralPrice(order.getIntegralPrice());
        orderInfoVo.setPayPostage(order.getPayPostage());
        orderInfoVo.setUseIntegral(order.getUseIntegral());
        orderInfoVo.setGainIntegral(order.getGainIntegral());
        orderInfoVo.setUserRemark(merchantOrder.getUserRemark());
        orderInfoVo.setMerchantRemark(merchantOrder.getMerchantRemark());
        orderInfoVo.setShippingType(merchantOrder.getShippingType());
        orderInfoVo.setRealName(merchantOrder.getRealName());
        orderInfoVo.setUserPhone(merchantOrder.getUserPhone());
        orderInfoVo.setUserAddress(merchantOrder.getUserAddress());
        return orderInfoVo;
    }

    /**
     * 平台端退款订单分页列表
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PlatformRefundOrderPageResponse> getPlatformAdminPage(RefundOrderSearchRequest request) {
        Page<RefundOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        QueryWrapper<RefundOrder> wrapper = Wrappers.query();
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            wrapper.eq("mer_id", request.getMerId());
        }
        if (StrUtil.isNotEmpty(request.getRefundOrderNo())) {
            wrapper.eq("refund_order_no", request.getRefundOrderNo());
        }
        if (StrUtil.isNotEmpty(request.getOrderNo())) {
            wrapper.eq("order_no", request.getOrderNo());
        }
        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            getRequestTimeWhere(wrapper, request.getDateLimit());
        }
        getStatusWhere(wrapper, request.getRefundStatus());
        wrapper.orderByDesc("id");
        List<RefundOrder> refundOrderList = dao.selectList(wrapper);
        if (CollUtil.isEmpty(refundOrderList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<Integer> merIdList = refundOrderList.stream().map(RefundOrder::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        List<Integer> uidList = refundOrderList.stream().map(RefundOrder::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        List<PlatformRefundOrderPageResponse> responseList = refundOrderList.stream().map(order -> {
            PlatformRefundOrderPageResponse response = new PlatformRefundOrderPageResponse();
            response.setRefundOrderNo(order.getRefundOrderNo());
            response.setOrderNo(order.getOrderNo());
            response.setUid(order.getUid());
            response.setRefundStatus(order.getRefundStatus());
            response.setRefundPrice(order.getRefundPrice());
            response.setCreateTime(order.getCreateTime());
            response.setMerRemark(order.getMerRemark());
            response.setAfterSalesType(order.getAfterSalesType());
            response.setUserNickName(userMap.get(order.getUid()).getNickname());
            response.setMerName(merchantMap.get(order.getMerId()).getName());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取平台端退款订单各状态数量
     * @param dateLimit 时间参数
     * @return RefundOrderCountItemResponse
     */
    @Override
    public RefundOrderCountItemResponse getPlatformOrderStatusNum(String dateLimit) {
        return getOrderStatusNum(dateLimit, 0);
    }

    /**
     * 平台备注退款单
     * @param request 备注参数
     * @return Boolean
     */
    @Override
    public Boolean platformMark(RefundOrderRemarkRequest request) {
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        refundOrder.setPlatformRemark(request.getRemark());
        return updateById(refundOrder);
    }

    /**
     * 平台端退款订单详情
     * @param refundOrderNo 退款单号
     * @return 退款单详情
     */
    @Override
    public RefundOrderAdminDetailResponse getPlatformDetail(String refundOrderNo) {
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrderNo);

        RefundOrderAdminDetailResponse response = new RefundOrderAdminDetailResponse();
        response.setRefundOrderNo(refundOrder.getRefundOrderNo());
        response.setAfterSalesType(refundOrder.getAfterSalesType());
        response.setRefundStatus(refundOrder.getRefundStatus());
        response.setRefundPrice(refundOrder.getRefundPrice());
        response.setPayPrice(refundOrderInfo.getPayPrice());

        response.setProductName(refundOrderInfo.getProductName());
        response.setImage(refundOrderInfo.getImage());
        response.setSku(refundOrderInfo.getSku());
        response.setPrice(refundOrderInfo.getPrice());
        response.setRefundTime(refundOrder.getRefundTime());
        response.setPayNum(refundOrderInfo.getPayNum());
        response.setRefundUseIntegral(refundOrder.getRefundUseIntegral());
        response.setRefundGainIntegral(refundOrder.getRefundGainIntegral());
        response.setRefundFirstBrokerageFee(refundOrder.getRefundFirstBrokerageFee());
        response.setRefundSecondBrokerageFee(refundOrder.getRefundSecondBrokerageFee());
        response.setRefundFreightFee(refundOrder.getRefundFreightFee());
        response.setApplyRefundNum(refundOrder.getTotalNum());

        response.setReturnGoodsType(refundOrder.getReturnGoodsType());
        response.setRefundReasonWap(refundOrder.getRefundReasonWap());
        response.setRefundReasonWapImg(refundOrder.getRefundReasonWapExplain());
        response.setRefundReasonWapExplain(refundOrder.getRefundReasonWapExplain());
        response.setReceiver(refundOrder.getReceiver());
        response.setReceiverPhone(refundOrder.getReceiverPhone());
        response.setReceiverAddressDetail(refundOrder.getReceiverAddressDetail());
        response.setExpressName(refundOrder.getExpressName());
        response.setTrackingNumber(refundOrder.getTrackingNumber());
        response.setTelephone(refundOrder.getTelephone());

        response.setStatusList(refundOrderStatusService.findListByRefundOrderNo(refundOrderNo));

        RefundOrderDetailOrderInfoVo orderInfoVo = getRefundOrderDetailOrderInfo(refundOrder.getOrderNo());
        response.setOrderInfoVo(orderInfoVo);
        return response;
    }

    /**
     * 获取某一天的所有数据
     * @param merId 商户id，0为所有商户
     * @param date 日期：年-月-日
     * @return List
     */
    @Override
    public List<RefundOrder> findByDate(Integer merId, String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(refund_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取某一月的所有数据
     * @param merId 商户id，0为所有商户
     * @param month 日期：年-月
     * @return List
     */
    @Override
    public List<RefundOrder> findByMonth(Integer merId, String month) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(refund_time, '%Y-%m') = {0}", month);
        return dao.selectList(lqw);
    }

    /**
     * 根据日期获取退款订单数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRefundOrderNumByDate(String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getId);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 根据日期获取退款订单金额
     * @param date 日期
     * @return Integer
     */
    @Override
    public BigDecimal getRefundOrderAmountByDate(String date) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getRefundPrice);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
        lqw.apply("date_format(update_time, '%Y-%m-%d') = {0}", date);
        List<RefundOrder> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return BigDecimal.ZERO;
        }
        return orderList.stream().map(RefundOrder::getRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取退款中（申请）订单数量
     */
    @Override
    public Integer getRefundingCount(Integer userId) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getUid, userId);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
        return dao.selectCount(lqw);
    }

    /**
     * 获取退款单详情
     * @param refundOrderNo 退款单号
     */
    @Override
    public RefundOrder getByRefundOrderNo(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrder::getRefundOrderNo, refundOrderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 待退款订单数量
     * @return Integer
     */
    @Override
    public Integer getAwaitAuditNum(Integer merId) {
        LambdaQueryWrapper<RefundOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(RefundOrder::getId);
        lqw.eq(RefundOrder::getRefundStatus, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);
        if (merId > 0) {
            lqw.eq(RefundOrder::getMerId, merId);
        }
        return dao.selectCount(lqw);
    }

    /**
     * 撤销退款单
     * @param refundOrderNo 退款单号
     * todo 缺少退款单日志，缺少订单状态更新
     */
    @Override
    public Boolean revoke(String refundOrderNo) {
        RefundOrder refundOrder = getByRefundOrderNo(refundOrderNo);
        if (ObjectUtil.isNull(refundOrder)) {
            throw new CrmebException("退款单不存在");
        }
        if (refundOrder.getRefundStatus() != 0 || refundOrder.getRefundStatus() != 4) {
            throw new CrmebException("退款单状态异常");
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE);

        return updateById(refundOrder);
    }

    /**
     * 退款单审核
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    public Boolean audit(OrderRefundAuditRequest request) {
        validatedAuditRequest(request);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY)) {
            throw new CrmebException("售后单状态异常");
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的资源");
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        if (ObjectUtil.isNull(order)) {
            throw new CrmebException("退款单关联的订单不存在");
        }
        if (request.getAuditType().equals("refuse")) {
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT);
            refundOrder.setRefundReason(request.getReason());
            RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
            OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
            orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(refundOrder);
                orderDetailService.updateById(orderDetail);
                refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核拒绝");
                return Boolean.TRUE;
            });
            if (execute) {
                // 设置订单退款状态
                settingOrderStatus(order);
                orderService.updateById(order);
            }
            return execute;
        }
        if (refundOrder.getAfterSalesType().equals(2)) {
            if (ObjectUtil.isNull(request.getMerAddressId())) {
                throw new CrmebException("请选择商家退货地址");
            }
            MerchantAddress merchantAddress = merchantAddressService.getByIdException(request.getMerAddressId());
            refundOrder.setReceiver(merchantAddress.getReceiverName());
            refundOrder.setReceiverPhone(merchantAddress.getReceiverPhone());
            refundOrder.setReceiverAddressDetail(merchantAddress.getDetail());
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS);

            return transactionTemplate.execute(e -> {
                updateById(refundOrder);
                refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
                return Boolean.TRUE;
            });
        }
        return refundPrice(refundOrder, order);
    }

    /**
     * 退款单收到退货
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean receiving(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND)
            || refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE)) {
            throw new CrmebException("退款单状态异常");
        }
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }

        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        return refundPrice(refundOrder, order);
    }

    /**
     * 平台强制退款
     * @param refundOrderNo 退款单号
     */
    @Override
    public Boolean compulsoryRefund(String refundOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(refundOrderNo);
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }
        // todo 判断退款单状态
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING)) {
            throw new CrmebException("退款单状态异常");
        }
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        return refundPrice(refundOrder, order);
    }

    /**
     * 退款单-商家拒绝收货退款
     * @param request 拒绝收货请求对象
     * todo 后续逻辑、退款状态可能有变动
     */
    @Override
    public Boolean receivingReject(RejectReceivingRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        RefundOrder refundOrder = getInfoException(request.getRefundOrderNo());
        if (!refundOrder.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException("无法操作非自己商户的订单");
        }
        if (!refundOrder.getRefundStatus().equals(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING)) {
            throw new CrmebException("退款单状态异常");
        }
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT);
        refundOrder.setRefundReason(request.getReason());
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        Order order = orderService.getByOrderNo(refundOrder.getOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(refundOrder);
            orderDetailService.updateById(orderDetail);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核拒绝");
            return Boolean.TRUE;
        });
        if (execute) {
            // 设置订单退款状态
            settingOrderStatus(order);
            orderService.updateById(order);
        }
        return execute;
    }

    /**
     * 退款
     * @param refundOrder 退款单
     */
    private Boolean refundPrice(RefundOrder refundOrder, Order order) {
        RefundOrderInfo refundOrderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
        OrderDetail orderDetail = orderDetailService.getById(refundOrderInfo.getOrderDetailId());
        BigDecimal refundPrice;
        if (orderDetail.getPayNum().equals(refundOrderInfo.getApplyRefundNum())) {
            // sku整退
            refundPrice = orderDetail.getPayPrice();
            orderDetail.setRefundPrice(refundPrice);
            refundOrderInfo.setRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
                refundOrderInfo.setRefundUseIntegral(orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
                refundOrderInfo.setRefundGainIntegral(orderDetail.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee());
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else if (orderDetail.getPayNum() == (orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum())) { // sku分退
            refundPrice = orderDetail.getPayPrice().subtract(orderDetail.getRefundPrice());
            // sku最后一部分退款
            orderDetail.setRefundPrice(orderDetail.getPayPrice());
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);
            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral() - orderDetail.getRefundUseIntegral());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().subtract(orderDetail.getRefundIntegralPrice()));
                orderDetail.setRefundUseIntegral(orderDetail.getUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getIntegralPrice());
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral() - orderDetail.getRefundGainIntegral());
                orderDetail.setRefundGainIntegral(orderDetail.getGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().subtract(orderDetail.getRefundFirstBrokerageFee()));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee());
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().subtract(orderDetail.getRefundSecondBrokerageFee()));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee());
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().subtract(orderDetail.getRefundPlatCouponPrice()));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice());
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().subtract(orderDetail.getRefundFreightFee()));
                orderDetail.setRefundFreightFee(orderDetail.getFreightFee());
            }
        } else {
            // sku非最后一部分退款
            BigDecimal ratio = new BigDecimal(refundOrderInfo.getApplyRefundNum()).divide(new BigDecimal(orderDetail.getPayNum()), 10, BigDecimal.ROUND_HALF_UP);
            refundPrice = orderDetail.getPayPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderDetail.setRefundPrice(orderDetail.getRefundPrice().add(refundPrice));
            refundOrderInfo.setRefundPrice(refundPrice);
            refundOrderInfo.setMerchantRefundPrice(refundPrice);

            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                refundOrderInfo.setRefundIntegralPrice(orderDetail.getIntegralPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundUseIntegral(orderDetail.getRefundUseIntegral() + refundOrderInfo.getRefundUseIntegral());
                orderDetail.setRefundIntegralPrice(orderDetail.getRefundIntegralPrice().add(refundOrderInfo.getRefundIntegralPrice()));
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                orderDetail.setRefundGainIntegral(orderDetail.getRefundGainIntegral() + refundOrderInfo.getRefundGainIntegral());
            }
            if (orderDetail.getFirstBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFirstBrokerageFee(orderDetail.getFirstBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFirstBrokerageFee(orderDetail.getRefundFirstBrokerageFee().add(refundOrderInfo.getRefundFirstBrokerageFee()));
            }
            if (orderDetail.getSecondBrokerageFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundSecondBrokerageFee(orderDetail.getSecondBrokerageFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundSecondBrokerageFee(orderDetail.getRefundSecondBrokerageFee().add(refundOrderInfo.getRefundSecondBrokerageFee()));
            }
            if (orderDetail.getPlatCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundPlatCouponPrice(orderDetail.getPlatCouponPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundPlatCouponPrice(orderDetail.getRefundPlatCouponPrice().add(refundOrderInfo.getRefundPlatCouponPrice()));
            }
            if (orderDetail.getFreightFee().compareTo(BigDecimal.ZERO) > 0) {
                refundOrderInfo.setRefundFreightFee(orderDetail.getFreightFee().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
                orderDetail.setRefundFreightFee(orderDetail.getRefundFreightFee().add(refundOrderInfo.getRefundFreightFee()));
            }
        }

        refundOrder.setRefundPrice(refundPrice);
        refundOrder.setMerchantRefundPrice(refundOrderInfo.getMerchantRefundPrice());
        refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
        refundOrder.setRefundIntegralPrice(refundOrderInfo.getRefundIntegralPrice());
        refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());
        refundOrder.setRefundFirstBrokerageFee(refundOrderInfo.getRefundFirstBrokerageFee());
        refundOrder.setRefundSecondBrokerageFee(refundOrderInfo.getRefundSecondBrokerageFee());
        refundOrder.setRefundPayType(order.getPayType());
        refundOrder.setRefundPlatCouponPrice(refundOrderInfo.getRefundPlatCouponPrice());
        refundOrder.setRefundFreightFee(refundOrderInfo.getRefundFreightFee());
        //退款
        if (order.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                Order platOrder = orderService.getByOrderNo(order.getPlatOrderNo());
                wxRefund(order, refundOrder.getRefundOrderNo(), refundPrice, platOrder.getPayPrice());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！" + e.getMessage());
            }
        }
        if (order.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY) && refundPrice.compareTo(BigDecimal.ZERO) > 0) {
            try {
                aliPayService.refund(order.getOrderNo(), refundOrder.getRefundOrderNo(), refundOrder.getRefundReasonWapExplain(), refundPrice);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("支付宝申请退款失败！" + e.getMessage());
            }
        }

        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() - refundOrderInfo.getApplyRefundNum());
        orderDetail.setRefundNum(orderDetail.getRefundNum() + refundOrderInfo.getApplyRefundNum());

        User user = userService.getById(order.getUid());
        refundOrder.setRefundTime(DateUtil.date());
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateById(orderDetail);
            refundOrderInfoService.updateById(refundOrderInfo);
            refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING);
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND);
                if (refundOrder.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // 更新用户金额
                    userService.updateNowMoney(order.getUid(), refundOrder.getRefundPrice(), Constants.OPERATION_TYPE_ADD);
                    // 用户余额记录
                    UserBalanceRecord userBalanceRecord = new UserBalanceRecord();
                    userBalanceRecord.setUid(user.getId());
                    userBalanceRecord.setLinkId(refundOrder.getRefundOrderNo());
                    userBalanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
                    userBalanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                    userBalanceRecord.setAmount(refundOrder.getRefundPrice());
                    userBalanceRecord.setBalance(user.getNowMoney().add(refundOrder.getRefundPrice()));
                    userBalanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_ORDER_REFUND, refundOrder.getRefundPrice()));
                    userBalanceRecordService.save(userBalanceRecord);
                }
            }
            updateById(refundOrder);
            refundOrderStatusService.add(refundOrder.getRefundOrderNo(), RefundOrderConstants.REFUND_ORDER_LOG_AUDIT, "售后单商家审核通过");
            return Boolean.TRUE;
        });
        if (execute) {
            settingOrderStatus(order);
            orderService.updateById(order);
            // 积分、佣金、优惠券等放入后置task中处理
            if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, refundOrder.getRefundOrderNo());
            }
        }
        return execute;
    }

    /**
     * 校验审核参数
     */
    private void validatedAuditRequest(OrderRefundAuditRequest request) {
        if (request.getAuditType().equals("refuse")) {
            if (StrUtil.isEmpty(request.getReason())) {
                throw new CrmebException("请填写拒绝退款原因");
            }
        }
    }

    /**
     * 获取退款订单各状态数量
     *
     * @param dateLimit 时间参数
     * @param merId     商户id，平台为0
     * @return RefundOrderCountItemResponse
     */
    private RefundOrderCountItemResponse getOrderStatusNum(String dateLimit, Integer merId) {
        RefundOrderCountItemResponse response = new RefundOrderCountItemResponse();
        // 全部订单
        response.setAll(getCount(dateLimit, 9, merId));
        // 待审核
        response.setAwait(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY, merId));
        // 审核拒绝
        response.setReject(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REJECT, merId));
        // 退款中
        response.setRefunding(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUNDING, merId));
        // 已退款
        response.setRefunded(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REFUND, merId));
        response.setAwaitReturning(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS, merId));
        response.setAwaitReceiving(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING, merId));
        response.setRevoke(getCount(dateLimit, OrderConstants.MERCHANT_REFUND_ORDER_STATUS_REVOKE, merId));
        return response;
    }

    /**
     * 获取订单总数
     *
     * @param dateLimit 时间端
     * @param status    String 状态
     * @return Integer
     */
    private Integer getCount(String dateLimit, Integer status, Integer merId) {
        //总数只计算时间
        QueryWrapper<RefundOrder> queryWrapper = new QueryWrapper<>();
        if (merId > 0) {
            queryWrapper.eq("mer_id", merId);
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            getRequestTimeWhere(queryWrapper, dateLimit);
        }
        getStatusWhere(queryWrapper, status);
        return dao.selectCount(queryWrapper);
    }

    /**
     * 获取request的where条件
     *
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param dateLimit    时间区间参数
     */
    private void getRequestTimeWhere(QueryWrapper<RefundOrder> queryWrapper, String dateLimit) {
        DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(dateLimit);
        queryWrapper.between("create_time", dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
    }

    /**
     * 根据订单状态获取where条件
     *
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param status       Integer 类型 9-全部
     */
    private void getStatusWhere(QueryWrapper<RefundOrder> queryWrapper, Integer status) {
        if (ObjectUtil.isNull(status)) {
            return;
        }
        if (status == 9) {
            return;
        }
        queryWrapper.eq("refund_status", status);
    }
}

