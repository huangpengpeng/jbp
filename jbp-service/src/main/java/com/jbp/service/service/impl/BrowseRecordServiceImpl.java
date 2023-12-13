package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.service.dao.BrowseRecordDao;
import com.jbp.service.service.BrowseRecordService;
import com.jbp.common.model.record.BrowseRecord;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserVisitRecordServiceImpl 接口实现
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
public class BrowseRecordServiceImpl extends ServiceImpl<BrowseRecordDao, BrowseRecord> implements BrowseRecordService {

    @Resource
    private BrowseRecordDao dao;

    /**
     * 根据用户id和商品id获取
     * @param uid 用户id
     * @param proId 商品id
     */
    @Override
    public BrowseRecord getByUidAndProId(Integer uid, Integer proId) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        lqw.eq(BrowseRecord::getProductId, proId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取用户足迹数量
     * @param uid 用户ID
     * @return 用户足迹数量
     */
    @Override
    public Integer getCountByUid(Integer uid) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户所有足迹
     * @param uid 用户Id
     * @return 用户足迹
     */
    @Override
    public List<BrowseRecord> findAllByUid(Integer uid) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        lqw.orderByDesc(BrowseRecord::getCreateTime);
        lqw.last(" limit 100");
        return dao.selectList(lqw);
    }

    /**
     * 更新足迹数据
     */
    @Override
    public Boolean myUpdate(BrowseRecord browseRecord) {
        return dao.myUpdate(browseRecord) > 0;
    }
}

