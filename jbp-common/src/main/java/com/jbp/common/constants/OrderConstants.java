package com.jbp.common.constants;

/**
 * 订单常量类
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
public class OrderConstants {

    /** 订单号前缀-平台 */
    public static final String ORDER_PREFIX_PLATFORM = "PT";
    /** 订单号前缀-商户 */
    public static final String ORDER_PREFIX_MERCHANT = "SH";
    /** 订单号前缀-订单组 */
    public static final String ORDER_PREFIX_GROUP = "GR";
    /** 订单号前缀-退款 */
    public static final String ORDER_PREFIX_REFUND = "RE";
    /** 订单号前缀-微信订单号 */
    public static final String ORDER_PREFIX_WECHAT = "WX";
    /** 订单号前缀-支付宝订单号 */
    public static final String ORDER_PREFIX_ALI = "AL";
    /** 充值订单号前缀 */
    public static final String RECHARGE_ORDER_PREFIX = "CZ";
    /** 结算订单号前缀_用户 */
    public static final String CLOSING_ORDER_PREFIX_USER = "CU";
    /** 结算订单号前缀_商户 */
    public static final String CLOSING_ORDER_PREFIX_MERCHANT = "CM";
    /** 共享仓订单号前缀 */
    public static final String GXC_ORDER_PREFIX = "GXC";
    /** 共享仓操作订单号前缀 */
    public static final String C_GXC_ORDER_PREFIX = "CGXC";

    /** 发货单号 */
    public static final String ORDER_PREFIX_SHIP = "FH";

    /** 订货单号 */
    public static final String ORDER_PREFIX_DING = "DH";



    /** 订单状态-待支付 */
    public static final Integer ORDER_STATUS_WAIT_PAY = 0;
    /** 订单状态-待发货 */
    public static final Integer ORDER_STATUS_WAIT_SHIPPING = 1;
    /** 订单状态-部分发货 */
    public static final Integer ORDER_STATUS_PART_SHIPPING = 2;
    /** 订单状态-待核销 */
    public static final Integer ORDER_STATUS_AWAIT_VERIFICATION = 3;
    /** 订单状态-待收货 */
    public static final Integer ORDER_STATUS_WAIT_RECEIPT = 4;
    /** 订单状态-已收货 */
    public static final Integer ORDER_STATUS_TAKE_DELIVERY = 5;
    /** 订单状态-已完成 */
    public static final Integer ORDER_STATUS_COMPLETE = 6;
    /** 订单状态-已取消 */
    public static final Integer ORDER_STATUS_CANCEL = 9;

    /** 订单取消状态-未取消 */
    public static final Integer ORDER_CANCEL_STATUS_NORMAL = 0;
    /** 订单取消状态-系统取消 */
    public static final Integer ORDER_CANCEL_STATUS_SYSTEM = 1;
    /** 订单取消状态-用户取消 */
    public static final Integer ORDER_CANCEL_STATUS_USER = 2;

    /** 订单等级-平台主订单 */
    public static final Integer ORDER_LEVEL_PLATFORM = 0;
    /** 订单等级-商户订单 */
    public static final Integer ORDER_LEVEL_MERCHANT = 1;
    /** 订单等级-商户子订单 */
    public static final Integer ORDER_LEVEL_MERCHANT_CHILD = 2;

    /** 订单类型-普通订单 */
    public static final Integer ORDER_TYPE_NORMAL = 0;
    /** 订单类型-视频号订单 */
    public static final Integer ORDER_TYPE_VIDEO = 1;
    /** 订单类型-秒杀订单 */
    public static final Integer ORDER_TYPE_SECKILL = 2;
    /** 订单类型-发货单 */
    public static final Integer ORDER_TYPE_SHIP = 3;
    /** 订单类型-定单 */
    public static final Integer ORDER_TYPE_DORDER = 4;

    /** 订单配送方式-快递 */
    public static final Integer ORDER_SHIPPING_TYPE_EXPRESS = 1;
    /** 订单配送方式-门店自提 */
    public static final Integer ORDER_SHIPPING_TYPE_PICK_UP = 2;
    /** 订单发货类型-快递 */
    public static final String ORDER_DELIVERY_TYPE_EXPRESS = "express";
    /** 订单发货类型-虚拟发货 */
    public static final String ORDER_DELIVERY_TYPE_FICTITIOUS = "fictitious";

    /** 预下单缓存前缀 */
    public static final String PRE_ORDER_CACHE_PREFIX = "user_pre_order:";
    /** 预下单缓存时间 */
    public static final Long PRE_ORDER_CACHE_TIME = 60L;

    /** 订单退款状态-未退款 */
    public static final Integer ORDER_REFUND_STATUS_NOT_APPLY = 0;
    /** 订单退款状态-申请中 */
    public static final Integer ORDER_REFUND_STATUS_APPLYING = 1;
    /** 订单退款状态-退款中 */
    public static final Integer ORDER_REFUND_STATUS_REFUNDING = 2;
    /** 订单退款状态-已退款 */
    public static final Integer ORDER_REFUND_STATUS_REFUND = 3;

    /** 订单下单类型-购物车 */
    public static final String PLACE_ORDER_TYPE_CART = "shoppingCart";
    /** 订单下单类型-立即购买 */
    public static final String PLACE_ORDER_TYPE_BUY_NOW = "buyNow";
    /** 订单下单类型-视屏号 */
    public static final String PLACE_ORDER_TYPE_VIDEO = "video";
    /** 订单下单类型-秒杀 */
    public static final String PLACE_ORDER_TYPE_SECKILL = "seckill";

    /** 订单下单类型-秒杀 */
    public static final String PLACE_ORDER_TYPE_SHIP = "ship";
    /** 订单下单类型-秒杀 */
    public static final String PLACE_ORDER_TYPE_DORDER = "dorder";


    /** 商户端订单查询状态-所有 */
    public static final String MERCHANT_ORDER_STATUS_ALL = "all";
    /** 商户端订单查询状态-未支付 */
    public static final String MERCHANT_ORDER_STATUS_UNPAID = "unPaid";
    /** 商户端订单查询状态-未发货 */
    public static final String MERCHANT_ORDER_STATUS_NOT_SHIPPED = "notShipped";
    /** 商户端订单查询状态-待收货 */
    public static final String MERCHANT_ORDER_STATUS_SPIKE = "spike";
    /** 商户端订单查询状态-已收货 */
    public static final String MERCHANT_ORDER_STATUS_RECEIVING = "receiving";
    /** 商户端订单查询状态-交易完成 */
    public static final String MERCHANT_ORDER_STATUS_COMPLETE = "complete";
    /** 商户端订单查询状态-待核销 */
    public static final String MERCHANT_ORDER_STATUS_AWAIT_VERIFICATION = "awaitVerification";
    /** 商户端订单查询状态-退款中 */
//    public static final String MERCHANT_ORDER_STATUS_REFUNDING = "refunding";
    /** 商户端订单查询状态-已退款 */
    public static final String MERCHANT_ORDER_STATUS_REFUNDED = "refunded";
    /** 商户端订单查询状态-已删除 */
    public static final String MERCHANT_ORDER_STATUS_DELETED = "deleted";

    /** 订单退款状态-未退款 */
    public static final Integer ORDER_REFUND_STATUS_NORMAL = 0;
    /** 订单退款状态-申请中 */
    public static final Integer ORDER_REFUND_STATUS_APPLY = 1;
    /** 订单退款状态-部分退款 */
    public static final Integer ORDER_REFUND_STATUS_PORTION = 2;
    /** 订单退款状态-已退款 */
    public static final Integer ORDER_REFUND_STATUS_ALL = 3;

    /** 商户退款订单状态-待审核 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_APPLY = 0;
    /** 商户退款订单状态-商家拒绝 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_REJECT = 1;
    /** 商户退款订单状态-退款中 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_REFUNDING = 2;
    /** 商户退款订单状态-已退款 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_REFUND = 3;
    /** 商户退款订单状态-用户退货 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_RETURN_GOODS = 4;
    /** 商户退款订单状态-商家待收货 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_AWAIT_RECEIVING = 5;
    /** 商户退款订单状态-撤销 */
    public static final Integer MERCHANT_REFUND_ORDER_STATUS_REVOKE = 6;


    /** 订单场景 报单*/
    public static final String ORDER_PLATFORM_DECL = "报单";
    /** 订单场景 换购*/
    public static final String ORDER_PLATFORM_TRADE = "换购";

}
