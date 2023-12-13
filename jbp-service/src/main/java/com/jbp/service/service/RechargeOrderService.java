package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.RechargeOrder;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.RechargeOrderSearchRequest;
import com.jbp.common.request.UserRechargeRequest;
import com.jbp.common.response.OrderPayResultResponse;
import com.jbp.common.response.RechargePackageResponse;

import java.util.List;

/**
 * RechargeOrderService 接口
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
public interface RechargeOrderService extends IService<RechargeOrder> {

    /**
     * 获取充值套餐
     *
     * @return UserRechargeResponse
     */
    RechargePackageResponse getRechargePackage();

    /**
     * 创建用户充值订单
     *
     * @param request 用户下单参数
     */
    OrderPayResultResponse userRechargeOrderCreate(UserRechargeRequest request);

    /**
     * 充值订单分页列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<RechargeOrder> getAdminPage(RechargeOrderSearchRequest request, PageParamRequest pageParamRequest);


    /**
     * 获取订单
     * @param outTradeNo 商户系统内部的订单号
     */
    RechargeOrder getByOutTradeNo(String outTradeNo);

    /**
     * 支付成功后置处理
     * @param rechargeOrder 支付订单
     */
    Boolean paySuccessAfter(RechargeOrder rechargeOrder);

    /**
     * 获取某一天的充值记录
     * @param date 日期 yyyy-MM-dd
     * @return 充值记录
     */
    List<RechargeOrder> findByDate(String date);

    /**
     * 获取某一月的充值记录
     * @param month 日期 yyyy-MM
     * @return 充值记录
     */
    List<RechargeOrder> findByMonth(String month);
}
