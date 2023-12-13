package com.jbp.service.service;

/**
 * 异步调用服务
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
public interface AsyncService {

    /**
     * 商品详情统计
     * @param proId 商品id
     * @param uid 用户uid
     */
    void productDetailStatistics(Integer proId, Integer uid);

    /**
     * 保存用户访问记录
     * @param userId 用户id
     * @param visitType 访问类型
     */
    void saveUserVisit(Integer userId, Integer visitType);

    /**
     * 订单支付成功拆单处理
     * @param orderNo 订单号
     */
    void orderPaySuccessSplit(String orderNo);

    /**
     * 访问用户个人中心记录
     * @param uid 用户id
     */
    void visitUserCenter(Integer uid);

    /**
     * 安装统计
     */
    void installStatistics();
}
