package com.jbp.common.constants;

/**
 * 退款订单状态常量类
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
public class RefundOrderConstants {

    /** 退款单日志-申请退款 */
    public static final String REFUND_ORDER_LOG_APPLY = "apply";
    /** 退款单日志-商家审核 */
    public static final String REFUND_ORDER_LOG_AUDIT = "audit";
    /** 退款单日志-商品退回 */
    public static final String REFUND_ORDER_LOG_RETURNING_GOODS = "returning";
    /** 退款单日志-商家确认收货 */
    public static final String REFUND_ORDER_LOG_RECEIVING = "receiving";
    /** 退款单日志-退款成功 */
    public static final String REFUND_ORDER_LOG_REFUND = "refund";

}
