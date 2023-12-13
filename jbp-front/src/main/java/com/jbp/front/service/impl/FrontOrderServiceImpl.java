package com.jbp.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jbp.front.service.FrontOrderService;
import com.jbp.common.constants.*;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.cat.Cart;
import com.jbp.common.model.coupon.CouponProduct;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.model.express.ShippingTemplates;
import com.jbp.common.model.express.ShippingTemplatesFree;
import com.jbp.common.model.express.ShippingTemplatesRegion;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.*;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductAttrValue;
import com.jbp.common.model.product.ProductReply;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserAddress;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.model.wechat.video.PayComponentProduct;
import com.jbp.common.model.wechat.video.PayComponentProductSku;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.*;
import com.jbp.common.response.*;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.vo.*;
import com.jbp.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * H5端订单操作
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
public class FrontOrderServiceImpl implements FrontOrderService {

    private final Logger logger = LoggerFactory.getLogger(FrontOrderServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ShippingTemplatesService shippingTemplatesService;
    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;
    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private LogisticService logisticService;
    @Autowired
    private PayComponentProductService payComponentProductService;
    @Autowired
    private PayComponentProductSkuService payComponentProductSkuService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;

    /**
     * 订单预下单
     *
     * @param request 预下单请求参数
     * @return PreOrderResponse
     */
    @Override
    public OrderNoResponse preOrder(PreOrderRequest request) {
        logger.info("preOrder:{}", JSON.toJSONString(request));
        User user = userService.getInfo();
        // 校验预下单商品信息
        PreOrderInfoVo preOrderInfoVo = validatePreOrderRequest(request, user);
        logger.info("preOrder 预下单 检查后:{} ", JSON.toJSONString(preOrderInfoVo));
        List<PreOrderInfoDetailVo> orderInfoList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : preOrderInfoVo.getMerchantOrderVoList()) {
            orderInfoList.addAll(merchantOrderVo.getOrderInfoList());
            BigDecimal merTotalPrice = merchantOrderVo.getOrderInfoList().stream().map(e -> e.getPrice().multiply(new BigDecimal(e.getPayNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrderVo.setProTotalFee(merTotalPrice);
            merchantOrderVo.setProTotalNum(merchantOrderVo.getOrderInfoList().stream().mapToInt(PreOrderInfoDetailVo::getPayNum).sum());
        }
        // 商品总计金额
        BigDecimal totalPrice = orderInfoList.stream().map(e -> e.getPrice().multiply(new BigDecimal(e.getPayNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        preOrderInfoVo.setProTotalFee(totalPrice);
        // 购买商品总数量
        int orderProNum = orderInfoList.stream().mapToInt(PreOrderInfoDetailVo::getPayNum).sum();
        preOrderInfoVo.setOrderProNum(orderProNum);
        // 获取默认地址
        UserAddress userAddress = userAddressService.getDefaultByUid(user.getId());
        if (ObjectUtil.isNotNull(userAddress)) {
            // 计算运费
            getFreightFee(preOrderInfoVo, userAddress);
            preOrderInfoVo.setAddressId(userAddress.getId());
        } else {
            preOrderInfoVo.setFreightFee(BigDecimal.ZERO);
            preOrderInfoVo.setAddressId(0);
        }
        // 实际支付金额
        preOrderInfoVo.setPayFee(preOrderInfoVo.getProTotalFee().add(preOrderInfoVo.getFreightFee()));
        preOrderInfoVo.setUserIntegral(user.getIntegral());
        preOrderInfoVo.setUserBalance(user.getNowMoney());
        preOrderInfoVo.setIntegralDeductionSwitch(false);
        preOrderInfoVo.setIsUseIntegral(false);
        String integralDeductionSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        if ("true".equals(integralDeductionSwitch) && preOrderInfoVo.getProTotalFee().compareTo(new BigDecimal(integralDeductionStartMoney)) >= 0) {
            preOrderInfoVo.setIntegralDeductionSwitch(true);
        }
        // 缓存订单
        String key = user.getId() + CrmebDateUtil.getNowTime().toString() + CrmebUtil.getUuid();
        redisUtil.set(OrderConstants.PRE_ORDER_CACHE_PREFIX + key, JSONObject.toJSONString(preOrderInfoVo), OrderConstants.PRE_ORDER_CACHE_TIME, TimeUnit.MINUTES);
        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(key);
        logger.info("preOrder response:{}", JSON.toJSONString(response));
        return response;
    }

    /**
     * 加载预下单信息
     *
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    @Override
    public PreOrderResponse loadPreOrder(String preOrderNo) {
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + preOrderNo;
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }
        String orderVoString = redisUtil.get(key);
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        PreOrderResponse preOrderResponse = new PreOrderResponse();
        preOrderResponse.setOrderInfoVo(orderInfoVo);
        return preOrderResponse;
    }

    /**
     * 计算订单价格
     *
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    @Override
    public ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request) {
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + request.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }
        List<OrderMerchantRequest> orderMerchantRequestList = request.getOrderMerchantRequestList();
        if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP))) {
            orderMerchantRequestList.forEach(m -> {
                if (m.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                    Merchant merchant = merchantService.getByIdException(m.getMerId());
                    if (!merchant.getIsTakeTheir()) {
                        throw new CrmebException("请先联系商户管理员开启门店自提");
                    }
                }
            });
        }

        String orderVoString = redisUtil.get(key).toString();
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);
        User user = userService.getInfo();
//        redisUtil.set(OrderConstants.PRE_ORDER_CACHE_PREFIX + key, JSONObject.toJSONString(preOrderInfoVo), OrderConstants.PRE_ORDER_CACHE_TIME, TimeUnit.MINUTES);
        return computedPrice(request, orderInfoVo, user);
    }

    /**
     * 创建订单
     *
     * @param orderRequest 创建订单请求参数
     * @return OrderNoResponse 订单编号
     */
    @Override
    public OrderNoResponse createOrder(CreateOrderRequest orderRequest) {
        User user = userService.getInfo();
        // 通过缓存获取预下单对象
        String key = OrderConstants.PRE_ORDER_CACHE_PREFIX + orderRequest.getPreOrderNo();
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }
        UserAddress userAddress = null;
        List<OrderMerchantRequest> orderMerchantRequestList = orderRequest.getOrderMerchantRequestList();
        if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS))) {
            if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                throw new CrmebException("请选择收货地址");
            }
            userAddress = userAddressService.getById(orderRequest.getAddressId());
            if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                throw new CrmebException("收货地址有误");
            }
        }
        if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP))) {
            orderMerchantRequestList.forEach(m -> {
                if (m.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                    Merchant merchant = merchantService.getByIdException(m.getMerId());
                    if (!merchant.getIsTakeTheir()) {
                        throw new CrmebException("请先联系商户管理员开启门店自提");
                    }
                }
            });
        }

        String orderVoString = redisUtil.get(key).toString();
        PreOrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, PreOrderInfoVo.class);

        // 校验商品库存
        List<MyRecord> skuRecordList = validateProductStock(orderInfoVo);

        // 计算订单各种价格
        getFreightFee(orderInfoVo, userAddress);
        getCouponFee(orderInfoVo, orderMerchantRequestList, user.getId());
        if (orderRequest.getIsUseIntegral() && user.getIntegral() > 0) {// 使用积分
            integralDeductionComputed(orderInfoVo, user.getIntegral());
        }
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();

        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVoList.stream().mapToInt(PreMerchantOrderVo::getUseIntegral).sum());
        order.setIntegralPrice(merchantOrderVoList.stream().map(PreMerchantOrderVo::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setPayPrice(order.getProTotalPrice().add(order.getTotalPostage()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(OrderConstants.ORDER_TYPE_NORMAL);// 默认普通订单
        if (orderInfoVo.getIsVideo()) {
            order.setType(OrderConstants.ORDER_TYPE_VIDEO);// 视频号订单
        }

        // 商户订单
        List<Integer> couponIdList = CollUtil.newArrayList();
        List<MerchantOrder> merchantOrderList = new ArrayList<>();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            MerchantOrder merchantOrder = new MerchantOrder();
            merchantOrder.setOrderNo(order.getOrderNo());
            merchantOrder.setMerId(merchantOrderVo.getMerId());
            merchantOrder.setUid(user.getId());
            for (OrderMerchantRequest om : orderMerchantRequestList) {
                if (om.getMerId().equals(merchantOrderVo.getMerId())) {
                    if (StrUtil.isNotBlank(om.getRemark())) {
                        merchantOrder.setUserRemark(om.getRemark());
                    }
                    merchantOrder.setShippingType(om.getShippingType());
                    break;
                }
            }
            if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrder.setUserAddress(merchantOrderVo.getMerName());
                merchantOrder.setVerifyCode(String.valueOf(CrmebUtil.randomCount(1111111111, 999999999)));
            } else {
                merchantOrder.setRealName(userAddress.getRealName());
                merchantOrder.setUserPhone(userAddress.getPhone());
                String userAddressStr = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getStreet() + userAddress.getDetail();
                merchantOrder.setUserAddress(userAddressStr);
            }
            merchantOrder.setTotalNum(merchantOrderVo.getProTotalNum());
            merchantOrder.setProTotalPrice(merchantOrderVo.getProTotalFee());
            merchantOrder.setTotalPostage(merchantOrderVo.getFreightFee());
            merchantOrder.setTotalPrice(merchantOrder.getProTotalPrice().add(merchantOrder.getTotalPostage()));
            merchantOrder.setPayPostage(merchantOrder.getTotalPostage());
            merchantOrder.setUseIntegral(merchantOrderVo.getUseIntegral());
            merchantOrder.setIntegralPrice(merchantOrderVo.getIntegralPrice());
            merchantOrder.setCouponId(merchantOrderVo.getUserCouponId());
            if (merchantOrder.getCouponId() > 0) {
                couponIdList.add(merchantOrder.getCouponId());
            }
            merchantOrder.setCouponPrice(merchantOrderVo.getCouponFee());
            merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
            merchantOrder.setGainIntegral(0);
            merchantOrder.setType(OrderConstants.ORDER_TYPE_NORMAL);
            merchantOrderList.add(merchantOrder);

            List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList();
            for (PreOrderInfoDetailVo detailVo : detailVoList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderNo(order.getOrderNo());
                orderDetail.setMerId(merchantOrder.getMerId());
                orderDetail.setUid(user.getId());
                orderDetail.setProductId(detailVo.getProductId());
                orderDetail.setProductName(detailVo.getProductName());
                orderDetail.setImage(detailVo.getImage());
                orderDetail.setAttrValueId(detailVo.getAttrValueId());
                orderDetail.setSku(detailVo.getSku());
                orderDetail.setPrice(detailVo.getPrice());
                orderDetail.setPayNum(detailVo.getPayNum());
                orderDetail.setWeight(detailVo.getWeight());
                orderDetail.setVolume(detailVo.getVolume());
                orderDetail.setProductType(detailVo.getProductType());
                orderDetail.setSubBrokerageType(detailVo.getSubBrokerageType());
                orderDetail.setBrokerage(detailVo.getBrokerage());
                orderDetail.setBrokerageTwo(detailVo.getBrokerageTwo());
                orderDetail.setFreightFee(detailVo.getFreightFee());
                orderDetail.setCouponPrice(detailVo.getCouponPrice());
                orderDetail.setUseIntegral(detailVo.getUseIntegral());
                orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
                orderDetail.setPayPrice(BigDecimal.ZERO);
                BigDecimal detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
                if (detailPayPrice.compareTo(BigDecimal.ZERO) >= 0) {
                    orderDetail.setPayPrice(detailPayPrice);
                }
                orderDetailList.add(orderDetail);
            }
        }

        logger.error("订单生成：商户订单列表： " + JSON.toJSONString(merchantOrderList));
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean result = true;
            logger.info("开始扣件商品库存:order:{}", JSON.toJSONString(order));
            if(order.getType().equals(0)){ // 普通商品
                logger.info("开始扣件商品库存 --> 普通商品:{}", JSON.toJSONString(skuRecordList));
                // 扣减库存
                for (MyRecord skuRecord : skuRecordList) {
                     // 普通商品口库存
                    result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                        return result;
                    }
                    // 普通商品规格扣库存
                    result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_NORMAL, skuRecord.getInt("attrValueVersion"));
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                        return result;
                    }
                }
            }else if(order.getType().equals(1)) {// 视频号订单
                logger.info("开始扣件商品库存 --> 视频号商品:{}", JSON.toJSONString(skuRecordList));
                MyRecord skuRecord = skuRecordList.get(0);
                // 商品规格表扣库存
                productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_COMPONENT, skuRecord.getInt("attrValueVersion"));
                // 视频商品规格表扣库存
                payComponentProductSkuService.operationStock(skuRecord.getInt("skuId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("skuVersion"));
                // 视频号商品扣库存
                payComponentProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
            }


            orderService.save(order);
            merchantOrderService.saveBatch(merchantOrderList);
            orderDetailService.saveBatch(orderDetailList);
            // 扣除用户积分
            if (order.getUseIntegral() > 0) {
                result  = userService.updateIntegral(user.getId(), order.getUseIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    logger.error("生成订单扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                    return result;
                }
                UserIntegralRecord userIntegralRecord = initOrderUseIntegral(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }

            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.useBatch(couponIdList);
            }
            // 生成订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
            // 清除购物车数据
            if (CollUtil.isNotEmpty(orderInfoVo.getCartIdList())) {
                cartService.deleteCartByIds(orderInfoVo.getCartIdList());
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        // 删除缓存订单
        if (redisUtil.exists(key)) {
            redisUtil.delete(key);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY, order.getOrderNo());

        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }

    /**
     * 用户积分记录——订单抵扣
     * @param uid 用户ID
     * @param useIntegral 使用的积分
     * @param integral 用户当前积分
     * @param orderNo 订单号
     * @return 用户积分记录
     */
    private UserIntegralRecord initOrderUseIntegral(Integer uid, Integer useIntegral, Integer integral, String orderNo) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(uid);
        integralRecord.setLinkId(orderNo);
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(StrUtil.format("订单使用{}积分进行金额抵扣", useIntegral));
        integralRecord.setIntegral(useIntegral);
        integralRecord.setBalance(integral - useIntegral);
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    /**
     * 积分抵扣计算
     *
     * @param orderInfoVo  订单vo
     * @param userIntegral 用户积分
     */
    private void integralDeductionComputed(PreOrderInfoVo orderInfoVo, Integer userIntegral) {
        BigDecimal payPrice = orderInfoVo.getProTotalFee().subtract(orderInfoVo.getCouponFee());
        String integralDeductionSwitch = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        if (integralDeductionSwitch.equals("false")) {
            throw new CrmebException("积分抵扣未开启，请重新下单");
        }
        String integralDeductionStartMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        if (Integer.parseInt(integralDeductionStartMoney) <= 0 || payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) < 0) {
            throw new CrmebException("支付金额不满足积分抵扣起始金额，请重新下单");
        }
        // 查询积分使用比例
        String integralRatio = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
        // 可抵扣金额
        BigDecimal canDeductionPrice = payPrice.multiply(new BigDecimal(integralRatio)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
        // 积分转换金额
        String integralDeductionMoney = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
        BigDecimal deductionPrice = new BigDecimal(userIntegral.toString()).multiply(new BigDecimal(integralDeductionMoney));
        // 积分兑换金额小于实际支付可抵扣金额
        int useIntegral;
        BigDecimal integralDeductionPrice;
        if (deductionPrice.compareTo(canDeductionPrice) <= 0) {
            useIntegral = userIntegral;
            integralDeductionPrice = deductionPrice;
        } else {
            useIntegral = canDeductionPrice.divide(new BigDecimal(integralDeductionMoney), 0, BigDecimal.ROUND_UP).intValue();
            integralDeductionPrice = canDeductionPrice;
        }
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        Integer tempUseIntegral = useIntegral;
        BigDecimal tempIntegralDeductionPrice = integralDeductionPrice;
        List<PreMerchantOrderVo> voList = merchantOrderVoList.stream().sorted(Comparator.comparing(o -> (o.getProTotalFee().subtract(o.getCouponFee())))).collect(Collectors.toList());
        for (int i = 0; i < voList.size(); i++) {
            PreMerchantOrderVo merchantOrderVo = voList.get(i);
            BigDecimal merPayPrice = merchantOrderVo.getProTotalFee().subtract(merchantOrderVo.getCouponFee());
            if (merchantOrderVoList.size() == (i + 1)) {
                merchantOrderVo.setUseIntegral(tempUseIntegral);
                merchantOrderVo.setIntegralPrice(tempIntegralDeductionPrice);
                Integer merUseIntegral = tempUseIntegral;
                BigDecimal merIntegralDeductionPrice = tempIntegralDeductionPrice;
                List<PreOrderInfoDetailVo> orderInfoList = merchantOrderVo.getOrderInfoList();
                if (orderInfoList.size() == 1) {
                    orderInfoList.get(0).setUseIntegral(merUseIntegral);
                    orderInfoList.get(0).setIntegralPrice(merIntegralDeductionPrice);
                } else {
                    for (int j = 0; j < orderInfoList.size(); j++) {
                        PreOrderInfoDetailVo detailVo = orderInfoList.get(j);
                        if (orderInfoList.size() == (j + 1)) {
                            detailVo.setUseIntegral(merUseIntegral);
                            detailVo.setIntegralPrice(merIntegralDeductionPrice);
                            break;
                        }
                        BigDecimal detailPayPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getCouponPrice());
                        BigDecimal ratio = detailPayPrice.divide(merPayPrice, 10, BigDecimal.ROUND_HALF_UP);
                        int detailUseIntegral = new BigDecimal(merUseIntegral.toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                        BigDecimal detailIntegralPrice = new BigDecimal(detailUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
                        merUseIntegral = merUseIntegral - detailUseIntegral;
                        merIntegralDeductionPrice = merIntegralDeductionPrice.subtract(detailIntegralPrice);
                        detailVo.setUseIntegral(detailUseIntegral);
                        detailVo.setIntegralPrice(detailIntegralPrice);
                    }
                }
                break;
            }
            BigDecimal payRatio = merPayPrice.divide(payPrice, 10, BigDecimal.ROUND_HALF_UP);
            Integer merUseIntegral = new BigDecimal(tempUseIntegral.toString()).multiply(payRatio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
            if (merUseIntegral.equals(0)) {
                continue;
            }
            BigDecimal merIntegralDeductionPrice = new BigDecimal(merUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
            merchantOrderVo.setUseIntegral(merUseIntegral > tempUseIntegral ? tempUseIntegral : merUseIntegral);
            merchantOrderVo.setIntegralPrice(merIntegralDeductionPrice.compareTo(tempIntegralDeductionPrice) > 0 ? tempIntegralDeductionPrice : merIntegralDeductionPrice);
            if (merIntegralDeductionPrice.compareTo(merPayPrice) > 0) {
                merchantOrderVo.setIntegralPrice(merPayPrice);
            }
            List<PreOrderInfoDetailVo> orderInfoList = merchantOrderVo.getOrderInfoList();
            if (orderInfoList.size() == 1) {
                orderInfoList.get(0).setUseIntegral(merUseIntegral);
                orderInfoList.get(0).setIntegralPrice(merIntegralDeductionPrice);
            } else {
                for (int j = 0; j < orderInfoList.size(); j++) {
                    PreOrderInfoDetailVo detailVo = orderInfoList.get(j);
                    if (orderInfoList.size() == (j + 1)) {
                        detailVo.setUseIntegral(merUseIntegral);
                        detailVo.setIntegralPrice(merIntegralDeductionPrice);
                        break;
                    }
                    BigDecimal detailPayPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum())).subtract(detailVo.getCouponPrice());
                    BigDecimal ratio = detailPayPrice.divide(merPayPrice, 10, BigDecimal.ROUND_HALF_UP);
                    int detailUseIntegral = new BigDecimal(merUseIntegral.toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                    BigDecimal detailIntegralPrice = new BigDecimal(detailUseIntegral).multiply(new BigDecimal(integralDeductionMoney));
                    merUseIntegral = merUseIntegral - detailUseIntegral;
                    merIntegralDeductionPrice = merIntegralDeductionPrice.subtract(detailIntegralPrice);
                    detailVo.setUseIntegral(detailUseIntegral);
                    detailVo.setIntegralPrice(detailIntegralPrice);
                }
            }
            tempUseIntegral = tempUseIntegral - merchantOrderVo.getUseIntegral();
            tempIntegralDeductionPrice = tempIntegralDeductionPrice.subtract(merchantOrderVo.getIntegralPrice());
        }
        orderInfoVo.setMerchantOrderVoList(voList);
    }

    /**
     * 订单列表
     *
     * @param status      订单状态|-1=全部,0=待支付,1=待发货,2=待收货
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderFrontDataResponse> list(Integer status, PageParamRequest pageRequest) {
        Integer userId = userService.getUserIdException();

        PageInfo<Order> pageInfo = orderService.getUserOrderList(userId, status, pageRequest);
        List<Order> orderList = pageInfo.getList();
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = orderList.stream().map(Order::getMerId).filter(i -> i > 0).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = null;
        if (CollUtil.isNotEmpty(merIdList)) {
            merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        }
        List<OrderFrontDataResponse> responseList = CollUtil.newArrayList();
        for (Order order : orderList) {
            OrderFrontDataResponse infoResponse = new OrderFrontDataResponse();
            BeanUtils.copyProperties(order, infoResponse);
            // 订单详情对象列表
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<OrderInfoFrontDataResponse> infoResponseList = CollUtil.newArrayList();
            orderDetailList.forEach(e -> {
                OrderInfoFrontDataResponse orderInfoResponse = new OrderInfoFrontDataResponse();
                BeanUtils.copyProperties(e, orderInfoResponse);
                infoResponseList.add(orderInfoResponse);
            });
            infoResponse.setOrderInfoList(infoResponseList);
            if (order.getMerId() > 0) {
                infoResponse.setMerName(merchantMap.get(order.getMerId()).getName());
            }
            responseList.add(infoResponse);
        }
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 移动端订单详情
     *
     * @param orderNo 订单编号
     * @return OrderFrontDetailResponse
     */
    @Override
    public OrderFrontDetailResponse frontDetail(String orderNo) {
        User currentUser = userService.getInfo();
        Order order = orderService.getByOrderNo(orderNo);
        if (order.getIsUserDel() || order.getIsMerchantDel() || !order.getUid().equals(currentUser.getId())) {
            throw new CrmebException("订单不存在");
        }
        OrderFrontDetailResponse response = new OrderFrontDetailResponse();
        BeanUtils.copyProperties(order, response);
        List<MerchantOrder> merchantOrderList = merchantOrderService.getByOrderNo(orderNo);
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderNo);
        Map<Integer, List<OrderDetail>> orderDetailMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail::getMerId));
        List<MerchantOrderFrontDetailResponse> merDetailResponseList = CollUtil.newArrayList();
        for (MerchantOrder merchantOrder : merchantOrderList) {
            MerchantOrderFrontDetailResponse merDetailResponse = new MerchantOrderFrontDetailResponse();
            BeanUtils.copyProperties(merchantOrder, merDetailResponse);
            Merchant merchant = merchantService.getById(merchantOrder.getMerId());
            merDetailResponse.setMerName(merchant.getName());
            if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merDetailResponse.setMerPhone(merchant.getPhone());
                merDetailResponse.setMerProvince(merchant.getProvince());
                merDetailResponse.setMerCity(merchant.getCity());
                merDetailResponse.setMerDistrict(merchant.getDistrict());
                merDetailResponse.setMerAddressDetail(merchant.getAddressDetail());
                merDetailResponse.setMerLatitude(merchant.getLatitude());
                merDetailResponse.setMerLongitude(merchant.getLongitude());
            }
            List<OrderDetail> detailList = orderDetailMap.get(merchantOrder.getMerId());
            List<OrderInfoFrontDataResponse> dataResponseList = detailList.stream().map(d -> {
                OrderInfoFrontDataResponse dataResponse = new OrderInfoFrontDataResponse();
                BeanUtils.copyProperties(d, dataResponse);
                return dataResponse;
            }).collect(Collectors.toList());
            merDetailResponse.setOrderInfoList(dataResponseList);
            merDetailResponseList.add(merDetailResponse);
        }
        response.setMerchantOrderList(merDetailResponseList);
        return response;
    }

    /**
     * 订单商品评论列表
     *
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<InfoReplyResponse> replyList(PageParamRequest pageRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.getReplyList(userId, false, pageRequest);
        List<OrderDetail> orderDetailList = pageInfo.getList();
        if (CollUtil.isEmpty(orderDetailList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = orderDetailList.stream().map(OrderDetail::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        List<InfoReplyResponse> responseList = orderDetailList.stream().map(info -> {
            InfoReplyResponse replyResponse = new InfoReplyResponse();
            BeanUtils.copyProperties(info, replyResponse);
            replyResponse.setMerName(merchantMap.get(info.getMerId()).getName());
            return replyResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 评价订单商品
     *
     * @param request 评价参数
     */
    @Override
    public Boolean replyProduct(OrderProductReplyRequest request) {
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE) || order.getStatus().equals(OrderConstants.ORDER_STATUS_TAKE_DELIVERY))) {
            throw new CrmebException("订单状态异常，无法评价");
        }
        OrderDetail orderDetail = orderDetailService.getById(request.getOrderDetailId());
        if (ObjectUtil.isNull(orderDetail) || !order.getOrderNo().equals(orderDetail.getOrderNo())) {
            throw new CrmebException("订单详情不存在");
        }
        if (!orderDetail.getIsReceipt()) {
            throw new CrmebException("请收货后再评论");
        }
        if (orderDetail.getIsReply()) {
            throw new CrmebException("已评价!");
        }
        orderDetail.setIsReply(true);
        User user = userService.getInfo();
        ProductReply productReply = new ProductReply();
        BeanUtils.copyProperties(request, productReply);
        productReply.setMerId(orderDetail.getMerId());
        productReply.setProductId(orderDetail.getProductId());
        productReply.setAttrValueId(orderDetail.getAttrValueId());
        productReply.setSku(orderDetail.getSku());
        productReply.setUid(user.getId());
        productReply.setNickname(user.getNickname());
        String cdnUrl = systemAttachmentService.getCdnUrl();
        productReply.setAvatar(systemAttachmentService.clearPrefix(user.getAvatar(), cdnUrl));
        if (CollUtil.isNotEmpty(request.getPics())) {
            List<String> pics = request.getPics().stream().map(e -> systemAttachmentService.clearPrefix(e, cdnUrl)).collect(Collectors.toList());
            productReply.setPics(String.join(",", pics));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            orderDetailService.updateById(orderDetail);
            productReplyService.save(productReply);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("评价订单商品失败");
        }
        return execute;
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public Boolean cancel(String orderNo) {
        Integer uid = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(uid)) {
            throw new CrmebException("订单不存在");
        }
        if (order.getPaid()) {
            throw new CrmebException("订单已支付，无法取消");
        }
        Boolean cancel = orderService.cancel(orderNo, true);
        if (cancel) {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER, order.getOrderNo());
        }
        return cancel;
    }

    /**
     * 订单收货
     *
     * @param orderNo 订单号
     * @return Boolean
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        Integer userId = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(userId)) {
            throw new CrmebException("订单不存在");
        }
        if (!order.getCancelStatus().equals(OrderConstants.ORDER_CANCEL_STATUS_NORMAL)) {
            throw new CrmebException("订单已取消");
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException("已退款订单无法收货");
        }
        if (!order.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_RECEIPT)) {
            throw new CrmebException("订单状态异常");
        }
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.takeDelivery(orderNo);
            orderDetailService.takeDelivery(orderNo);
            return Boolean.TRUE;
        });
        if (execute) {
            //后续操作放入redis
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, orderNo);
        }
        return execute;
    }

    /**
     * 删除订单
     *
     * @param orderNo 订单号
     * @return Boolean
     */
    @Override
    public Boolean delete(String orderNo) {
        Integer userId = userService.getUserIdException();
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUid().equals(userId)) {
            throw new CrmebException("订单不存在");
        }
        if (!(order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE) || order.getStatus().equals(OrderConstants.ORDER_STATUS_CANCEL))) {
            throw new CrmebException("未完成订单无法删除");
        }
        if (order.getIsUserDel()) {
            throw new CrmebException("订单已删除，请不要进行重复操作");
        }
        order.setIsUserDel(true);
        return transactionTemplate.execute(e -> {
            orderService.updateById(order);
            orderStatusService.createLog(orderNo, OrderStatusConstants.ORDER_STATUS_USER_DELETE, OrderStatusConstants.ORDER_LOG_USER_DELETE);
            return Boolean.TRUE;
        });
    }

    /**
     * 售后申请列表(可申请售后列表)
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<OrderDetail> getAfterSaleApplyList(String orderNo, PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.findAfterSaleApplyList(uid, orderNo, pageParamRequest);
        List<OrderDetail> orderDetailList = pageInfo.getList();
        if (CollUtil.isEmpty(orderDetailList)) {
            return pageInfo;
        }
        List<Integer> merIdList = orderDetailList.stream().map(OrderDetail::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        orderDetailList.forEach(o -> {
            o.setMerName(merchantMap.get(o.getMerId()).getName());
        });
        return pageInfo;
    }

    /**
     * 查询退款理由
     *
     * @return 退款理由集合
     */
    @Override
    public List<String> getRefundReason() {
        String reasonString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STOR_REASON);
        reasonString = CrmebUtil.UnicodeToCN(reasonString);
        reasonString = reasonString.replace("rn", "n");
        return Arrays.asList(reasonString.split("\\n"));
    }

    /**
     * 订单退款申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean refundApply(OrderRefundApplyRequest request) {
        Integer uid = userService.getUserIdException();
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (!order.getUid().equals(uid) || order.getIsUserDel()) {
            throw new CrmebException("订单不存在");
        }
        if (order.getCancelStatus() > OrderConstants.ORDER_CANCEL_STATUS_NORMAL) {
            throw new CrmebException("订单已取消");
        }
        if (!order.getPaid()) {
            throw new CrmebException("未支付订单无法申请退款");
        }
        if (order.getRefundStatus().equals(OrderConstants.ORDER_REFUND_STATUS_ALL)) {
            throw new CrmebException("订单已全部退款");
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE)) {
            throw new CrmebException("已完成订单无法申请退款");
        }
        OrderDetail orderDetail = orderDetailService.getById(request.getOrderDetailId());
        if (ObjectUtil.isNull(orderDetail) || !orderDetail.getOrderNo().equals(order.getOrderNo())) {
            throw new CrmebException("订单详情不存在");
        }
        int canApplyNum = orderDetail.getPayNum() - orderDetail.getApplyRefundNum() - orderDetail.getRefundNum();
        if (canApplyNum < request.getNum()) {
            throw new CrmebException(StrUtil.format("剩余可退款数量为{}", canApplyNum));
        }
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());

        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_REFUND));
        refundOrder.setOrderNo(order.getOrderNo());
        refundOrder.setMerId(order.getMerId());
        refundOrder.setUid(order.getUid());
        refundOrder.setRealName(merchantOrder.getRealName());
        refundOrder.setUserPhone(merchantOrder.getUserPhone());
        refundOrder.setUserAddress(merchantOrder.getUserAddress());
        refundOrder.setRefundPrice(order.getPayPrice());
        refundOrder.setTotalNum(order.getTotalNum());
        refundOrder.setRefundReasonWap(request.getText());
        refundOrder.setRefundReasonWapImg(systemAttachmentService.clearPrefix(request.getReasonImage()));
        refundOrder.setRefundReasonWapExplain(request.getExplain());
        refundOrder.setRefundStatus(OrderConstants.MERCHANT_REFUND_ORDER_STATUS_APPLY);

        RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
        refundOrderInfo.setRefundOrderNo(refundOrder.getRefundOrderNo());
        refundOrderInfo.setMerId(orderDetail.getMerId());
        refundOrderInfo.setOrderDetailId(orderDetail.getId());
        refundOrderInfo.setProductId(orderDetail.getProductId());
        refundOrderInfo.setProductName(orderDetail.getProductName());
        refundOrderInfo.setImage(orderDetail.getImage());
        refundOrderInfo.setAttrValueId(orderDetail.getAttrValueId());
        refundOrderInfo.setSku(orderDetail.getSku());
        refundOrderInfo.setPrice(orderDetail.getPrice());
        refundOrderInfo.setPayNum(orderDetail.getPayNum());
        refundOrderInfo.setProductType(orderDetail.getProductType());
        refundOrderInfo.setPayPrice(orderDetail.getPayPrice());
        refundOrderInfo.setApplyRefundNum(request.getNum());
        // 临时性计算退款金额、积分
        if (request.getNum().equals(orderDetail.getPayNum())) {
            refundOrderInfo.setRefundPrice(orderDetail.getPayPrice());
            refundOrderInfo.setRefundUseIntegral(orderDetail.getUseIntegral());
            refundOrderInfo.setRefundGainIntegral(orderDetail.getGainIntegral());
        } else {
            refundOrderInfo.setRefundUseIntegral(0);
            refundOrderInfo.setRefundGainIntegral(0);
            BigDecimal ratio = new BigDecimal(request.getNum().toString()).divide(new BigDecimal(orderDetail.getPayNum().toString()), 10, BigDecimal.ROUND_HALF_UP);
            refundOrderInfo.setRefundPrice(orderDetail.getPayPrice().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP));
            if (orderDetail.getUseIntegral() > 0) {
                refundOrderInfo.setRefundUseIntegral(new BigDecimal(orderDetail.getUseIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            }
            if (orderDetail.getGainIntegral() > 0) {
                refundOrderInfo.setRefundGainIntegral(new BigDecimal(orderDetail.getGainIntegral().toString()).multiply(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            }
        }

        refundOrder.setRefundPrice(refundOrderInfo.getRefundPrice());
        refundOrder.setRefundUseIntegral(refundOrderInfo.getRefundUseIntegral());
        refundOrder.setRefundGainIntegral(refundOrderInfo.getRefundGainIntegral());

        order.setRefundStatus(OrderConstants.ORDER_REFUND_STATUS_APPLY);
        orderDetail.setApplyRefundNum(orderDetail.getApplyRefundNum() + request.getNum());
        Boolean execute = transactionTemplate.execute(e -> {
            orderService.updateById(order);
            orderDetailService.updateById(orderDetail);
            refundOrderService.save(refundOrder);
            refundOrderInfoService.save(refundOrderInfo);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("申请退款失败");
        return execute;
    }

    /**
     * 退款订单列表
     *
     * @param type        列表类型：0-处理中，9-申请记录
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<RefundOrderResponse> getRefundOrderList(Integer type, PageParamRequest pageRequest) {
        PageInfo<RefundOrder> pageInfo = refundOrderService.getH5List(type, pageRequest);
        List<RefundOrder> refundOrderList = pageInfo.getList();
        if (CollUtil.isEmpty(refundOrderList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<RefundOrderResponse> responseList = refundOrderList.stream().map(refundOrder -> {
            RefundOrderResponse response = new RefundOrderResponse();
            BeanUtils.copyProperties(refundOrder, response);
            RefundOrderInfo orderInfo = refundOrderInfoService.getByRefundOrderNo(refundOrder.getRefundOrderNo());
            response.setProductName(orderInfo.getProductName());
            response.setImage(orderInfo.getImage());
            response.setSku(orderInfo.getSku());
            response.setApplyRefundNum(orderInfo.getApplyRefundNum());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 退款订单详情
     *
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    @Override
    public RefundOrderInfoResponse refundOrderDetail(String refundOrderNo) {
        return refundOrderService.getRefundOrderDetailByRefundOrderNo(refundOrderNo);
    }

    /**
     * 订单物流详情
     */
    @Override
    public LogisticsResultVo getLogisticsInfo(Integer invoiceId) {
        return orderService.getLogisticsInfo(invoiceId);
    }

    /**
     * 获取发货单列表
     * @param orderNo 订单号
     * @return 发货单列表
     */
    @Override
    public OrderInvoiceFrontResponse getInvoiceList(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        List<OrderInvoiceResponse> invoiceList = orderService.getInvoiceList(orderNo);
        OrderInvoiceFrontResponse response = new OrderInvoiceFrontResponse();
        response.setInvoiceList(invoiceList);
        if (CollUtil.isEmpty(invoiceList)) {
            response.setNum(1);
            response.setDeliveryNum(0);
            return response;
        }
        response.setNum(order.getStatus().equals(OrderConstants.ORDER_STATUS_PART_SHIPPING) ? invoiceList.size() + 1 : invoiceList.size());
        response.setDeliveryNum(invoiceList.size());
        return response;
    }

    /**
     * 获取个人中心订单数量
     */
    @Override
    public OrderCenterNumResponse userCenterNum() {
        Integer userId = userService.getUserIdException();
        OrderCenterNumResponse response = new OrderCenterNumResponse();
        response.setAwaitPayCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_PAY, userId));
        response.setAwaitShippedCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_SHIPPING, userId));
        response.setReceiptCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_WAIT_RECEIPT, userId));
        response.setVerificationCount(orderService.getCountByStatusAndUid(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION, userId));
        response.setAwaitReplyCount(orderDetailService.getAwaitReplyCount(userId));
        response.setRefundCount(refundOrderService.getRefundingCount(userId));
        return response;
    }

    /**
     * 获取订单状态图
     */
    @Override
    public List<HashMap<String, Object>> getOrderStatusImage() {
        List<HashMap<String, Object>> mapList = systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_ORDER_STATUS_IMAGE);
        return mapList;
    }

    private List<MyRecord> validateProductStock(PreOrderInfoVo orderInfoVo) {
        List<MyRecord> recordList = CollUtil.newArrayList();
//        if (orderInfoVo.getSeckillId() > 0) {
//        }
//        if (orderInfoVo.getBargainId() > 0) {
//        }
//        if (orderInfoVo.getCombinationId() > 0) {
//        }
        if (orderInfoVo.getIsVideo()) {// 视频号订单
            // 查询商品信息 视频号都是单品下单
            List<PreOrderInfoDetailVo> detailVos = orderInfoVo.getMerchantOrderVoList().get(0).getOrderInfoList();
            PayComponentProduct product = payComponentProductService.getById(detailVos.get(0).getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("商品信息不存在，请刷新后重新选择");
            }
            if (product.getIsDel()) {
                throw new CrmebException("商品已删除，请刷新后重新选择");
            }
            if (!product.getStatus().equals(5)) {
                throw new CrmebException("商品已下架，请刷新后重新选择");
            }
            if (product.getStock().equals(0) ||product.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException("商品库存不足，请刷新后重新选择");
            }
            // 查询商品规格属性值信息
            ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailVos.get(0).getAttrValueId(), detailVos.get(0).getProductId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
            }
            if (attrValue.getStock().equals(0) || attrValue.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException("商品规格库存不足，请刷新后重新选择");
            }
            // 查询视频商品规格属性值
            PayComponentProductSku productSku = payComponentProductSkuService.getByProIdAndAttrValueId(product.getId(), attrValue.getId());
            if (ObjectUtil.isNull(productSku)) {
                throw new CrmebException("商品sku信息不存在，请刷新后重新选择");
            }
            if (productSku.getStockNum().equals(0) || productSku.getStockNum() < detailVos.get(0).getPayNum()) {
                throw new CrmebException("商品sku库存不足，请刷新后重新选择");
            }

            MyRecord record = new MyRecord();
            record.set("productId", product.getId());
            record.set("attrValueId", detailVos.get(0).getAttrValueId());
            record.set("attrValueVersion", attrValue.getVersion());
            record.set("num", detailVos.get(0).getPayNum());
            record.set("skuId", productSku.getId());
            record.set("skuVersion", productSku.getVersion());
            recordList.add(record);
            return recordList;
        }
        // 普通商品
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        merchantOrderVoList.forEach(merchantOrderVo -> {
            Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
            if (!merchant.getIsSwitch()) {
                throw new CrmebException("商户已关闭，请重新下单");
            }
            merchantOrderVo.getOrderInfoList().forEach(info -> {
                // 查询商品信息
                Product product = productService.getById(info.getProductId());
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException("购买的商品信息不存在");
                }
                if (!product.getIsShow()) {
                    throw new CrmebException("购买的商品已下架");
                }
                if (product.getStock().equals(0) || info.getPayNum() > product.getStock()) {
                    throw new CrmebException("购买的商品库存不足");
                }
                // 查询商品规格属性值信息
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(info.getAttrValueId(), info.getProductId(), ProductConstants.PRODUCT_TYPE_NORMAL);
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException("购买的商品规格信息不存在");
                }
                if (attrValue.getStock() < info.getPayNum()) {
                    throw new CrmebException("购买的商品库存不足");
                }
                MyRecord record = new MyRecord();
                record.set("productId", info.getProductId());
                record.set("num", info.getPayNum());
                record.set("attrValueId", info.getAttrValueId());
                record.set("attrValueVersion", attrValue.getVersion());
                recordList.add(record);
            });
        });
        return recordList;
    }

    /**
     * 计算订单运费
     */
    private void getFreightFee(PreOrderInfoVo orderInfoVo, UserAddress userAddress) {
        BigDecimal freightFee = BigDecimal.ZERO;

        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            BigDecimal storePostage = BigDecimal.ZERO;
            if (merchantOrderVo.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrderVo.setFreightFee(storePostage);
                continue;
            }
            if (ObjectUtil.isNull(userAddress) || userAddress.getCityId() <= 0) {
                merchantOrderVo.setFreightFee(storePostage);
                continue;
            }
            // 运费根据商品计算
            Map<Integer, MyRecord> proMap = CollUtil.newHashMap();
            merchantOrderVo.getOrderInfoList().forEach(e -> {
                Integer proId = e.getProductId();
                if (proMap.containsKey(proId)) {
                    MyRecord record = proMap.get(proId);
                    record.set("totalPrice", record.getBigDecimal("totalPrice").add(e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum()))));
                    record.set("totalNum", record.getInt("totalNum") + e.getPayNum());
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", record.getBigDecimal("weight").add(weight));
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", record.getBigDecimal("volume").add(volume));
                } else {
                    MyRecord record = new MyRecord();
                    record.set("totalPrice", e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum())));
                    record.set("totalNum", e.getPayNum());
                    record.set("tempId", e.getTempId());
                    record.set("proId", proId);
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", weight);
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", volume);
                    proMap.put(proId, record);
                }
            });

            // 指定包邮（单品运费模板）> 指定区域配送（单品运费模板）
            int districtId = userAddress.getDistrictId();
            for (Map.Entry<Integer, MyRecord> m : proMap.entrySet()) {
                MyRecord record = m.getValue();
                Integer tempId = record.getInt("tempId");
                ShippingTemplates shippingTemplate = shippingTemplatesService.getById(tempId);
                if (ObjectUtil.isNull(shippingTemplate) || shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_ALL)) {
                    continue;
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_DEFINED)) {
                    ShippingTemplatesFree shippingTemplatesFree = shippingTemplatesFreeService.getByTempIdAndCityId(tempId, districtId);
                    if (ObjectUtil.isNotNull(shippingTemplatesFree)) {
                        BigDecimal totalPrice = record.getBigDecimal("totalPrice");
                        if (totalPrice.compareTo(shippingTemplatesFree.getPrice()) >= 0) {
                            continue;
                        }
                        if (shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER)) {
                            if (BigDecimal.valueOf(record.getInt("totalNum")).compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                                continue;
                            }
                        }
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT)
                                ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                            continue;
                        }
                    }
                }
                ShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, districtId);
                if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                    shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, 0);
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_PART)) {
                    if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                        continue;
                    }
                }
                BigDecimal postageFee = BigDecimal.ZERO;
                // 判断计费方式：件数、重量、体积
                switch (shippingTemplate.getType()) {
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER: // 件数
                        // 判断件数是否超过首件
                        Integer num = record.getInt("totalNum");
                        if (num <= shippingTemplatesRegion.getFirst().intValue()) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            int renewalNum = num - shippingTemplatesRegion.getFirst().intValue();
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            BigDecimal divide = BigDecimal.valueOf(renewalNum).divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                        }
                        List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getProductId().equals(record.getInt("proId"))).collect(Collectors.toList());
                        if (detailVoList.size() == 1) {
                            detailVoList.get(0).setFreightFee(postageFee);
                        } else {
                            for (int i = 0; i < detailVoList.size(); i++) {
                                PreOrderInfoDetailVo detail = detailVoList.get(i);
                                if (detailVoList.size() == (i + 1)) {
                                    detail.setFreightFee(postageFee);
                                    break;
                                }
                                BigDecimal ratio = new BigDecimal(detail.getPayNum().toString()).divide(new BigDecimal(num.toString()), 10, BigDecimal.ROUND_HALF_UP);
                                BigDecimal multiply = postageFee.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                                detail.setFreightFee(multiply);
                                postageFee = postageFee.subtract(multiply);
                            }
                        }
                        break;
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT: // 重量
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_VOLIME: // 体积
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            BigDecimal renewalNum = surplus.subtract(shippingTemplatesRegion.getFirst());
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            BigDecimal divide = renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                            List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getProductId().equals(record.getInt("proId"))).collect(Collectors.toList());
                            if (infoDetailVoList.size() == 1) {
                                infoDetailVoList.get(0).setFreightFee(postageFee);
                            } else {
                                for (int i = 0; i < infoDetailVoList.size(); i++) {
                                    PreOrderInfoDetailVo detail = infoDetailVoList.get(i);
                                    if (infoDetailVoList.size() == (i + 1)) {
                                        detail.setFreightFee(postageFee);
                                        break;
                                    }
                                    BigDecimal wv = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? detail.getWeight() : detail.getVolume();
                                    BigDecimal ratio = wv.multiply(new BigDecimal(detail.getPayNum().toString())).divide(surplus, 10, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal multiply = postageFee.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                                    detail.setFreightFee(multiply);
                                    postageFee = postageFee.subtract(multiply);
                                }
                            }
                        }
                        break;
                }
            }
            merchantOrderVo.setFreightFee(storePostage);
            freightFee = freightFee.add(storePostage);
        }
        orderInfoVo.setFreightFee(freightFee);
    }

    /**
     * 校验预下单商品信息
     *
     * @param request 预下单请求参数
     * @return OrderInfoVo
     */
    private PreOrderInfoVo validatePreOrderRequest(PreOrderRequest request, User user) {
        logger.info("预下单检查调用 -》validatePreOrderRequest->:request:{}|user:{}",JSON.toJSONString(request),JSON.toJSONString(user));
        PreOrderInfoVo preOrderInfoVo = new PreOrderInfoVo();
        List<PreMerchantOrderVo> merchantOrderVoList = CollUtil.newArrayList();
        if (request.getPreOrderType().equals("shoppingCart")) {// 购物车购买
            logger.info("预下单检查调用:购物车购买");
            merchantOrderVoList = validatePreOrderShopping(request, user);
            List<Integer> cartIdList = request.getOrderDetails().stream().map(PreOrderDetailRequest::getShoppingCartId).distinct().collect(Collectors.toList());
            preOrderInfoVo.setCartIdList(cartIdList);
        }
        if (request.getPreOrderType().equals("buyNow")) {// 立即购买
            logger.info("预下单检查调用:立即购买");
            // 立即购买只会有一条详情
            PreOrderDetailRequest detailRequest = request.getOrderDetails().get(0);
            merchantOrderVoList.add(validatePreOrderNormal(detailRequest));
        }
        if (request.getPreOrderType().equals("video")) {
            logger.info("预下单检查调用:视频号下单");
            // 视频号暂时只能购买一个商品
            PreOrderDetailRequest detailRequest = request.getOrderDetails().get(0);
            merchantOrderVoList.add(validatePreOrderVideo(detailRequest));
            preOrderInfoVo.setIsVideo(true);
        }
        preOrderInfoVo.setMerchantOrderVoList(merchantOrderVoList);
        logger.info("预下单检查调用:结果preOrderInfoVo:{}", JSON.toJSONString(preOrderInfoVo));
        return preOrderInfoVo;
    }

    /**
     * 普通商品下单校验
     *
     * @param detailRequest 商品参数
     */
    private PreMerchantOrderVo validatePreOrderNormal(PreOrderDetailRequest detailRequest) {
        // 普通商品
        if (ObjectUtil.isNull(detailRequest.getProductId())) {
            throw new CrmebException("商品编号不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException("商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException("购买数量必须大于0");
        }
        // 查询商品信息
        Product product = productService.getById(detailRequest.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException("商品信息不存在，请刷新后重新选择");
        }
        if (!product.getIsShow()) {
            throw new CrmebException("商品已下架，请刷新后重新选择");
        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException("商品库存不足，请刷新后重新选择");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), detailRequest.getProductId(), ProductConstants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
        }
        if (attrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException("商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = merchantService.getByIdException(product.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("商户已关闭，请重新选择商品");
        }

        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        merchantOrderVo.setMerId(merchant.getId());
        merchantOrderVo.setMerName(merchant.getName());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(product.getId());
        detailVo.setProductName(product.getName());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : product.getImage());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(product.getIsSub() ? 1 : 2);
        detailVo.setBrokerage(attrValue.getBrokerage());
        detailVo.setBrokerageTwo(attrValue.getBrokerageTwo());
        if (detailVo.getSubBrokerageType() == 2) {
            String firstRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO);
            String secondRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO);
            detailVo.setBrokerage(StrUtil.isNotBlank(firstRatio) ? Integer.parseInt(firstRatio) : 0);
            detailVo.setBrokerageTwo(StrUtil.isNotBlank(secondRatio) ? Integer.parseInt(secondRatio) : 0);
        }
        detailVo.setProductType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }

    /**
     * 购物车预下单校验
     *
     * @param request 请求参数
     * @param user    用户
     * @return List<PreMerchantOrderVo>
     */
    private List<PreMerchantOrderVo> validatePreOrderShopping(PreOrderRequest request, User user) {
        List<PreMerchantOrderVo> merchantOrderVoList = CollUtil.newArrayList();
        request.getOrderDetails().forEach(e -> {
            if (ObjectUtil.isNull(e.getShoppingCartId())) {
                throw new CrmebException("购物车编号不能为空");
            }
            Cart cart = cartService.getByIdAndUid(e.getShoppingCartId(), user.getId());
            if (ObjectUtil.isNull(cart)) {
                throw new CrmebException("未找到对应的购物车信息");
            }
            e.setProductId(cart.getProductId());
            e.setAttrValueId(cart.getProductAttrUnique());
            e.setProductNum(cart.getCartNum());
            PreMerchantOrderVo merchantOrderVo = validatePreOrderNormal(e);
            if (merchantOrderVoList.stream().anyMatch(m -> m.getMerId().equals(merchantOrderVo.getMerId()))) {
                for (PreMerchantOrderVo orderVo : merchantOrderVoList) {
                    if (orderVo.getMerId().equals(merchantOrderVo.getMerId())) {
                        orderVo.getOrderInfoList().addAll(merchantOrderVo.getOrderInfoList());
                        break;
                    }
                }
            } else {
                merchantOrderVoList.add(merchantOrderVo);
            }
        });
        return merchantOrderVoList;
    }

    private ComputedOrderPriceResponse computedPrice(OrderComputedPriceRequest request, PreOrderInfoVo orderInfoVo, User user) {
        String integralDeductionSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        if (request.getIsUseIntegral()) {
            if (integralDeductionSwitch.equals("false")) {
                throw new CrmebException("积分抵扣未开启，请重新下单");
            }
        }

        // 计算各种价格
        ComputedOrderPriceResponse priceResponse = new ComputedOrderPriceResponse();
        List<OrderMerchantRequest> orderMerchantRequestList = request.getOrderMerchantRequestList();
        // 计算运费
        UserAddress userAddress = userAddressService.getById(request.getAddressId());
        orderInfoVo.getMerchantOrderVoList().forEach(e -> {
            orderMerchantRequestList.forEach(o -> {
                if (o.getMerId().equals(e.getMerId())) {
                    e.setShippingType(o.getShippingType());
                    e.setUserCouponId(o.getUserCouponId());
                }
            });
        });
        getFreightFee(orderInfoVo, userAddress);
        priceResponse.setFreightFee(orderInfoVo.getFreightFee());
        // 优惠券计算
        getCouponFee(orderInfoVo, orderMerchantRequestList, user.getId());
        priceResponse.setCouponFee(orderInfoVo.getCouponFee());
        List<ComputedMerchantOrderResponse> merOrderResponseList = orderInfoVo.getMerchantOrderVoList().stream().map(vo -> {
            ComputedMerchantOrderResponse merOrderResponse = new ComputedMerchantOrderResponse();
            merOrderResponse.setMerId(vo.getMerId());
            merOrderResponse.setUserCouponId(vo.getUserCouponId());
            merOrderResponse.setCouponFee(vo.getCouponFee());
            merOrderResponse.setFreightFee(vo.getFreightFee());
            return merOrderResponse;
        }).collect(Collectors.toList());
        priceResponse.setMerOrderResponseList(merOrderResponseList);
        // 积分部分
        BigDecimal payPrice = orderInfoVo.getProTotalFee().subtract(priceResponse.getCouponFee());
        priceResponse.setIsUseIntegral(request.getIsUseIntegral());
        priceResponse.setProTotalFee(orderInfoVo.getProTotalFee());
        if (!request.getIsUseIntegral() || user.getIntegral() <= 0) {// 不使用积分
            priceResponse.setDeductionPrice(BigDecimal.ZERO);
            priceResponse.setSurplusIntegral(user.getIntegral());
            priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
            priceResponse.setUsedIntegral(0);
            priceResponse.setIntegralDeductionSwitch(false);
            priceResponse.setIsUseIntegral(false);
            String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
            if ("ture".equals(integralDeductionSwitch) && payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) >= 0) {
                priceResponse.setIntegralDeductionSwitch(true);
            }
            return priceResponse;
        }
        // 使用积分
        if (request.getIsUseIntegral() && user.getIntegral() > 0) {
            String integralDeductionStartMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
            if (Integer.parseInt(integralDeductionStartMoney) <= 0 || payPrice.compareTo(new BigDecimal(integralDeductionStartMoney)) < 0) {
                priceResponse.setDeductionPrice(BigDecimal.ZERO);
                priceResponse.setSurplusIntegral(user.getIntegral());
                priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
                priceResponse.setUsedIntegral(0);
                priceResponse.setIntegralDeductionSwitch(false);
                priceResponse.setIsUseIntegral(false);
                return priceResponse;
            }

            // 查询积分使用比例
            String integralRatio = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
            // 可抵扣金额
            BigDecimal canDeductionPrice = payPrice.multiply(new BigDecimal(integralRatio)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
            // 积分转换金额
            String integralDeductionMoney = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
            BigDecimal deductionPrice = new BigDecimal(user.getIntegral()).multiply(new BigDecimal(integralDeductionMoney));
            // 积分兑换金额小于实际支付可抵扣金额
            if (deductionPrice.compareTo(canDeductionPrice) <= 0) {
                payPrice = payPrice.subtract(deductionPrice);
                priceResponse.setSurplusIntegral(0);
                priceResponse.setUsedIntegral(user.getIntegral());
            } else {
                deductionPrice = canDeductionPrice;
                if (canDeductionPrice.compareTo(BigDecimal.ZERO) > 0) {
                    int usedIntegral = canDeductionPrice.divide(new BigDecimal(integralDeductionMoney), 0, BigDecimal.ROUND_UP).intValue();
                    priceResponse.setSurplusIntegral(user.getIntegral() - usedIntegral);
                    priceResponse.setUsedIntegral(usedIntegral);
                }
            }
            payPrice = payPrice.subtract(deductionPrice);
            priceResponse.setPayFee(payPrice.add(priceResponse.getFreightFee()));
            priceResponse.setDeductionPrice(deductionPrice);
            priceResponse.setIsUseIntegral(true);
            priceResponse.setIntegralDeductionSwitch(true);
        }
        return priceResponse;
    }

    /**
     * 视频号下单校验
     * 暂时只支持都买一种商品
     * @param detailRequest 请求参数
     * @return 预下单结果
     */
    private PreMerchantOrderVo validatePreOrderVideo(PreOrderDetailRequest detailRequest) {
        logger.info("预下单下单校验 -》validatePreOrderVideo-》:{}", JSONObject.toJSONString(detailRequest));
        if (ObjectUtil.isNull(detailRequest.getProductId())) {
            throw new CrmebException("商品编号不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException("商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException("购买数量必须大于0");
        }
        // 查询商品信息
        PayComponentProduct product = payComponentProductService.getById(detailRequest.getProductId());
        logger.info("预下单下单校验 -》product-》:{}", JSON.toJSONString(product));
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品信息不存在，请刷新后重新选择");
        }
        if (product.getIsDel()) {
            throw new CrmebException("商品已删除，请刷新后重新选择");
        }
        if (!product.getStatus().equals(5)) {
            throw new CrmebException("商品已下架，请刷新后重新选择");
        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException("商品库存不足，请刷新后重新选择");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(), detailRequest.getProductId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
        logger.info("预下单下单校验 -》attrValue:{}", JSON.toJSONString(attrValue));
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException("商品规格信息不存在，请刷新后重新选择");
        }
        if (attrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException("商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = merchantService.getByIdException(product.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("商户已关闭，请重新选择商品");
        }
        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        merchantOrderVo.setMerId(merchant.getId());
        merchantOrderVo.setMerName(merchant.getName());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(product.getId());
        detailVo.setProductName(product.getTitle());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(attrValue.getPrice());
        detailVo.setPayPrice(attrValue.getPrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : product.getHeadImg());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(product.getIsSub() ? 1 : 2);
        detailVo.setBrokerage(attrValue.getBrokerage());
        detailVo.setBrokerageTwo(attrValue.getBrokerageTwo());
//        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
//        detailVo.setProductId(product.getId());
//        detailVo.setProductName(product.getTitle());
//        detailVo.setAttrValueId(attrValue.getId());
//        detailVo.setSku(attrValue.getSku());
//        detailVo.setPrice(attrValue.getPrice());
//        detailVo.setPayNum(detailRequest.getProductNum());
//        detailVo.setImage(attrValue.getImage());
//        detailVo.setVolume(attrValue.getVolume());
//        detailVo.setWeight(attrValue.getWeight());
//        detailVo.setTempId(product.getTempId());
////        detailVo.setIsSub(product.getIsSub());
//        detailVo.setProductType(ProductConstants.PRODUCT_TYPE_COMPONENT);
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        merchantOrderVo.setOrderInfoList(infoList);
        logger.info("预下单下单校验-》merchantOrderVo:{}", JSON.toJSONString(merchantOrderVo));
        return merchantOrderVo;
    }

    /**
     * 获取优惠金额
     */
    private void getCouponFee(PreOrderInfoVo orderInfoVo, List<OrderMerchantRequest> orderMerchantRequestList, Integer uid) {
        long count = orderMerchantRequestList.stream().filter(e -> e.getUserCouponId() > 0).count();
        if (count <= 0) {
            orderInfoVo.setCouponFee(BigDecimal.ZERO);
            return;
        }
//        if (orderInfoVo.getSeckillId() > 0 || orderInfoVo.getBargainId() > 0 || orderInfoVo.getCombinationId() > 0) {
//            throw new CrmebException("营销活动商品无法使用优惠券");
//        }
        if (orderInfoVo.getIsVideo()) {
            throw new CrmebException("视频号商品无法使用优惠券");
        }
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            for (OrderMerchantRequest orderMerchantRequest : orderMerchantRequestList) {
                if (merchantOrderVo.getMerId().equals(orderMerchantRequest.getMerId()) && orderMerchantRequest.getUserCouponId() > 0) {
                    merchantOrderVo.setUserCouponId(orderMerchantRequest.getUserCouponId());
                }
            }
        }
        BigDecimal couponFee = BigDecimal.ZERO;
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            if (merchantOrderVo.getUserCouponId() <= 0) {
                continue;
            }
            BigDecimal merchantCouponFee = BigDecimal.ZERO;
            // 判断优惠券是否可以使用
            CouponUser couponUser = couponUserService.getById(merchantOrderVo.getUserCouponId());
            if (ObjectUtil.isNull(couponUser) || !couponUser.getUid().equals(uid)) {
                throw new CrmebException("优惠券领取记录不存在！");
            }
            if (!couponUser.getMerId().equals(merchantOrderVo.getMerId())) {
                throw new CrmebException("商家无此优惠券");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_USED.equals(couponUser.getStatus())) {
                throw new CrmebException("此优惠券已使用！");
            }
            if (CouponConstants.STORE_COUPON_USER_STATUS_LAPSED.equals(couponUser.getStatus())) {
                throw new CrmebException("此优惠券已失效！");
            }
            //判断是否在使用时间内
            Date date = CrmebDateUtil.nowDateTime();
            if (couponUser.getStartTime().compareTo(date) > 0) {
                throw new CrmebException("此优惠券还未到达使用时间范围之内！");
            }
            if (date.compareTo(couponUser.getEndTime()) > 0) {
                throw new CrmebException("此优惠券已经失效了");
            }
            if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(orderInfoVo.getProTotalFee()) > 0) {
                throw new CrmebException("总金额小于优惠券最小使用金额");
            }
            //检测优惠券信息
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                // 商品券
                List<Integer> productIdList = merchantOrderVo.getOrderInfoList().stream().map(PreOrderInfoDetailVo::getProductId).collect(Collectors.toList());
                if (productIdList.size() < 1) {
                    throw new CrmebException("No item found product");
                }
                //设置优惠券所提供的集合
                List<CouponProduct> couponProductList = couponProductService.findByCid(couponUser.getCouponId());
                List<Integer> primaryKeyIdList = couponProductList.stream().map(CouponProduct::getPid).collect(Collectors.toList());
                //取两个集合的交集，如果是false则证明没有相同的值
                //oldList.retainAll(newList)返回值代表oldList是否保持原样，如果old和new完全相同，那old保持原样并返回false。
                //交集：listA.retainAll(listB) ——listA内容变为listA和listB都存在的对象；listB不变
                primaryKeyIdList.retainAll(productIdList);
                if (CollUtil.isEmpty(primaryKeyIdList)) {
                    throw new CrmebException("此券为商品券，请在购买相关产品后使用！");
                }
                List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList().stream().filter(info -> primaryKeyIdList.contains(info.getProductId())).collect(Collectors.toList());
                if (CollUtil.isEmpty(infoDetailVoList)) {
                    throw new CrmebException("此券为商品券，请在购买相关产品后使用！");
                }
                BigDecimal proTotalPrice = infoDetailVoList.stream().map(e -> e.getPrice().multiply(new BigDecimal(e.getPayNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(proTotalPrice) > 0) {
                    throw new CrmebException("总金额小于优惠券最低使用金额");
                }
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    merchantCouponFee = merchantCouponFee.add(new BigDecimal(couponUser.getMoney().toString()));
                } else {
                    merchantCouponFee = merchantCouponFee.add(proTotalPrice);
                }
                BigDecimal couponPrice = merchantCouponFee;
                if (infoDetailVoList.size() == 1) {
                    infoDetailVoList.get(0).setCouponPrice(couponPrice);
                } else {
                    for (int i = 0; i < infoDetailVoList.size(); i++) {
                        PreOrderInfoDetailVo detailVo = infoDetailVoList.get(i);
                        if (infoDetailVoList.size() == (i + 1)) {
                            detailVo.setCouponPrice(couponPrice);
                            break;
                        }
                        BigDecimal detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        BigDecimal ratio = detailPrice.divide(proTotalPrice, 10, BigDecimal.ROUND_HALF_UP);
                        BigDecimal detailCouponFee = couponPrice.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                        couponPrice = couponPrice.subtract(detailCouponFee);
                        detailVo.setCouponPrice(detailCouponFee);
                    }
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                // 商家券
                List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList();
                BigDecimal proTotalPrice = infoDetailVoList.stream().map(i -> i.getPrice().multiply(new BigDecimal(i.getPayNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (new BigDecimal(couponUser.getMinPrice().toString()).compareTo(proTotalPrice) > 0) {
                    throw new CrmebException("总金额小于优惠券最低使用金额");
                }
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) > 0) {
                    merchantCouponFee = merchantCouponFee.add(new BigDecimal(couponUser.getMoney().toString()));
                } else {
                    merchantCouponFee = merchantCouponFee.add(proTotalPrice);
                }
                BigDecimal couponPrice = merchantCouponFee;
                if (infoDetailVoList.size() == 1) {
                    infoDetailVoList.get(0).setCouponPrice(couponPrice);
                } else {
                    for (int i = 0; i < infoDetailVoList.size(); i++) {
                        PreOrderInfoDetailVo detailVo = infoDetailVoList.get(i);
                        if (infoDetailVoList.size() == (i + 1)) {
                            detailVo.setCouponPrice(couponPrice);
                            break;
                        }
                        BigDecimal detailPrice = detailVo.getPrice().multiply(new BigDecimal(detailVo.getPayNum()));
                        BigDecimal ratio = detailPrice.divide(proTotalPrice, 10, BigDecimal.ROUND_HALF_UP);
                        BigDecimal detailCouponFee = couponPrice.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                        couponPrice = couponPrice.subtract(detailCouponFee);
                        detailVo.setCouponPrice(detailCouponFee);
                    }
                }
            }
            couponFee = couponFee.add(merchantCouponFee);
            merchantOrderVo.setCouponFee(merchantCouponFee);
        }
        orderInfoVo.setCouponFee(couponFee);
    }
}
