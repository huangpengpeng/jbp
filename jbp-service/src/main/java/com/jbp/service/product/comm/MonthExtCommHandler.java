package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.utils.*;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 月结扩展佣金
 */
@Slf4j
@Component
public class MonthExtCommHandler extends AbstractProductCommHandler {

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


    @Override
    public Integer getType() {
        return ProductCommEnum.拓展佣金.getType();
    }


    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.拓展佣金.getName() + "参数不完整");
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
    public void clearing(ClearingFinal clearingFinal) {
        Long clearingId = clearingFinal.getId();
        List<ClearingUser> clearingUsers = clearingUserService.getByClearing(clearingId);
        Date startTime = DateTimeUtils.parseDate(clearingFinal.getStartTime());
        Date endTime = DateTimeUtils.parseDate(clearingFinal.getEndTime());
        List<Order> successList = orderService.getSuccessList(startTime, endTime);
        // 计算积分汇总
        BigDecimal totalScore = BigDecimal.ZERO;
        Map<Integer, ProductComm> map = Maps.newConcurrentMap();
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
                totalScore = totalScore.add(realScore);
            }
        }

        if (ArithmeticUtils.lessEquals(totalScore, BigDecimal.ZERO)) {
            log.error(clearingFinal.getName() + "结算积分为0");
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
            return;
        }

        // 权重汇总
        BigDecimal sum = clearingUsers.stream().map(p -> JSONObject.parseObject(p.getRule()).getBigDecimal("weight")).reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));
        if (ArithmeticUtils.lessEquals(sum, BigDecimal.ZERO)) {
            log.error(clearingFinal.getName() + "权重为0");
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
        }
        // 一份多少钱
        BigDecimal divide = totalScore.divide(sum, 2, BigDecimal.ROUND_UP);
        // 明细
        List<ClearingBonusFlow> clearingBonusFlowList = Lists.newArrayList();
        // 个人佣金汇总
        Map<Integer, BigDecimal> userFeeMap = Maps.newConcurrentMap();
        // 总佣金
        BigDecimal totalFee = BigDecimal.ZERO;
        // 计算每个人实际金额
        for (ClearingUser p : clearingUsers) {
            BigDecimal weight = JSONObject.parseObject(p.getRule()).getBigDecimal("weight");
            if (ArithmeticUtils.lessEquals(weight, BigDecimal.ZERO)) {
                continue;
            }
            BigDecimal commFee = divide.multiply(weight).setScale(2, BigDecimal.ROUND_UP);
            totalFee = totalFee.add(commFee);
            // 新增明细
            JSONObject json = new JSONObject();
            json.put("总权重", sum);
            json.put("权重", weight);
            json.put("比例", divide);
            ClearingBonusFlow flow = new ClearingBonusFlow(p.getUid(), p.getAccountNo(), p.getLevel(), p.getLevelName(),
                    clearingId, clearingFinal.getName(), clearingFinal.getCommName(),
                    commFee, "", json.toJSONString());
            clearingBonusFlowList.add(flow);
            // 个人金额汇总
            BigDecimal fee = commFee.add(BigDecimal.valueOf(MapUtils.getDouble(userFeeMap, p.getUid(), 0.0)));
            userFeeMap.put(p.getUid(), fee);
        }
        // 保存明细
        List<List<ClearingBonusFlow>> partition = Lists.partition(clearingBonusFlowList, 2000);
        for (List<ClearingBonusFlow> clearingBonusFlows : partition) {
            clearingBonusFlowService.insertBatchList(clearingBonusFlows);
        }
        // 保存汇总
        List<ClearingBonus> clearingBonusList = Lists.newArrayList();
        Map<Integer, ClearingUser> clearingUserMap = FunctionUtil.keyValueMap(clearingUsers, ClearingUser::getUid);
        userFeeMap.forEach((k, v) -> {
            ClearingUser clearingUser = clearingUserMap.get(k);
            ClearingBonus clearingBonus = new ClearingBonus(k, clearingUser.getAccountNo(), clearingUser.getLevel(), clearingUser.getLevelName(),
                    clearingId, clearingFinal.getName(), clearingFinal.getCommName(), StringUtils.N_TO_10("KZ_"), v);
            clearingBonusList.add(clearingBonus);
        });
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
}


