package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserRelation;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.agent.RelationScoreService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.service.agent.UserRelationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang.BooleanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小区业绩独立线星级人数
 */
@Component
public class CapaXsIsolateLineHandler implements ConditionHandler {

    @Autowired
    private RelationScoreService relationScoreService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private UserRelationService userRelationService;


    @Override
    public String getName() {
        return ConditionEnum.业绩独立线升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public CapaXsIsolateLineHandler.Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getIndeCount() == null || rule.getIndeCapaXsId() == null
                    || rule.getMinTeamAmt() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        // 当前用户是否满足改升级条件  满足返回 true  不满足返回false
        Rule rule = getRule(riseCondition);
        // 小区业绩
        RelationScore relationScore0 = relationScoreService.getByUser(uid, 0);
        BigDecimal score0 = relationScore0 == null ? BigDecimal.ZERO : relationScore0.getUsableScore().add(relationScore0.getUsedScore()).subtract(relationScore0.getFakeScore());
        RelationScore relationScore1 = relationScoreService.getByUser(uid, 1);
        BigDecimal score1 = relationScore1 == null ? BigDecimal.ZERO : relationScore1.getUsableScore().add(relationScore1.getUsedScore()).subtract(relationScore0.getFakeScore());
        // 小区业绩
        BigDecimal minTeam = BigDecimal.valueOf(Math.min(score0.doubleValue(), score1.doubleValue()));
        if (ArithmeticUtils.less(minTeam, BigDecimal.ZERO)) {
            minTeam = BigDecimal.ZERO;
        }
        // 小于指定业绩
        if (ArithmeticUtils.less(minTeam, rule.getMinTeamAmt())) {
            return false;
        }
        // 7.独立线条数
        List<UserRelation> userRelationList = userRelationService.getByPid(uid);
        if (CollectionUtils.isEmpty(userRelationList) || userRelationList.size() < rule.getIndeCount()) {
            return false;
        }
        // 8.满足要求的独立线条数
        int indeCount = 0;
        for (UserRelation userRelation : userRelationList) {
            List<UserCapaXs> userCapaXsList = userCapaXsService.getRelationUnder(userRelation.getUId(), rule.getIndeCapaXsId());
            UserCapaXs userCapaXs = userCapaXsService.getByUser(userRelation.getUId());
            if (userCapaXs != null && userCapaXs.getCapaId().compareTo(rule.getIndeCapaXsId()) >= 0) {
                userCapaXsList.add(userCapaXs);
            }
            if (hasChild(userRelation.getUId(), userCapaXsList, rule.getIndeCapaXsNum())) {
                indeCount++;
            }
        }
        if (indeCount >= rule.getIndeCount()) {
            return true;
        }
        return false;
    }

    private Boolean hasChild(Integer uid, List<UserCapaXs> userCapaXsList, int indeCapaXsNum) {
        int num = 0;
        for (UserCapaXs userCapaXs : userCapaXsList) {
            if (BooleanUtils.isNotTrue(userCapaXs.getIfFake()) && userInvitationService.hasChild(userCapaXs.getUid(), uid)) {
                num = num + 1;
                if (num >= indeCapaXsNum) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 升级条件规则信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 独立线条数
         */
        private Integer indeCount;

        /**
         * 每条独立线条数星级人数
         */
        private Integer indeCapaXsNum;

        /**
         * 独立线条数星级
         */
        private Long indeCapaXsId;

        /**
         * 独立线条数星级名称
         */
        private String indeCapaXsName;

        /**
         * 小区业绩
         */
        private BigDecimal minTeamAmt;
    }
}
