package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.agent.InvitationScoreService;
import com.jbp.service.service.agent.SelfScoreService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 邀请独立线升星
 */
@Component
public class CapaXsInvitationLineHandler implements ConditionHandler {

    @Resource
    private InvitationScoreService invitationScoreService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private Environment environment;
    @Resource
    private SelfScoreService selfScoreService;

    @Override
    public String getName() {
        return ConditionEnum.邀请独立线升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getIndeCount() == null || rule.getIndeCapaXsId() == null
                    || rule.getMinTeamAmt() == null || rule.getTeamAmt() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        // 获取规则
        Rule rule = getRule(riseCondition);
        // 1.自己的累积业绩
        String ifOpen = environment.getProperty("teamAmtSelf.ifopen");
        BigDecimal teamAmt = BigDecimal.ZERO;
        if (Boolean.parseBoolean(ifOpen)) {
            teamAmt = selfScoreService.getUserNext(uid, true);
        } else {
            teamAmt = invitationScoreService.getInvitationScore(uid, true);
        }


        if (ArithmeticUtils.less(teamAmt, rule.getTeamAmt())) {
            return false;
        }
        // 2.一阶人数
        List<UserInvitation> nextList = userInvitationService.getNextOrMidList(uid);
        if (CollectionUtils.isEmpty(nextList) || nextList.size() < rule.getIndeCount().intValue()) {
            return false;
        }
        // 3.每条独立线满足星级人数
        int indeCount = 0;
        for (UserInvitation userInvitation : nextList) {
            // 团队业绩
            BigDecimal nextTotal = BigDecimal.ZERO;
            if (Boolean.parseBoolean(ifOpen)) {
                nextTotal = selfScoreService.getUserNext(uid, true);
            } else {
                nextTotal = invitationScoreService.getInvitationScore(userInvitation.getUId(), true);
            }
            if (ArithmeticUtils.less(nextTotal, rule.getMinTeamAmt())) {
                continue;
            }
            List<UserCapaXs> userCapaXsList = new ArrayList<>();

            if (Boolean.parseBoolean(ifOpen)) {
                userCapaXsList = userCapaXsService.getInvitationUnder(userInvitation.getUId(), rule.getIndeCapaXsId());
            } else {
                userCapaXsList = userCapaXsService.getRelationUnder(userInvitation.getUId(), rule.getIndeCapaXsId());
            }

            UserCapaXs userCapaXs = userCapaXsService.getByUser(userInvitation.getUId());
            if (userCapaXs != null && userCapaXs.getCapaId().compareTo(rule.getIndeCapaXsId()) >= 0) {
                userCapaXsList.add(userCapaXs);
            }
            if (userCapaXsList.size() >= rule.getIndeCapaXsNum().intValue()) {
                indeCount++;
            }
        }
        return indeCount >= rule.getIndeCount().intValue();
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

        /**
         * 累积业绩
         */
        private BigDecimal teamAmt;
    }
}
