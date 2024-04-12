package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPatch;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 月结培育佣金
 */
@Slf4j
@Component
public class MonthPyCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private ClearingBonusService clearingBonusService;
    @Resource
    private ClearingBonusFlowService clearingBonusFlowService;
    @Resource
    private ClearingInvitationFlowService invitationFlowService;
    @Resource
    private ClearingRelationFlowService relationFlowService;

    @Override
    public Integer getType() {
        return ProductCommEnum.培育佣金.getType();
    }


    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.培育佣金.getName() + "参数不完整");
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
    public Map<Long, Rule> getRule(ProductComm productComm) {
        try {
            Map<Long, Rule> map = Maps.newConcurrentMap();
            ProductCommConfig config = productCommConfigService.getByType(getType());
            JSONArray array = JSONArray.parseArray(config.getRatioJson());
            for (int i = 0; i < array.size(); i++) {
                Rule rule = array.getJSONObject(i).toJavaObject(Rule.class);
                map.put(rule.getLevel(), rule);
            }
            return map;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void clearing(ClearingFinal clearingFinal) {
        List<ClearingUser> clearingUsers = clearingUserService.getByClearing(clearingFinal.getId());
        Map<Integer, ClearingUser> cleaingUserMap = FunctionUtil.keyValueMap(clearingUsers, ClearingUser::getUid);
        Date startTime = DateTimeUtils.parseDate(clearingFinal.getStartTime());
        Date endTime = DateTimeUtils.parseDate(clearingFinal.getEndTime());
        List<Order> successList = orderService.getSuccessList(startTime, endTime);

        // 计算积分汇总
        BigDecimal totalScore = BigDecimal.ZERO;
        Map<Integer, ProductComm> map = Maps.newConcurrentMap();
        // 服务下拿总金额
        Map<Integer, BigDecimal> underRelationUserFeeMap = Maps.newConcurrentMap();
        // 销售下拿总金额
        Map<Integer, BigDecimal> uuperInvitationUserFeeMap = Maps.newConcurrentMap();
        // 明细
        LinkedList<ClearingBonusFlow> clearingBonusFlowList = new LinkedList<>();
        for (Order order : successList) {
            BigDecimal orderScore = BigDecimal.ZERO;
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
                // 订单积分汇总
                orderScore = orderScore.add(realScore);
                // 结算周期汇总
                totalScore = totalScore.add(realScore);
            }

            // 培育业绩不是大于0直接忽略
            if (!ArithmeticUtils.gt(orderScore, BigDecimal.ZERO)) {
                continue;
            }

            // 购买人往上拿钱25层
            List<ClearingRelationFlow> relationUpperList = relationFlowService.getByUser(order.getUid(), 25);
            relationUpperList = relationUpperList.stream().sorted(Comparator.comparing(ClearingRelationFlow::getLevel)).collect(Collectors.toList());
            // 下拿分钱
            for (ClearingRelationFlow flow : relationUpperList) {
                ClearingUser clearingUser = cleaingUserMap.get(flow.getPId());
                if (clearingUser != null) {
                    Rule rule = JSONObject.parseObject(clearingUser.getRule(), Rule.class);
                    if (rule.getUnderRelationNum() >= flow.getLevel().intValue()) {
                        BigDecimal commFee = orderScore.multiply(rule.getUnderScale()).setScale(2, BigDecimal.ROUND_UP);
                        if (ArithmeticUtils.gt(commFee, BigDecimal.ZERO)) {
                            ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(flow.getPId(), clearingUser.getAccountNo(),
                                    clearingUser.getLevel(), clearingUser.getLevelName(),
                                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                                    commFee, "服务下拿-" + "单号:" + order.getOrderNo() + "总积分:" + orderScore + "代数:" + flow.getLevel(), clearingUser.getRule());
                            clearingBonusFlowList.add(clearingBonusFlow);
                            // 个人金额汇总
                            BigDecimal fee = commFee.add(BigDecimal.valueOf(MapUtils.getDouble(underRelationUserFeeMap, flow.getPId(), 0.0)));
                            underRelationUserFeeMap.put(flow.getPId(), fee);
                        }
                    }
                }
            }

            // 购买人往上拿钱25代
            List<ClearingInvitationFlow> invitationUpperList = invitationFlowService.getByUser(order.getUid(), 25);
            invitationUpperList = invitationUpperList.stream().sorted(Comparator.comparing(ClearingInvitationFlow::getLevel)).collect(Collectors.toList());
            // 下拿分钱
            for (ClearingInvitationFlow flow : invitationUpperList) {
                ClearingUser clearingUser = cleaingUserMap.get(flow.getPId());
                if (clearingUser != null) {
                    Rule rule = JSONObject.parseObject(clearingUser.getRule(), Rule.class);
                    if (rule.getUnderInvitationNum() >= flow.getLevel().intValue()) {
                        BigDecimal commFee = orderScore.multiply(rule.getUnderScale()).setScale(2, BigDecimal.ROUND_UP);
                        if (ArithmeticUtils.gt(commFee, BigDecimal.ZERO)) {
                            ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(flow.getPId(), clearingUser.getAccountNo(),
                                    clearingUser.getLevel(), clearingUser.getLevelName(),
                                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                                    commFee, "销售下拿-" + "单号:" + order.getOrderNo() + "总积分:" + orderScore + "代数:" + flow.getLevel(), clearingUser.getRule());
                            clearingBonusFlowList.add(clearingBonusFlow);
                            // 个人金额汇总
                            BigDecimal fee = commFee.add(BigDecimal.valueOf(MapUtils.getDouble(uuperInvitationUserFeeMap, flow.getPId(), 0.0)));
                            uuperInvitationUserFeeMap.put(flow.getPId(), fee);
                        }
                    }
                }
            }
        }

        // 服务关系上拿
        underRelationUserFeeMap.forEach((uid, commFee) -> {
            // 查询下级客户
            ClearingUser self = cleaingUserMap.get(uid);
            BigDecimal totalFee = commFee.multiply(JSONObject.parseObject(self.getRule(), Rule.class).getUpperScale());
            List<ClearingRelationFlow> relationFlows = relationFlowService.getByPUser(uid);
            if (CollectionUtils.isNotEmpty(relationFlows)) {
                Map<Integer, List<ClearingRelationFlow>> relationListMap = FunctionUtil.valueMap(relationFlows, ClearingRelationFlow::getLevel);
                for (int i = 1; i <= 10; i++) {
                    List<ClearingRelationFlow> clearingRelationFlows = relationListMap.get(Integer.valueOf(i));
                    if (CollectionUtils.isEmpty(clearingRelationFlows)) {
                        break;
                    }
                    List<ClearingUser> usableList = Lists.newArrayList();
                    for (ClearingRelationFlow clearingRelationFlow : clearingRelationFlows) {
                        ClearingUser clearingUser = cleaingUserMap.get(clearingRelationFlow.getPId());
                        Rule rule = JSONObject.parseObject(clearingUser.getRule(), Rule.class);
                        if (rule.getUpperRelationNum() >= i) {
                            usableList.add(clearingUser);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(usableList)) {
                        BigDecimal clearingFee = totalFee.divide(BigDecimal.valueOf(usableList.size()), 2, BigDecimal.ROUND_UP);
                        if (ArithmeticUtils.gt(clearingFee, BigDecimal.ZERO)) {
                            for (ClearingUser clearingUser : usableList) {
                                ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingUser.getUid(), clearingUser.getAccountNo(),
                                        clearingUser.getLevel(), clearingUser.getLevelName(),
                                        clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                                        commFee, "服务上拿-" + "层数:" + i + "总金额:" + totalFee + "提供账户:" + self.getAccountNo(), clearingUser.getRule());
                                clearingBonusFlowList.add(clearingBonusFlow);
                            }
                        }
                    }
                }
            }
        });

        // 销售关系上拿
        uuperInvitationUserFeeMap.forEach((uid, commFee) -> {
            // 查询下级客户
            ClearingUser self = cleaingUserMap.get(uid);
            BigDecimal totalFee = commFee.multiply(JSONObject.parseObject(self.getRule(), Rule.class).getUpperScale());
            List<ClearingInvitationFlow> invitationFlows = invitationFlowService.getByPUser(uid);
            if (CollectionUtils.isNotEmpty(invitationFlows)) {
                Map<Integer, List<ClearingInvitationFlow>> invitationListMap = FunctionUtil.valueMap(invitationFlows, ClearingInvitationFlow::getLevel);
                for (int i = 1; i <= 10; i++) {
                    List<ClearingInvitationFlow> clearingInvitationFlows = invitationListMap.get(Integer.valueOf(i));
                    if (CollectionUtils.isEmpty(clearingInvitationFlows)) {
                        break;
                    }
                    List<ClearingUser> usableList = Lists.newArrayList();
                    for (ClearingInvitationFlow clearingInvitationFlow : clearingInvitationFlows) {
                        ClearingUser clearingUser = cleaingUserMap.get(clearingInvitationFlow.getPId());
                        Rule rule = JSONObject.parseObject(clearingUser.getRule(), Rule.class);
                        if (rule.getUpperInvitationNum() >= i) {
                            usableList.add(clearingUser);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(usableList)) {
                        BigDecimal clearingFee = totalFee.divide(BigDecimal.valueOf(usableList.size()), 2, BigDecimal.ROUND_UP);
                        if (ArithmeticUtils.gt(clearingFee, BigDecimal.ZERO)) {
                            for (ClearingUser clearingUser : usableList) {
                                ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingUser.getUid(), clearingUser.getAccountNo(),
                                        clearingUser.getLevel(), clearingUser.getLevelName(),
                                        clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                                        commFee, "销售上拿-" + "层数:" + i + "总金额:" + totalFee + "提供账户:" + self.getAccountNo(), clearingUser.getRule());
                                clearingBonusFlowList.add(clearingBonusFlow);
                            }
                        }
                    }
                }
            }
        });

        // 根据明细统计金额汇总
        Map<Integer, BigDecimal> userFeeMap = Maps.newConcurrentMap();
        BigDecimal totalFee = BigDecimal.ZERO;
        for (ClearingBonusFlow clearingBonusFlow : clearingBonusFlowList) {
            totalFee = totalFee.add(clearingBonusFlow.getCommAmt());
            // 个人金额汇总
            BigDecimal fee = clearingBonusFlow.getCommAmt().add(BigDecimal.valueOf(MapUtils.getDouble(userFeeMap, clearingBonusFlow.getUid(), 0.0)));
            userFeeMap.put(clearingBonusFlow.getUid(), fee);
        }
        // 保存个人金额
        List<ClearingBonus> clearingBonusList = Lists.newArrayList();
        userFeeMap.forEach((uid, fee) -> {
            ClearingUser clearingUser = cleaingUserMap.get(uid);
            ClearingBonus clearingBonus = new ClearingBonus(uid, clearingUser.getAccountNo(), clearingUser.getLevel(), clearingUser.getLevelName(),
                    clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(), StringUtils.N_TO_10("PY_"), fee);
            clearingBonusList.add(clearingBonus);
        });

        // 保存奖金汇总
        for (List<ClearingBonus> clearingBonuses : Lists.partition(clearingBonusList, 2000)) {
            clearingBonusService.insertBatchList(clearingBonuses);
        }
        // 保存奖金明细
        clearingBonusService.insertBatchList(clearingBonusList);
        for (List<ClearingBonusFlow> clearingBonusFlows : Lists.partition(clearingBonusFlowList, 2000)) {
            clearingBonusFlowService.insertBatchList(clearingBonusFlows);
        }
        // 更新结算信息
        clearingFinal.setTotalScore(totalScore);
        clearingFinal.setTotalAmt(totalFee);
        clearingFinal.setStatus(ClearingFinal.Constants.已结算.name());
        clearingFinalService.updateById(clearingFinal);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 级别名称
         */
        private Long capaId;


        /**
         * 付款金额
         */
        private BigDecimal payPrice;


        /**
         * 结算级别  1   2  3
         */
        private Long level;

        /**
         * 级别名称
         * 1. 创客当月复购100元
         * 2. VIP以上复购100元
         * 3. VIP以上复购300元
         */
        private String levelName;

        /**
         * 获取服务上级多少层的业绩分红
         * 创客当月复购100元 2层
         * VIP以上复购100元 5层
         * VIP以上复购300元 10层
         */
        private int upperRelationNum;

        /**
         * 获取邀请上级多少层的业绩分红
         * 创客当月复购100元 2层
         * VIP以上复购100元 5层
         * VIP以上复购300元 10层
         */
        private int upperInvitationNum;

        /**
         * 获取下级服务多少层的业绩分红
         * 创客当月复购100元 25层
         * VIP以上复购100元 25层
         * VIP以上复购300元 25层
         */
        private int underRelationNum;

        /**
         * 获取下级邀请多少层的业绩分红
         * 创客当月复购100元 25层
         * VIP以上复购100元 25层
         * VIP以上复购300元 25层
         */
        private int underInvitationNum;

        // 下拿比例
        private BigDecimal underScale = BigDecimal.valueOf(0.01);

        // 上拿同层比例
        private BigDecimal upperScale = BigDecimal.valueOf(0.05);

    }

}




