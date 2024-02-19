package com.jbp.service.condition;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.*;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.agent.RelationScoreService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小区业绩独立线星级人数
 */
@Component
public class XsIsolateLineHandler implements ConditionHandler {

    @Autowired
    private RelationScoreService relationScoreService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserInvitationFlowService userInvitationFlowService;
    @Autowired
    private UserCapaXsService userCapaXsService;



    @Override
    public String getName() {
        return ConditionEnum.小区业绩独立线星级人数.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition){
        getRule(riseCondition);
    }

    @Override
    public XsIsolateLineHandler.Rule getRule(RiseCondition riseCondition) {
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
        BigDecimal score0 = relationScore0 == null ? BigDecimal.ZERO : relationScore0.getUsableScore().add(relationScore0.getUsedScore());
        RelationScore relationScore1 = relationScoreService.getByUser(uid, 1);
        BigDecimal score1 = relationScore1 == null ? BigDecimal.ZERO : relationScore1.getUsableScore().add(relationScore1.getUsedScore());
        // 小区业绩
        BigDecimal minTeam = BigDecimal.valueOf(Math.min(score0.doubleValue(), score1.doubleValue()));
        // 小于指定业绩
        if (ArithmeticUtils.less(minTeam, rule.getMinTeamAmt())) {
            return false;
        }
        // 独立线条数
        List<UserInvitation> nextList = userInvitationService.getNextList(uid);
        if (CollectionUtils.isEmpty(nextList) || nextList.size() < rule.getIndeCount()) {
            return false;
        }
        int i = 0;
        // 独立线等级人数
        for (UserInvitation userInvitation : nextList) {
            if (i >= rule.getIndeCount()) {
                break;
            }
            List<UserInvitationFlow> underList = userInvitationFlowService.getUnderList(userInvitation.getUId(), rule.getIndeCapaXsId());
            if (CollectionUtils.isNotEmpty(underList)) {
                i++;
            } else {
                UserCapaXs userCapaXs = userCapaXsService.getByUser(userInvitation.getUId());
                if (userCapaXs != null && NumberUtil.compare(userCapaXs.getCapaId(), rule.getIndeCapaXsId()) >= 0) {
                    i++;
                }
            }
        }
        return true;
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
        private Long indeCapaXsNum;

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
