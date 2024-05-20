package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MonthGuanLiCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private ClearingUserService clearingUserService;
    @Resource
    private ClearingBonusService clearingBonusService;
    @Resource
    private ClearingBonusFlowService clearingBonusFlowService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private ClearingInvitationFlowService invitationFlowService;

    @Override
    public Integer getType() {
        return ProductCommEnum.月度管理补贴.getType();
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.月度管理补贴.getName() + "参数不完整");
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
        // [{"level":1,"levelName":"主管","minScore":0,"scale":0.02},{"level":2,"levelName":"经理","minScore":100000,"scale":0.03},{"level":3,"levelName":"资深经理","minScore":300000,"scale":0.04},{"level":4,"levelName":"高级经理","minScore":600000,"scale":0.05},{"level":5,"levelName":"总监","minScore":1200000,"scale":0.06},{"level":6,"levelName":"资深总监","minScore":1900000,"scale":0.07},{"level":7,"levelName":"高级总监","minScore":2700000,"scale":0.08},{"level":8,"levelName":"总裁","minScore":3600000,"scale":0.09},{"level":9,"levelName":"资深总裁","minScore":4800000,"scale":0.1},{"level":10,"levelName":"高级总裁","minScore":500000000,"scale":0.101}]
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

        List<ClearingUser> clearingUserList = clearingUserService.getByClearing(clearingFinal.getId());
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        for (ClearingUser clearingUser : clearingUserList) {
            Rule rule = JSONObject.parseObject(clearingUser.getRule(), Rule.class);
            totalScore = totalScore.add(rule.getUsableScore());
            BigDecimal fee = rule.getUsableScore().multiply(rule.getScale()).setScale(2, BigDecimal.ROUND_DOWN);
            totalFee = totalFee.add(fee);
            if (ArithmeticUtils.gt(fee, BigDecimal.ZERO)) {
                ClearingBonus clearingBonus = new ClearingBonus(clearingUser.getUid(), clearingUser.getAccountNo(), clearingUser.getLevel(), clearingUser.getLevelName(),
                        clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(), StringUtils.N_TO_10("MGL_"), fee);
                clearingBonusService.save(clearingBonus);
                // 新增明细
                ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingUser.getUid(), clearingUser.getAccountNo(),
                        clearingUser.getLevel(), clearingUser.getLevelName(),
                        clearingFinal.getId(), clearingFinal.getName(), clearingFinal.getCommName(),
                        fee, "月度管理补贴-" + "可用积分:" + rule.getUsableScore() + "增加总金额:" + fee, clearingUser.getRule());
                clearingBonusFlowService.save(clearingBonusFlow);
            }
        }

        List<ClearingBonus> clearingBonusList = clearingBonusService.get4Clearing(clearingFinal.getId());
        Map<Integer, ClearingBonus> clearingBonusMap = FunctionUtil.keyValueMap(clearingBonusList, ClearingBonus::getUid);
        for (ClearingBonus clearingBonus : clearingBonusList) {
            List<ClearingInvitationFlow> invitationFlows = invitationFlowService.getByPUser(clearingBonus.getUid(), clearingBonus.getClearingId(), 1);
            if (CollectionUtils.isNotEmpty(invitationFlows)) {
                for (ClearingInvitationFlow invitationFlow : invitationFlows) {
                    ClearingBonus child = clearingBonusMap.get(invitationFlow.getUId());
                    if (child != null) {
                        ClearingBonusFlow clearingBonusFlow = new ClearingBonusFlow(clearingBonus.getUid(), clearingBonus.getAccountNo(),
                                child.getLevel(), child.getLevelName(),
                                clearingFinal.getId(), child.getName(), child.getCommName(),
                                BigDecimal.valueOf(-1).multiply(child.getCommAmt()), "月度管理补贴-减少一阶总金额:" + child.getCommAmt() + "一阶账户:" + child.getAccountNo(), null);
                        clearingBonusFlowService.save(clearingBonusFlow);

                        clearingBonus.setCommAmt(clearingBonus.getCommAmt().subtract(child.getCommAmt()));
                    }
                }
            }
            clearingBonusService.updateById(clearingBonus);
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


    public Rule getRuleByScore(List<Rule> collect, BigDecimal score) {
        for (Rule rule : collect) {
            if (ArithmeticUtils.gte(score, rule.getMinScore())) {
                return rule;
            }
        }
        return null;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 级别名称
         * 1. 主管
         * 2. 经理
         * 3. 资深经理
         * 4. 高级经理
         * 5. 总监
         * 6. 资深总监
         * 7. 高级总监
         * 8. 总裁
         * 9. 资深总裁
         * 10. 高级总裁
         */
        private Long level;

        /**
         * 级别名称
         * 1. 主管
         * 2. 经理
         * 3. 资深经理
         * 4. 高级经理
         * 5. 总监
         * 6. 资深总监
         * 7. 高级总监
         * 8. 总裁
         * 9. 资深总裁
         * 10. 高级总裁
         */
        private String levelName;

        /**
         * 佣金比例
         */
        private BigDecimal scale;

        /**
         * 最小业绩
         */
        private BigDecimal minScore;

        /**
         * 等级ID
         */
        private Long capaId;


        /**
         * 可用业绩
         */
        private BigDecimal usableScore;
    }


}
