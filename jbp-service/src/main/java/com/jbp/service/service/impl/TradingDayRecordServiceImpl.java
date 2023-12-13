package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.record.TradingDayRecord;
import com.jbp.service.dao.TradingDayRecordDao;
import com.jbp.service.service.TradingDayRecordService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ShoppingProductDayRecordService 接口实现
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
@Service
public class TradingDayRecordServiceImpl extends ServiceImpl<TradingDayRecordDao, TradingDayRecord> implements TradingDayRecordService {

    @Resource
    private TradingDayRecordDao dao;

}

