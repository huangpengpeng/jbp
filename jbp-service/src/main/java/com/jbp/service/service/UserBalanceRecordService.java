package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserBalanceRecord;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

/**
*  UserBalanceRecordService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface UserBalanceRecordService extends IService<UserBalanceRecord> {

    /**
     * 获取用户消费记录
     */
    List<UserBalanceRecord> getMonetaryRecordByUid(Integer uid);

    /**
     * 获取用户充值记录
     */
    List<UserBalanceRecord> getRechargeRecordByUid(Integer uid);

    /**
     * 用户余额记录
     * @param uid 用户id
     * @param recordType 记录类型：all-全部，expenditure-支出，income-收入
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBalanceRecord> getUserBalanceRecord(Integer uid, String recordType, PageParamRequest pageRequest);
}