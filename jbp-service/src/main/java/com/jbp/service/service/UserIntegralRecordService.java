package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.IntegralRecordPageResponse;

import java.util.List;

/**
 * 用户积分记录Service
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
public interface UserIntegralRecordService extends IService<UserIntegralRecord> {

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    List<UserIntegralRecord> findListByOrderNoAndUid(String orderNo, Integer uid);

    /**
     * 积分解冻
     */
    void integralThaw();

//    /**
//     * PC后台列表
//     * @param request 搜索条件
//     * @param pageParamRequest 分页参数
//     * @return 记录列表
//     */
//    PageInfo<UserIntegralRecordResponse> findAdminList(AdminIntegralSearchRequest request, PageParamRequest pageParamRequest);
//
//    /**
//     * 根据类型条件计算积分总数
//     * @param uid 用户uid
//     * @param type 类型：1-增加，2-扣减
//     * @param date 日期
//     * @param linkType 关联类型
//     * @return 积分总数
//     */
//    Integer getSumIntegral(Integer uid, Integer type, String date, String linkType);

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<UserIntegralRecord> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest);

//    /**
//     * 获取用户冻结的积分
//     * @param uid 用户uid
//     * @return 积分数量
//     */
//    Integer getFrozenIntegralByUid(Integer uid);

    /**
     * 根据订单号跟类型获取记录（一个订单同类型只会有一条数据）
     * @param orderNo 订单编号
     * @param type 类型：1-增加，2-扣减
     * @return UserIntegralRecord
     */
    UserIntegralRecord getByOrderNoAndType(String orderNo, Integer type);

    /**
     * 用户累计积分
     * @param uid 用户id
     * @return 用户累计积分
     */
    Integer getSettledIntegralByUid(Integer uid);

    /**
     * 用户冻结积分
     * @param uid 用户id
     * @return 用户冻结积分
     */
    Integer getFreezeIntegralByUid(Integer uid);

    /**
     * 管理端查询积分记录分页列表
     * @param request 搜索条件
     * @param pageRequest 分页参数
     * @return 记录列表
     */
    PageInfo<IntegralRecordPageResponse> findRecordPageListByPlat(IntegralPageSearchRequest request, PageParamRequest pageRequest);
}
