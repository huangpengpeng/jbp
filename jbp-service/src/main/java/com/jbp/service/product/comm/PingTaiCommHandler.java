package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PingTaiCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderService orderService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private ClearingVipUserService clearingVipUserService;
    @Resource
    private ClearingBonusService clearingBonusService;
    @Resource
    private ClearingBonusFlowService clearingBonusFlowService;

    @Override
    public Integer getType() {
        return ProductCommEnum.平台分红.getType();
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
        }
        for (Rule rule : rules) {
            if (rule.getRatio() == null || rule.getLevel() == null || StringUtils.isEmpty(rule.getLevelName())) {
                throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
            }
        }
        // 删除数据库的信息
        productCommService.remove(new LambdaQueryWrapper<ProductComm>()
                .eq(ProductComm::getProductId, productComm.getProductId())
                .eq(ProductComm::getType, productComm.getType()));
        // 保存最新的信息
        productCommService.save(productComm);
        return true;
    }

    @Override
    public List<Rule> getRule(ProductComm productComm) {
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONArray.parseArray(productCommConfig.getRatioJson(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void clearing(ClearingFinal clearingFinal) {
        // 获取当日业绩
        List<ClearingUser> clearingUsers = clearingUserService.getByClearing(clearingFinal.getId());
        Date startTime = DateTimeUtils.parseDate(clearingFinal.getStartTime());
        Date endTime = DateTimeUtils.parseDate(clearingFinal.getEndTime());
        List<Rule> ruleList = getRule(null);
        // 结算周期内支付成功的订单
        List<Order> successList = orderService.getSuccessList(startTime, endTime);
        Map<Integer, ProductComm> map = Maps.newConcurrentMap();
        BigDecimal totalScore = BigDecimal.ZERO;
        for (Order order : successList) {
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (OrderDetail orderDetail : orderDetailList) {
                ProductComm productComm = map.get(orderDetail.getProductId());
                if (productComm == null) {
                    productComm = productCommService.getByProduct(orderDetail.getProductId(), getType());
                    map.put(orderDetail.getProductId(), productComm);
                }
                // 佣金不存在或者关闭直接忽略
                if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                    continue;
                }
                BigDecimal realScore = orderDetailService.getRealScore(orderDetail);
                realScore = BigDecimal.valueOf(realScore.multiply(productComm.getScale()).intValue());
                // 结算周期汇总
                totalScore = totalScore.add(realScore);
            }
        }
        if (ArithmeticUtils.lessEquals(totalScore, BigDecimal.ZERO)) {
            log.error(clearingFinal.getName() + "结算积分为0");
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
            return;
        }
        // 获取购买赠送份额
        List<ClearingVipUser> clearingVipUsers = clearingVipUserService.
                list(new LambdaQueryWrapper<ClearingVipUser>().eq(ClearingVipUser::getStatus, 0).last(" and  usedAmount < maxAmount"));
        if (CollectionUtils.isEmpty(clearingVipUsers) && CollectionUtils.isEmpty(clearingUsers)) {
            log.error(clearingFinal.getName() + "没有获奖名单");
            clearingFinal.setTotalScore(totalScore);
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
            return;
        }
        // 两个结算名单
        Map<Long, List<ClearingUser>> cleaingUserMap = FunctionUtil.valueMap(clearingUsers, ClearingUser::getLevel);
        Map<Long, List<ClearingVipUser>> cleaingVipUserMap = FunctionUtil.valueMap(clearingVipUsers, ClearingVipUser::getLevel);
        // 获取规则
        List<Rule> rules = getRule(null);
        rules = rules.stream().filter(s -> s.getRefLevel() == null).collect(Collectors.toList());
        // 每个等级单价
        Map<Long, BigDecimal> priceMap = Maps.newConcurrentMap();
        for (Rule rule : rules) {
            priceMap.put(rule.getLevel(), BigDecimal.ZERO);
            List<ClearingUser> clearingUserList = cleaingUserMap.get(rule.getLevel());
            Integer size = CollectionUtils.isEmpty(clearingUserList) ? 0 : clearingUserList.size();
            List<ClearingVipUser> clearingVipUserList = cleaingVipUserMap.get(rule.getLevel());
            Integer size2 = CollectionUtils.isEmpty(clearingVipUserList) ? 0 : clearingVipUserList.size();
            size = size + size2;

            BigDecimal totalAmt = totalScore.multiply(rule.getRatio());
            if (size > 0 && ArithmeticUtils.gt(totalAmt, BigDecimal.ZERO)) {
                BigDecimal price = totalAmt.divide(BigDecimal.valueOf(size), 0, BigDecimal.ROUND_UP);
                if (ArithmeticUtils.gt(price, BigDecimal.ZERO)) {
                    priceMap.put(rule.getLevel(), price);
                }
            }
        }
        // 个人佣金汇总
        LinkedList<ClearingBonus> clearingBonusList = new LinkedList<>();
        // 分佣明细
        LinkedList<ClearingBonusFlow> clearingBonusFlowList = new LinkedList<>();
        // 总佣金
        BigDecimal totalFee = BigDecimal.ZERO;
        // 分钱
        for (ClearingVipUser clearingVipUser : clearingVipUsers) {
            BigDecimal price = priceMap.get(clearingVipUser.getLevel());
            BigDecimal usedAmount = clearingVipUser.getUsedAmount();
            BigDecimal usableAmt = clearingVipUser.getMaxAmount().subtract(clearingVipUser.getUsedAmount());
            BigDecimal realFee = BigDecimal.valueOf(Math.min(price.doubleValue(), usableAmt.doubleValue()));
            clearingVipUser.setUsedAmount(clearingVipUser.getUsedAmount().add(realFee));
            clearingVipUserService.updateById(clearingVipUser);
            ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingVipUser.getUid(), clearingVipUser.getAccountNo(),
                    clearingVipUser.getLevel(), clearingVipUser.getLevelName(),
                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                    realFee, "购买商品赠送分红等级:" + clearingVipUser.getLevelName() + "额度上限:" + clearingVipUser.getMaxAmount() + "已获金额:" + usedAmount, clearingVipUser.getRule());
            clearingBonusFlowList.add(clearingBonusFlow);

            // 佣金
            ClearingBonus clearingBonus = new ClearingBonus(clearingVipUser.getUid(), clearingVipUser.getAccountNo(), clearingVipUser.getLevel(), clearingVipUser.getLevelName(),
                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(), StringUtils.N_TO_10("PT_"), realFee);
            clearingBonusList.add(clearingBonus);

            totalFee = totalFee.add(realFee);
        }
        for (ClearingUser clearingUser : clearingUsers) {
            BigDecimal realFee = priceMap.get(clearingUser.getLevel());
            ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingUser.getUid(), clearingUser.getAccountNo(),
                    clearingUser.getLevel(), clearingUser.getLevelName(),
                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                    realFee, "累积业绩符合" + clearingUser.getLevelName() + "要求", clearingUser.getRule());
            clearingBonusFlowList.add(clearingBonusFlow);
            // 个人汇总
            ClearingBonus clearingBonus = new ClearingBonus(clearingUser.getUid(), clearingUser.getAccountNo(), clearingUser.getLevel(), clearingUser.getLevelName(),
                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(), StringUtils.N_TO_10("PT_"), realFee);
            clearingBonusList.add(clearingBonus);

            totalFee = totalFee.add(realFee);
        }

        // 保存明细
        List<List<ClearingBonusFlow>> partition = Lists.partition(clearingBonusFlowList, 2000);
        for (List<ClearingBonusFlow> clearingBonusFlows : partition) {
            clearingBonusFlowService.insertBatchList(clearingBonusFlows);
        }

        List<List<ClearingBonus>> clearingBonuss = Lists.partition(clearingBonusList, 2000);
        for (List<ClearingBonus> bonuss : clearingBonuss) {
            clearingBonusService.insertBatchList(bonuss);
        }
        // 更新结算信息
        clearingFinal.setTotalScore(totalScore);
        clearingFinal.setTotalAmt(totalFee);
        clearingFinal.setStatus(ClearingFinal.Constants.待出款.name());
        clearingFinalService.updateById(clearingFinal);
    }

    @Override
    public void del4Clearing(ClearingFinal clearingFinal) {
        clearingBonusService.del4Clearing(clearingFinal.getId());
        clearingBonusFlowService.del4Clearing(clearingFinal.getId());
    }

    public static void main(String[] args) {

        List<Rule> ruleList = Lists.newArrayList();
        for (int i = 0; i < 4 ; i++) {
            Rule rule = new Rule();
            rule.setLevel(Long.valueOf(i));
            rule.setLevelName("平台分红级别"+i);
            rule.setRatio(BigDecimal.valueOf(i).divide(BigDecimal.valueOf(100)));
            ruleList.add(rule);
        }

        System.out.println(JSONArray.toJSONString(ruleList));


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 结算级别
         */
        private Long level;

        /**
         * 结算名称
         */
        private String levelName;

        /**
         * 比例
         */
        private BigDecimal ratio;

        /**
         * 累积奖金
         */
        private BigDecimal minScore;

        /**
         * 关联结算级别
         */
        private Long refLevel;

        /**
         * 最大金额
         */
        private BigDecimal maxFee;
    }
}
