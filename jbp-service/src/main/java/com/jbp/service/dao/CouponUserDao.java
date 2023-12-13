package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.response.CouponUserOrderResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户优惠券表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface CouponUserDao extends BaseMapper<CouponUser> {

    /**
     * 获取预下单可用优惠券
     */
    List<CouponUserOrderResponse> findListByPreOrder(Map<String, Object> map);
}
