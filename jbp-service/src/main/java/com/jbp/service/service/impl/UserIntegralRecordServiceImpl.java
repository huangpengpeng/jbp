package com.jbp.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.vo.IntegralRecordVo;
import com.jbp.service.dao.UserIntegralRecordDao;
import com.jbp.service.service.UserIntegralRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户积分记录Service实现类
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
public class UserIntegralRecordServiceImpl extends ServiceImpl<UserIntegralRecordDao, UserIntegralRecord> implements UserIntegralRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegralRecordServiceImpl.class);

    @Resource
    private UserIntegralRecordDao dao;

    @Override
    public UserIntegralRecord add(Integer uid, String integralType, String externalNo, Integer type,
                                  String title, BigDecimal integral, BigDecimal balance, String mark, String postscript) {
        Date time = DateTimeUtils.getNow();
        UserIntegralRecord record = UserIntegralRecord
                .builder()
                .uid(uid)
                .integralType(integralType)
                .externalNo(externalNo)
                .type(type)
                .title(title)
                .integral(integral)
                .balance(balance)
                .mark(mark)
                .postscript(postscript)
                .createTime(time)
                .updateTime(time)
                .build();
        save(record);
        return record;
    }


    /**
     * 积分明细分页查询
     */
    @Override
    public PageInfo<IntegralRecordVo> page(IntegralPageSearchRequest request, PageParamRequest pageRequest) {
        Page<UserIntegralRecord> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        Map<String, Object> map = BeanUtil.beanToMap(pageRequest);
        List<IntegralRecordVo> list = dao.findList(map);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        return CommonPage.copyPageInfo(page, list);
    }
}

