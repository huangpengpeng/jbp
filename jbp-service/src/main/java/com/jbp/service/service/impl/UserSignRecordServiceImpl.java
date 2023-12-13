package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.UserSignRecordDao;
import com.jbp.service.service.UserSignRecordService;
import com.jbp.common.model.sgin.UserSignRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  UserSignRecordServiceImpl 接口实现
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
public class UserSignRecordServiceImpl extends ServiceImpl<UserSignRecordDao, UserSignRecord> implements UserSignRecordService {

    @Resource
    private UserSignRecordDao dao;

    /**
     * 获取用户签到记录
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserSignRecord> pageRecordList(PageParamRequest pageParamRequest) {
        Page<UserSignRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(UserSignRecord::getId);
        List<UserSignRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    /**
     * 获取用户最后一条签到记录
     * @param uid 用户id
     * @return UserSignRecord
     */
    @Override
    public UserSignRecord getLastByUid(Integer uid) {
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserSignRecord::getUid, uid);
        lqw.orderByDesc(UserSignRecord::getCreateTime);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取某个月的签到记录
     * @param uid 用户id
     * @param month 月份 yyyy-MM
     * @return 签到记录
     */
    @Override
    public List<UserSignRecord> findByMonth(Integer uid, String month) {
        QueryWrapper<UserSignRecord> query = Wrappers.query();
        query.eq("uid", uid);
        query.apply("date_format(create_time, '%Y-%m') = {0}", month);
        return dao.selectList(query);
    }

    /**
     * 获取用户签到记录列表
     * @param uid 用户ID
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserSignRecord> findPageByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserSignRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSignRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserSignRecord::getUid, uid);
        lqw.orderByDesc(UserSignRecord::getId);
        List<UserSignRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }
}

