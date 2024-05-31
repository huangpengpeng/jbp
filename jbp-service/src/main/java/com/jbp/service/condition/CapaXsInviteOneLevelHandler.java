package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 一阶人数升星
 */
@Component
public class CapaXsInviteOneLevelHandler implements ConditionHandler {

    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private Environment environment;

    @Override
    public String getName() {
        return ConditionEnum.一阶人数升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getNum() == null || rule.getCapaXsId() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {

        //过滤指定用户星级
        String filter =environment.getProperty("teamAmtSelf.filterList");
        if(!StringUtils.isBlank(filter)) {
            List<String> filterList = Arrays.asList(filter);
            if (filterList.contains(uid)) {
                return false;
            }
        }

        Rule rule = getRule(riseCondition);
        List<UserInvitation> nextList = userInvitationService.getNextList2(uid);
        if(CollectionUtils.isEmpty(nextList) || nextList.size() < rule.getNum().intValue()){
            return false;
        }
        int num = 0;

        for (UserInvitation userInvitation : nextList) {
            UserCapaXs userCapaXs = userCapaXsService.getByUser(userInvitation.getUId());
            if (userCapaXs != null && userCapaXs.getCapaId().compareTo(rule.getCapaXsId()) >= 0) {
                num++;
            }
            if (num >= rule.getNum()) {
                return true;
            }
        }
        return num >= rule.getNum();
    }

    /**
     * 升级条件规则信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 一阶人数
         */
        private Integer num;

        /**
         * 星级
         */
        private Long capaXsId;

        /**
         * 星级名称
         */
        private String capaXsName;

    }
}
