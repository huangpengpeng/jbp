package com.jbp.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.MerchantBillDao;
import com.jbp.service.service.MerchantBillService;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.bill.MerchantBill;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.FundsFlowRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.DateLimitUtilVo;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * MerchantBillServiceImpl 接口实现
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
public class MerchantBillServiceImpl extends ServiceImpl<MerchantBillDao, MerchantBill> implements MerchantBillService {

    @Resource
    private MerchantBillDao dao;

    /**
     * 资金监控
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantBill> getFundsFlow(FundsFlowRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<MerchantBill> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantBill> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantBill::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.eq(MerchantBill::getOrderNo, request.getOrderNo());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(MerchantBill::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(MerchantBill::getId);
        List<MerchantBill> merchantBillList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, merchantBillList);
    }
}

