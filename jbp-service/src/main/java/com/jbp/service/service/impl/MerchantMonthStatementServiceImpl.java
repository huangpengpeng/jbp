package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.service.dao.MerchantMonthStatementDao;
import com.jbp.service.service.MerchantMonthStatementService;
import com.jbp.service.service.OrderProfitSharingService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.model.admin.SystemAdmin;
import com.jbp.common.model.bill.MerchantMonthStatement;
import com.jbp.common.model.order.OrderProfitSharing;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.SecurityUtil;
import com.jbp.common.vo.DateLimitUtilVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * MerchantMonthStatementServiceImpl 接口实现
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
public class MerchantMonthStatementServiceImpl extends ServiceImpl<MerchantMonthStatementDao, MerchantMonthStatement> implements MerchantMonthStatementService {

    @Resource
    private MerchantMonthStatementDao dao;

    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 获取某个月的所有帐单
     *
     * @param month 月份：年-月
     * @return List
     */
    @Override
    public List<MerchantMonthStatement> findByMonth(String month) {
        LambdaQueryWrapper<MerchantMonthStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMonthStatement::getDataDate, month);
        return dao.selectList(lqw);
    }

    /**
     * 分页列表
     *
     * @param dateLimit        时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantMonthStatement> getPageList(String dateLimit, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<MerchantMonthStatement> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<MerchantMonthStatement> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMonthStatement::getMerId, systemAdmin.getMerId());
        if (StrUtil.isNotBlank(dateLimit)) {
            DateLimitUtilVo dateLimitVo = CrmebDateUtil.getMonthLimit(dateLimit);
            String startMonth = dateLimitVo.getStartTime();
            String endMonth = dateLimitVo.getEndTime();
            lqw.between(MerchantMonthStatement::getDataDate, startMonth, endMonth);
        }
        lqw.orderByDesc(MerchantMonthStatement::getId);
        List<MerchantMonthStatement> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        // 判断是否包含本月
        String nowMonth = DateUtil.date().toString(DateConstants.DATE_FORMAT_MONTH);
        for (MerchantMonthStatement monthStatement : list) {
            if (!monthStatement.getDataDate().equals(nowMonth)) {
                continue;
            }
            writeMonthStatement(monthStatement);
        }
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 写入本月数据
     *
     * @param monthStatement 本月帐单
     */
    private void writeMonthStatement(MerchantMonthStatement monthStatement) {
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByMonth(monthStatement.getMerId(), monthStatement.getDataDate());
        List<RefundOrder> refundOrderList = refundOrderService.findByMonth(monthStatement.getMerId(), monthStatement.getDataDate());

        // 订单支付总金额
        BigDecimal orderPayAmount = new BigDecimal("0.00");
        // 订单支付笔数
        int orderNum = 0;
        // 订单收入金额
        BigDecimal orderIncomeAmount = new BigDecimal("0.00");
        // 平台手续费
        BigDecimal handlingFee = new BigDecimal("0.00");
        // 一二级分佣金额
        BigDecimal firstBrokerage = new BigDecimal("0.00");
        BigDecimal secondBrokerage = new BigDecimal("0.00");
        // 商户退款金额
        BigDecimal refundAmount = new BigDecimal("0.00");
        // 退款笔数
        int refundNum = 0;

        if (CollUtil.isNotEmpty(sharingList)) {
            orderPayAmount = sharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderNum = sharingList.size();
            orderIncomeAmount = sharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            handlingFee = sharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            firstBrokerage = sharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            secondBrokerage = sharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (CollUtil.isNotEmpty(refundOrderList)) {
            refundAmount = refundOrderList.stream().map(RefundOrder::getMerchantRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundNum = refundOrderList.size();
        }

        // 支出总金额 = 商户退款金额
        BigDecimal payoutAmount = refundAmount;
        // 支出笔数 = 退款笔数
        int payoutNum = refundNum;
        // 商户日收支 = 订单收入金额 - 商户退款金额
        BigDecimal incomeExpenditure = orderIncomeAmount.subtract(refundAmount);

        monthStatement.setOrderPayAmount(orderPayAmount);
        monthStatement.setOrderNum(orderNum);
        monthStatement.setOrderIncomeAmount(orderIncomeAmount);
        monthStatement.setHandlingFee(handlingFee);
        monthStatement.setFirstBrokerage(firstBrokerage);
        monthStatement.setSecondBrokerage(secondBrokerage);
        monthStatement.setPayoutAmount(payoutAmount);
        monthStatement.setPayoutNum(payoutNum);
        monthStatement.setRefundAmount(refundAmount);
        monthStatement.setRefundNum(refundNum);
        monthStatement.setIncomeExpenditure(incomeExpenditure);
    }
}

