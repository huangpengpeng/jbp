package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.jbp.service.service.*;
import com.jbp.common.constants.DateConstants;
import com.jbp.common.constants.RedisConstants;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.bill.MerchantDailyStatement;
import com.jbp.common.model.bill.MerchantMonthStatement;
import com.jbp.common.model.bill.PlatformDailyStatement;
import com.jbp.common.model.bill.PlatformMonthStatement;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.order.OrderProfitSharing;
import com.jbp.common.model.order.RechargeOrder;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.record.MerchantDayRecord;
import com.jbp.common.model.record.ProductDayRecord;
import com.jbp.common.model.record.ShoppingProductDayRecord;
import com.jbp.common.model.record.TradingDayRecord;
import com.jbp.common.utils.RedisUtil;
import com.jbp.common.vo.MyRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatisticsTaskService 接口实现
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
public class StatisticsTaskServiceImpl implements StatisticsTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsTaskServiceImpl.class);

    @Autowired
    private ShoppingProductDayRecordService shoppingProductDayRecordService;
    @Autowired
    private ProductDayRecordService productDayRecordService;
    @Autowired
    private TradingDayRecordService tradingDayRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PlatformDailyStatementService platformDailyStatementService;
    @Autowired
    private PlatformMonthStatementService platformMonthStatementService;
    @Autowired
    private MerchantDailyStatementService merchantDailyStatementService;
    @Autowired
    private MerchantMonthStatementService merchantMonthStatementService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderProfitSharingService orderProfitSharingService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private MerchantDayRecordService merchantDayRecordService;

    /**
     * 每天零点的自动统计前一天的数据
     */
    @Override
    public void autoStatistics() {
        // 获取昨天的日期
        String yesterdayStr = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);

       //  ========================================
       //  平台部分
       //  ========================================

        // 商品新增量
        Integer newProductNum = productService.getNewProductByDate(yesterdayStr);

        // 浏览量
        int pageViewNum = 0;
        Object pageViewObject = redisUtil.get(RedisConstants.PRO_PAGE_VIEW_KEY + yesterdayStr);
        if (ObjectUtil.isNotNull(pageViewObject)) {
            pageViewNum = (Integer) pageViewObject;
        }

        // 收藏量
        Integer collectNum = productRelationService.getCountByDate(yesterdayStr);

        // 加购量
        int addCartNum = 0;
        Object addCartObject = redisUtil.get(RedisConstants.PRO_ADD_CART_KEY + yesterdayStr);
        if (ObjectUtil.isNotNull(addCartObject)) {
            addCartNum = (Integer) addCartObject;
        }

        // 交易总件数
        Integer orderProductNum = orderService.getOrderProductNumByDate(yesterdayStr);

        // 交易成功件数
        Integer orderSuccessProductNum = orderService.getOrderSuccessProductNumByDate(yesterdayStr);

        // 保存商品统计数据（商城每日）
        ShoppingProductDayRecord shoppingProductDayRecord = new ShoppingProductDayRecord();
        shoppingProductDayRecord.setDate(yesterdayStr);
        shoppingProductDayRecord.setAddProductNum(newProductNum);
        shoppingProductDayRecord.setPageView(pageViewNum);
        shoppingProductDayRecord.setCollectNum(collectNum);
        shoppingProductDayRecord.setAddCartNum(addCartNum);
        shoppingProductDayRecord.setOrderProductNum(orderProductNum);
        shoppingProductDayRecord.setOrderSuccessProductNum(orderSuccessProductNum);

        //  ========================================
        //  商品个体部分
        //  ========================================

        // 获取所有未删除的商品
        List<Product> allProduct = productService.findAllProductByNotDelete();
        List<ProductDayRecord> productDayRecordList = allProduct.stream().map(e -> {
            ProductDayRecord productDayRecord = new ProductDayRecord();
            productDayRecord.setProductId(e.getId());
            productDayRecord.setMerId(e.getMerId());
            productDayRecord.setDate(yesterdayStr);
            return productDayRecord;
        }).collect(Collectors.toList());

        List<String> redisKeyList = new ArrayList<>();
        productDayRecordList.forEach(e -> {
            // 浏览量
            int pageViewDetailNum = 0;
            Object pageViewObject1 = redisUtil.get(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, yesterdayStr, e.getProductId()));
            if (ObjectUtil.isNotNull(pageViewObject1)) {
                pageViewDetailNum = (Integer) pageViewObject1;
                redisKeyList.add(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, yesterdayStr, e.getProductId()));
            }
            e.setPageView(pageViewDetailNum);
            // 收藏量
            Integer collectDetailNum = productRelationService.getCountByDateAndProId(yesterdayStr, e.getProductId());
            e.setCollectNum(collectDetailNum);
            // 加购件数
            int addCartDetailNum = 0;
            Object addCartObject1 = redisUtil.get(StrUtil.format(RedisConstants.PRO_PRO_ADD_CART_KEY, yesterdayStr, e.getProductId()));
            if (ObjectUtil.isNotNull(addCartObject1)) {
                addCartDetailNum = (Integer) addCartObject1;
                redisKeyList.add(StrUtil.format(RedisConstants.PRO_PRO_ADD_CART_KEY, yesterdayStr, e.getProductId()));
            }
            e.setAddCartNum(addCartDetailNum);
            // 销量
            e.setOrderProductNum(orderDetailService.getSalesNumByDateAndProductId(yesterdayStr, e.getProductId()));
            // 销售额
            e.setOrderSuccessProductFee(orderDetailService.getSalesByDateAndProductId(yesterdayStr, e.getProductId()));
        });

        //  ========================================
        //  交易记录/日
        //  ========================================

        // 订单数量
        Integer orderNum = orderService.getOrderNumByDate(0, yesterdayStr);

        // 订单支付数量
        Integer payOrderNum = orderService.getPayOrderNumByDate(yesterdayStr);

        // 订单支付金额
        BigDecimal payOrderAmount = orderService.getPayOrderAmountByDate(0, yesterdayStr);

        // 订单退款数量
        Integer refundOrderNum = refundOrderService.getRefundOrderNumByDate(yesterdayStr);

        // 订单退款金额
        BigDecimal refundOrderAmount = refundOrderService.getRefundOrderAmountByDate(yesterdayStr);

        TradingDayRecord tradingDayRecord = new TradingDayRecord();
        tradingDayRecord.setDate(yesterdayStr);
        tradingDayRecord.setProductOrderNum(orderNum);
        tradingDayRecord.setProductOrderPayNum(payOrderNum);
        tradingDayRecord.setProductOrderPayFee(payOrderAmount);
        tradingDayRecord.setProductOrderRefundNum(refundOrderNum);
        tradingDayRecord.setProductOrderRefundFee(refundOrderAmount);

        //  ========================================
        //  商户记录/日
        //  ========================================
        List<Merchant> merchantList = merchantService.all();
        List<MerchantDayRecord> merchantDayRecordList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(merchantList)) {
            for (Merchant merchant : merchantList) {
                MerchantDayRecord merchantDayRecord = new MerchantDayRecord();
                merchantDayRecord.setMerId(merchant.getId());
                merchantDayRecord.setDate(yesterdayStr);
                int merchantVisitors = 0;
                Object visitorsObject = redisUtil.get(StrUtil.format(RedisConstants.MERCHANT_VISITORS_KEY, yesterdayStr, merchant.getId()));
                if (ObjectUtil.isNotNull(visitorsObject)) {
                    merchantVisitors = (Integer) visitorsObject;
                    redisKeyList.add(StrUtil.format(RedisConstants.MERCHANT_VISITORS_KEY, yesterdayStr, merchant.getId()));
                }
                merchantDayRecord.setVisitors(merchantVisitors);
                merchantDayRecordList.add(merchantDayRecord);
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            shoppingProductDayRecordService.save(shoppingProductDayRecord);
            tradingDayRecordService.save(tradingDayRecord);
            if (CollUtil.isNotEmpty(productDayRecordList)) {
                productDayRecordService.saveBatch(productDayRecordList, 100);
            }
            if (CollUtil.isNotEmpty(merchantDayRecordList)) {
                merchantDayRecordService.saveBatch(merchantDayRecordList, 100);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            LOGGER.error("每日统计任务保存数据库失败");
            throw new CrmebException("每日统计任务保存数据库失败");
        }
        redisUtil.delete(RedisConstants.PRO_PAGE_VIEW_KEY + yesterdayStr);
        redisUtil.delete(RedisConstants.PRO_ADD_CART_KEY + yesterdayStr);
        if (CollUtil.isNotEmpty(redisKeyList)) {
            redisKeyList.forEach(redisKey -> redisUtil.delete(redisKey));
        }
    }

    /**
     * 每日帐单定时任务
     * 每天1点执行
     * <p>
     * 生成当天数据记录，值为0
     * 为上一天的数据记录，计入数据
     */
    @Override
    public void dailyStatement() {
        List<Integer> merIdList = merchantService.getAllId();
        // 写入记录
        MyRecord record = writeDailyStatement();
        Integer status = record.getInt("status");
        if (status.equals(0)) {
            // 初始化当天数据
            Boolean init = initDailyStatement(merIdList);
            if (!init) {
                LOGGER.error("每日帐单定时任务,初始化当天数据,触发回滚");
            }
            return;
        }
        PlatformDailyStatement platformDailyStatement = record.get("platformDailyStatement");
        Integer listSize = record.getInt("listSize");
        transactionTemplate.execute(e -> {
            boolean update = platformDailyStatementService.updateById(platformDailyStatement);
            if (!update) {
                LOGGER.error("每日帐单定时任务,更新平台日帐单失败,触发回滚");
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (listSize > 0) {
                List<MerchantDailyStatement> merchantDailyStatementList = record.get("merchantDailyStatementList");
                update = merchantDailyStatementService.updateBatchById(merchantDailyStatementList, 100);
                if (!update) {
                    LOGGER.error("每日帐单定时任务,批量更新商户日帐单失败,触发回滚");
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            // 初始化当天数据
            update = initDailyStatement(merIdList);
            if (!update) {
                LOGGER.error("每日帐单定时任务,初始化当天数据,触发回滚");
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });

    }

    /**
     * 每月帐单定时任务
     * 每月第一天2点执行
     * 获取上个月所有的帐单记录，然后通过累加获取月帐单记录
     * 生成当月帐单记录，数值为0
     */
    @Override
    public void monthStatement() {
        String lastMonth = DateUtil.lastMonth().toString(DateConstants.DATE_FORMAT_MONTH);
        PlatformMonthStatement platformMonthStatement = platformMonthStatementService.getByMonth(lastMonth);
        if (ObjectUtil.isNull(platformMonthStatement)) {
            return;
        }
        List<Integer> merIdList = merchantService.getAllId();
        writePlatformMonthStatement(platformMonthStatement);
        List<MerchantMonthStatement> merchantMonthStatementList = merchantMonthStatementService.findByMonth(lastMonth);
        if (CollUtil.isEmpty(merchantMonthStatementList)) {
            transactionTemplate.execute(e -> {
                boolean save = platformMonthStatementService.updateById(platformMonthStatement);
                if (!save) {
                    LOGGER.error("每月帐单定时任务,更新平台月帐单失败,触发回滚");
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                save = initMonthStatement(merIdList);
                if (!save) {
                    LOGGER.error("每月帐单定时任务,初始化平台当月帐单失败,触发回滚");
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            });
            return;
        }
        writeMerchantMonthStatement(merchantMonthStatementList, lastMonth);
        transactionTemplate.execute(e -> {
            boolean save = platformMonthStatementService.updateById(platformMonthStatement);
            if (!save) {
                LOGGER.error("每月帐单定时任务,更新平台月帐单失败,触发回滚");
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            save = merchantMonthStatementService.updateBatchById(merchantMonthStatementList, 100);
            if (!save) {
                LOGGER.error("每月帐单定时任务,更新商户月帐单失败,触发回滚");
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            save = initMonthStatement(merIdList);
            if (!save) {
                LOGGER.error("每月帐单定时任务,初始化平台当月帐单失败,触发回滚");
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }

    private Boolean initMonthStatement(List<Integer> merIdList) {
        String nowMonth = DateUtil.date().toString(DateConstants.DATE_FORMAT_MONTH);
        PlatformMonthStatement platformMonthStatement = new PlatformMonthStatement();
        platformMonthStatement.setDataDate(nowMonth);
        if (CollUtil.isEmpty(merIdList)) {
            return platformMonthStatementService.save(platformMonthStatement);
        }
        List<MerchantMonthStatement> merchantMonthStatementList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(merIdList)) {
            merIdList.forEach(merId -> {
                MerchantMonthStatement merchantMonthStatement = new MerchantMonthStatement();
                merchantMonthStatement.setMerId(merId);
                merchantMonthStatement.setDataDate(nowMonth);
                merchantMonthStatementList.add(merchantMonthStatement);
            });
        }
        boolean save = platformMonthStatementService.save(platformMonthStatement);
        if (!save) {
            return save;
        }
        return merchantMonthStatementService.saveBatch(merchantMonthStatementList, 100);
    }

    private void writeMerchantMonthStatement(List<MerchantMonthStatement> merchantMonthStatementList, String lastMonth) {
        List<MerchantDailyStatement> merchantDailyStatementList = merchantDailyStatementService.findByMonth(lastMonth);
        if (CollUtil.isEmpty(merchantDailyStatementList)) {
            return;
        }
        merchantMonthStatementList.forEach(monthStatement -> {
            List<MerchantDailyStatement> dailyStatementList = merchantDailyStatementList.stream().filter(e -> e.getMerId().equals(monthStatement.getMerId())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(dailyStatementList)) {
                BigDecimal orderIncomeAmount = dailyStatementList.stream().map(MerchantDailyStatement::getOrderIncomeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal orderPayAmount = dailyStatementList.stream().map(MerchantDailyStatement::getOrderPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                int orderNum = dailyStatementList.stream().mapToInt(MerchantDailyStatement::getOrderNum).sum();
                BigDecimal handlingFee = dailyStatementList.stream().map(MerchantDailyStatement::getHandlingFee).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal payoutAmount = dailyStatementList.stream().map(MerchantDailyStatement::getPayoutAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                int payoutNum = dailyStatementList.stream().mapToInt(MerchantDailyStatement::getPayoutNum).sum();
                BigDecimal refundAmount = dailyStatementList.stream().map(MerchantDailyStatement::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                int refundNum = dailyStatementList.stream().mapToInt(MerchantDailyStatement::getRefundNum).sum();
                BigDecimal incomeExpenditure = dailyStatementList.stream().map(MerchantDailyStatement::getIncomeExpenditure).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal firstBrokerage = dailyStatementList.stream().map(MerchantDailyStatement::getFirstBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal secondBrokerage = dailyStatementList.stream().map(MerchantDailyStatement::getSecondBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add);
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
        });
    }

    private void writePlatformMonthStatement(PlatformMonthStatement platformMonthStatement) {
        List<PlatformDailyStatement> dailyStatementList = platformDailyStatementService.findByMonth(platformMonthStatement.getDataDate());
        if (CollUtil.isEmpty(dailyStatementList)) {
            return;
        }
        BigDecimal orderPayAmount = dailyStatementList.stream().map(PlatformDailyStatement::getOrderPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalOrderNum = dailyStatementList.stream().mapToInt(PlatformDailyStatement::getTotalOrderNum).sum();
        BigDecimal handlingFee = dailyStatementList.stream().map(PlatformDailyStatement::getHandlingFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal payoutAmount = dailyStatementList.stream().map(PlatformDailyStatement::getPayoutAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int payoutNum = dailyStatementList.stream().mapToInt(PlatformDailyStatement::getPayoutNum).sum();
        BigDecimal merchantTransferAmount = dailyStatementList.stream().map(PlatformDailyStatement::getMerchantTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int merchantTransferNum = dailyStatementList.stream().mapToInt(PlatformDailyStatement::getMerchantTransferNum).sum();
        BigDecimal refundAmount = dailyStatementList.stream().map(PlatformDailyStatement::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int refundNum = dailyStatementList.stream().mapToInt(PlatformDailyStatement::getRefundNum).sum();
        BigDecimal incomeExpenditure = dailyStatementList.stream().map(PlatformDailyStatement::getIncomeExpenditure).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal firstBrokerage = dailyStatementList.stream().map(PlatformDailyStatement::getFirstBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal secondBrokerage = dailyStatementList.stream().map(PlatformDailyStatement::getSecondBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal integralPrice = dailyStatementList.stream().map(PlatformDailyStatement::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundReplaceBrokerage = dailyStatementList.stream().map(PlatformDailyStatement::getRefundReplaceBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundReplaceIntegralPrice = dailyStatementList.stream().map(PlatformDailyStatement::getRefundReplaceIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rechargeAmount = dailyStatementList.stream().map(PlatformDailyStatement::getRechargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int rechargeNum = dailyStatementList.stream().mapToInt(PlatformDailyStatement::getRechargeNum).sum();
        platformMonthStatement.setOrderPayAmount(orderPayAmount);
        platformMonthStatement.setTotalOrderNum(totalOrderNum);
        platformMonthStatement.setHandlingFee(handlingFee);
        platformMonthStatement.setMerchantTransferAmount(merchantTransferAmount);
        platformMonthStatement.setMerchantTransferNum(merchantTransferNum);
        platformMonthStatement.setFirstBrokerage(firstBrokerage);
        platformMonthStatement.setSecondBrokerage(secondBrokerage);
        platformMonthStatement.setIntegralPrice(integralPrice);
        platformMonthStatement.setPayoutAmount(payoutAmount);
        platformMonthStatement.setPayoutNum(payoutNum);
        platformMonthStatement.setRefundAmount(refundAmount);
        platformMonthStatement.setRefundReplaceBrokerage(refundReplaceBrokerage);
        platformMonthStatement.setRefundReplaceIntegralPrice(refundReplaceIntegralPrice);
        platformMonthStatement.setRefundNum(refundNum);
        platformMonthStatement.setRechargeAmount(rechargeAmount);
        platformMonthStatement.setRechargeNum(rechargeNum);
        platformMonthStatement.setIncomeExpenditure(incomeExpenditure);
    }

    /**
     * 写入记录
     *
     * @return MyRecord
     */
    private MyRecord writeDailyStatement() {
        MyRecord myRecord = new MyRecord();
        /*为上一天的记录写入数据*/
        String yesterday = DateUtil.yesterday().toDateStr();
        // 平台帐单
        PlatformDailyStatement platformDailyStatement = platformDailyStatementService.getByDate(yesterday);
        if (ObjectUtil.isNull(platformDailyStatement)) {
            myRecord.set("status", 0);
            return myRecord;
        }
        List<OrderProfitSharing> sharingList = orderProfitSharingService.findByDate(0, yesterday);
        List<RefundOrder> refundOrderList = refundOrderService.findByDate(0, yesterday);
        List<RechargeOrder> rechargeOrderList = rechargeOrderService.findByDate(yesterday);
        // 平台记录写入
        platformDataWrite(platformDailyStatement, sharingList, refundOrderList, rechargeOrderList);
        myRecord.set("status", 1);
        myRecord.set("platformDailyStatement", platformDailyStatement);
        // 商户记录写入
        List<MerchantDailyStatement> merchantDailyStatementList = merchantDailyStatementService.findByDate(yesterday);
        if (CollUtil.isEmpty(merchantDailyStatementList)) {
            myRecord.set("listSize", 0);
            return myRecord;
        }
        merchantDataWrite(merchantDailyStatementList, sharingList, refundOrderList);
        myRecord.set("listSize", merchantDailyStatementList.size());
        myRecord.set("merchantDailyStatementList", merchantDailyStatementList);
        return myRecord;
    }

    /**
     * 商户记录写入
     *
     * @param merchantDailyStatementList 商户日账单列表
     * @param sharingList                分账列表
     * @param refundOrderList                退款订单列表
     */
    private void merchantDataWrite(List<MerchantDailyStatement> merchantDailyStatementList, List<OrderProfitSharing> sharingList, List<RefundOrder> refundOrderList) {
        if (CollUtil.isEmpty(merchantDailyStatementList)) {
            return;
        }
        merchantDailyStatementList.forEach(statement -> {
            // 订单支付总金额
            BigDecimal orderPayAmount = BigDecimal.ZERO;
            // 订单支付笔数
            int orderNum = 0;
            // 商户分账金额
            BigDecimal orderIncomeAmount = BigDecimal.ZERO;
            // 平台手续费
            BigDecimal handlingFee = BigDecimal.ZERO;
            // 一二级分佣金额
            BigDecimal firstBrokerage = BigDecimal.ZERO;
            BigDecimal secondBrokerage = BigDecimal.ZERO;
            // 商户退款金额
            BigDecimal refundAmount = BigDecimal.ZERO;
            // 退款笔数
            int refundNum = 0;
            if (CollUtil.isNotEmpty(sharingList)) {
                List<OrderProfitSharing> merchantSharingList = sharingList.stream().filter(e -> e.getMerId().equals(statement.getMerId())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(merchantSharingList)) {
                    orderPayAmount =  merchantSharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    orderNum = merchantSharingList.size();
                    orderIncomeAmount = merchantSharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    handlingFee = merchantSharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    firstBrokerage = merchantSharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
                    secondBrokerage = merchantSharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
            if (CollUtil.isNotEmpty(refundOrderList)) {
                List<RefundOrder> merRefundOrderList = refundOrderList.stream().filter(e -> e.getMerId().equals(statement.getMerId())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(merRefundOrderList)) {
                    refundAmount = merRefundOrderList.stream().map(RefundOrder::getMerchantRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    refundNum = merRefundOrderList.size();
                }
            }
            // 支出总金额 = 商户退款金额
            BigDecimal payoutAmount = refundAmount;
            // 支出笔数 = 退款笔数
            int payoutNum = refundNum;
            // 商户日收支 = 订单收入金额 - 商户退款金额
            BigDecimal incomeExpenditure = orderIncomeAmount.subtract(refundAmount);

            statement.setOrderPayAmount(orderPayAmount);
            statement.setOrderNum(orderNum);
            statement.setOrderIncomeAmount(orderIncomeAmount);
            statement.setHandlingFee(handlingFee);
            statement.setFirstBrokerage(firstBrokerage);
            statement.setSecondBrokerage(secondBrokerage);
            statement.setPayoutAmount(payoutAmount);
            statement.setPayoutNum(payoutNum);
            statement.setRefundAmount(refundAmount);
            statement.setRefundNum(refundNum);
            statement.setIncomeExpenditure(incomeExpenditure);
        });
    }

    /**
     * 平台记录写入
     *
     * @param platformDailyStatement 平台日帐单
     * @param sharingList            分账列表
     * @param refundOrderList        退款列表
     * @param rechargeOrderList      充值列表
     */
    private void platformDataWrite(PlatformDailyStatement platformDailyStatement, List<OrderProfitSharing> sharingList, List<RefundOrder> refundOrderList, List<RechargeOrder> rechargeOrderList) {
        // 订单支付总金额
        BigDecimal orderPayAmount = BigDecimal.ZERO;
        // 总订单支付笔数
        int orderPayNum = 0;
        // 日手续费收入
        BigDecimal handlingFee = BigDecimal.ZERO;
        // 商户分账金额
        BigDecimal merchantTransferAmount = BigDecimal.ZERO;
        // 商户分账笔数
        int merchantTransferNum = 0;
        // 平台退款金额
        BigDecimal refundAmount = BigDecimal.ZERO;
        // 平台代扣佣金金额
        BigDecimal refundReplaceBrokerage = BigDecimal.ZERO;
        // 退款平台积分抵扣金额
        BigDecimal refundReplaceIntegralPrice = BigDecimal.ZERO;
        // 退款笔数
        int refundOrderNum = 0;
        // 一级佣金金额
        BigDecimal firstBrokerage = BigDecimal.ZERO;
        // 二级佣金额
        BigDecimal secondBrokerage = BigDecimal.ZERO;
        // 充值金额
        BigDecimal rechargeAmount = BigDecimal.ZERO;
        // 充值笔数
        int rechargeNum = 0;
        // 订单积分抵扣金额
        BigDecimal integralPrice = BigDecimal.ZERO;

        if (CollUtil.isNotEmpty(sharingList)) {
            orderPayAmount =  sharingList.stream().map(OrderProfitSharing::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderPayNum = sharingList.size();
            handlingFee = sharingList.stream().map(OrderProfitSharing::getProfitSharingPlatPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantTransferAmount = sharingList.stream().map(OrderProfitSharing::getProfitSharingMerPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            firstBrokerage = sharingList.stream().map(OrderProfitSharing::getFirstBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            secondBrokerage = sharingList.stream().map(OrderProfitSharing::getSecondBrokerageFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantTransferNum = sharingList.size();
            integralPrice = sharingList.stream().map(OrderProfitSharing::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (CollUtil.isNotEmpty(refundOrderList)) {
            refundAmount = refundOrderList.stream().map(RefundOrder::getPlatformRefundPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundReplaceBrokerage = refundOrderList.stream().filter(RefundOrder::getIsReplace).map(e -> e.getRefundFirstBrokerageFee().add(e.getRefundSecondBrokerageFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundReplaceIntegralPrice = refundOrderList.stream().map(RefundOrder::getRefundIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            refundOrderNum = refundOrderList.size();
        }
        if (CollUtil.isNotEmpty(rechargeOrderList)) {
            rechargeNum = rechargeOrderList.size();
            rechargeAmount = rechargeOrderList.stream().map(RechargeOrder::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 收入 = 订单支付总金额 + 退订单支付积分抵扣金额
        BigDecimal income = orderPayAmount.add(refundReplaceIntegralPrice);
        // 支出 = 商户分账金额 + 一级佣金 + 二级佣金 + 订单支付积分抵扣金额 + 平台退款金额 + 平台退款代扣佣金
        BigDecimal payoutAmount = merchantTransferAmount.add(firstBrokerage).add(secondBrokerage).add(integralPrice).add(refundAmount).add(refundReplaceBrokerage);
        // 支出笔数 = 分账笔数 + 退款笔数
        int payoutNum = merchantTransferNum + refundOrderNum;

        // 平台日收支 = 平台手续费 + 退订单支付积分抵扣金额 - 订单支付积分抵扣金额 - 平台退款金额 - 平台退款代扣佣金
        BigDecimal incomeExpenditure = handlingFee.add(refundReplaceIntegralPrice).subtract(integralPrice).subtract(refundAmount).subtract(refundReplaceBrokerage);

        platformDailyStatement.setOrderPayAmount(orderPayAmount);
        platformDailyStatement.setTotalOrderNum(orderPayNum);
        platformDailyStatement.setHandlingFee(handlingFee);
        platformDailyStatement.setMerchantTransferAmount(merchantTransferAmount);
        platformDailyStatement.setMerchantTransferNum(merchantTransferNum);
        platformDailyStatement.setFirstBrokerage(firstBrokerage);
        platformDailyStatement.setSecondBrokerage(secondBrokerage);
        platformDailyStatement.setIntegralPrice(integralPrice);
        platformDailyStatement.setPayoutAmount(payoutAmount);
        platformDailyStatement.setPayoutNum(payoutNum);
        platformDailyStatement.setRefundAmount(refundAmount);
        platformDailyStatement.setRefundReplaceBrokerage(refundReplaceBrokerage);
        platformDailyStatement.setRefundReplaceIntegralPrice(refundReplaceIntegralPrice);
        platformDailyStatement.setRefundNum(refundOrderNum);
        platformDailyStatement.setRechargeAmount(rechargeAmount);
        platformDailyStatement.setRechargeNum(rechargeNum);
        platformDailyStatement.setIncomeExpenditure(incomeExpenditure);
    }

    /**
     * 初始化当天记录（数值为0，为分页查询设计）
     */
    private Boolean initDailyStatement(List<Integer> merIdList) {
        String today = DateUtil.date().toDateStr();
        PlatformDailyStatement platformDailyStatement = new PlatformDailyStatement();
        platformDailyStatement.setDataDate(today);
        if (CollUtil.isEmpty(merIdList)) {
            return platformDailyStatementService.save(platformDailyStatement);
        }
        List<MerchantDailyStatement> merchantDailyStatementList = merIdList.stream().map(merId -> {
            MerchantDailyStatement statement = new MerchantDailyStatement();
            statement.setMerId(merId);
            statement.setDataDate(today);
            return statement;
        }).collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            boolean save = platformDailyStatementService.save(platformDailyStatement);
            if (!save) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            save = merchantDailyStatementService.saveBatch(merchantDailyStatementList, 100);
            if (!save) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
    }
}
