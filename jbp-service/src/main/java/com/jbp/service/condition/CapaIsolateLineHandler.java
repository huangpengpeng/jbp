package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import com.jbp.service.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 一阶人数升星
 */
@Component
public class CapaIsolateLineHandler implements ConditionHandler {

    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserInvitationService userInvitationService;


    @Override
    public String getName() {
        return ConditionEnum.邀请独立线升级.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getIndeCapaId() == null || rule.getIndeCapaNum() == null || rule.getIndeCapaId() == null || StringUtils.isEmpty(rule.getIndeCapaName())) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        Rule rule = getRule(riseCondition);
        List<UserInvitation> nextList = userInvitationService.getNextList(uid);
        if (CollectionUtils.isEmpty(nextList) || nextList.size() < rule.getIndeCount().intValue()) {
            return false;
        }
        int indeCount = 0;
        for (UserInvitation userInvitation : nextList) {
            List<UserCapa> userCapaList = userCapaService.getInvitationUnder(userInvitation.getUId(), rule.getIndeCapaId());
            UserCapa userCapa = userCapaService.getByUser(userInvitation.getUId());
            if (userCapa != null && userCapa.getCapaId().compareTo(rule.getIndeCapaId()) >= 0) {
                userCapaList.add(userCapa);
            }
            if (userCapaList.size() >= rule.getIndeCapaNum().intValue()) {
                indeCount++;
            }
        }
        return indeCount >= rule.getIndeCount();
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
        private Integer indeCapaNum;

        /**
         * 独立线条数星级
         */
        private Long indeCapaId;

        /**
         * 独立线条数星级名称
         */
        private String indeCapaName;
    }
}
