package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.UserBalanceRecordDao;
import com.jbp.service.service.UserBalanceRecordService;
import com.jbp.common.constants.BalanceRecordConstants;
import com.jbp.common.model.user.UserBalanceRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  UserBalanceRecordServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class UserBalanceRecordServiceImpl extends ServiceImpl<UserBalanceRecordDao, UserBalanceRecord> implements UserBalanceRecordService {

    @Resource
    private UserBalanceRecordDao dao;

    /**
     * 获取用户消费记录
     */
    @Override
    public List<UserBalanceRecord> getMonetaryRecordByUid(Integer uid) {
        LambdaQueryWrapper<UserBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBalanceRecord::getUid, uid);
        lqw.eq(UserBalanceRecord::getLinkType, BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBalanceRecord::getType, BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
        lqw.orderByDesc(UserBalanceRecord::getId);
        return dao.selectList(lqw);
    }

    /**
     * 获取用户充值记录
     */
    @Override
    public List<UserBalanceRecord> getRechargeRecordByUid(Integer uid) {
        LambdaQueryWrapper<UserBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBalanceRecord::getUid, uid);
        lqw.eq(UserBalanceRecord::getLinkType, BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_RECHARGE);
        lqw.eq(UserBalanceRecord::getType, BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
        lqw.orderByDesc(UserBalanceRecord::getId);
        return dao.selectList(lqw);
    }

    /**
     * 用户余额记录
     * @param uid 用户id
     * @param recordType 记录类型：all-全部，expenditure-支出，income-收入，recharge-充值
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBalanceRecord> getUserBalanceRecord(Integer uid, String recordType, PageParamRequest pageRequest) {
        Page<UserBalanceRecord> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserBalanceRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBalanceRecord::getUid, uid);
        switch (recordType) {
            case "expenditure":
                lqw.eq(UserBalanceRecord::getType, BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
                break;
            case "income":
                lqw.eq(UserBalanceRecord::getType, BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                break;
            case "recharge":
                lqw.eq(UserBalanceRecord::getType, BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                lqw.eq(UserBalanceRecord::getLinkType, BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_RECHARGE);
                break;
        }
        lqw.orderByDesc(UserBalanceRecord::getId);
        List<UserBalanceRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }
}

