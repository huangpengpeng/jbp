package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 邀请独立线多等级升星
 */
@Component
public class CapaXsInvitationLine2Handler implements ConditionHandler {

    @Resource
    private InvitationScoreService invitationScoreService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private SelfScoreService selfScoreService;

    @Resource
    private Environment environment;

    @Override
    public String getName() {
        return ConditionEnum.邀请独立线多等级升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule getRule(RiseCondition riseCondition) {
            try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (CollectionUtils.isEmpty(rule.getCapaXsList()) || rule.getMinTeamAmt() == null || rule.getTeamAmt() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            for (CapaXsInfo capaXsInfo : rule.getCapaXsList()) {
                if (capaXsInfo.getCapaXsId() == null || StringUtils.isEmpty(capaXsInfo.getCapaXsName()) || capaXsInfo.getNum() == null) {
                    throw new RuntimeException(getName() + ":升级规则格式错误2");
                }
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
            List<String> filterList = Arrays.asList(filter.split(","));
            if (filterList.contains(uid.toString())) {
                return false;
            }
        }

        // 获取规则
        Rule rule = getRule(riseCondition);
        // 1.自己的累积业绩
        if (rule.getTeamAmt() != null && ArithmeticUtils.gt(rule.getTeamAmt(), BigDecimal.ZERO)) {
            BigDecimal teamAmt = BigDecimal.ZERO;
           String ifOpen =environment.getProperty("teamAmtSelf.ifopen");
            if(Boolean.parseBoolean(ifOpen)){
                teamAmt =  selfScoreService.getUserNext(uid,false);
            }else{
                teamAmt = invitationScoreService.getInvitationScore(uid, true);
            }

            if (ArithmeticUtils.less(teamAmt, rule.getTeamAmt())) {
                return false;
            }
        }
        // 2.一阶人数
        List<CapaXsInfo> capaXsList = rule.getCapaXsList();
        int indeCount = capaXsList.size();
        List<UserInvitation> nextList = userInvitationService.getNextList2(uid);
        if (CollectionUtils.isEmpty(nextList) || nextList.size() < indeCount) {
            return false;
        }
        // 条件排序 高等级  多人数 一次排序
        capaXsList.sort(Comparator.comparing(CapaXsInfo::getCapaXsId, Comparator.reverseOrder()).thenComparing(CapaXsInfo::getNum, Comparator.reverseOrder()));

        // 3.每条独立线满足星级人数
        List<Integer> uidList = Lists.newArrayList(); // 满足或者不满足的人加入
        for (CapaXsInfo capaXsInfo : capaXsList) {
            capaXsInfo.setIfOk(false);
            for (UserInvitation userInvitation : nextList) {
                // 小区业绩
                if (!uidList.contains(userInvitation.getUId())) {
                    if (rule.getMinTeamAmt() != null && ArithmeticUtils.gt(rule.getMinTeamAmt(), BigDecimal.ZERO)) {
                        String ifOpen =environment.getProperty("teamAmtSelf.ifopen");
                        BigDecimal nextTotal =BigDecimal.ZERO;
                        if(Boolean.parseBoolean(ifOpen)){
                            nextTotal =  selfScoreService.getUserNext(uid,false);
                        }else{
                            nextTotal = invitationScoreService.getInvitationScore(userInvitation.getUId(), true);
                        }

                        if (ArithmeticUtils.less(nextTotal, rule.getMinTeamAmt())) {
                            uidList.add(userInvitation.getUId());
                            continue;
                        }
                    }
                    String ifOpen =environment.getProperty("teamAmtSelf.ifopen");
                    List<UserCapaXs> userCapaXsList =new ArrayList<>();

                    if(Boolean.parseBoolean(ifOpen)) {
                        userCapaXsList =  userCapaXsService.getInvitationUnder(userInvitation.getUId(), capaXsInfo.getCapaXsId());
                    }else{
                        userCapaXsList =   userCapaXsService.getRelationUnder(userInvitation.getUId(), capaXsInfo.getCapaXsId());
                    }
                    UserCapaXs userCapaXs = userCapaXsService.getByUser(userInvitation.getUId());
                    if (userCapaXs != null && userCapaXs.getCapaId().compareTo(capaXsInfo.getCapaXsId()) >= 0) {
                        userCapaXsList.add(userCapaXs);
                    }
                    if (userCapaXsList.size() >= capaXsInfo.getNum().intValue()) {
                        capaXsInfo.setIfOk(true);
                        uidList.add(userInvitation.getUId());
                        break;
                    }
                }
            }
        }
        for (CapaXsInfo capaXsInfo : capaXsList) {
            if (capaXsInfo == null || !capaXsInfo.getIfOk()) {
                return false;
            }
        }
        return true;


    }


    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setMinTeamAmt(BigDecimal.valueOf(100));
        rule.setTeamAmt(BigDecimal.valueOf(200));
        List<CapaXsInfo> capaXsList = Lists.newArrayList();
        for (int i = 1; i < 3; i++) {
            CapaXsInfo capaXsInfo = new CapaXsInfo();
            capaXsInfo.setNum(i);
            capaXsInfo.setCapaXsId(Long.valueOf(i));
            capaXsInfo.setCapaXsName("星级名称");
            capaXsList.add(capaXsInfo);
        }
        rule.setCapaXsList(capaXsList);
        System.out.println(JSONObject.toJSONString(rule));



    }


    /**
     * 升级条件规则信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 星级信息
         */
        private List<CapaXsInfo> capaXsList;

        /**
         * 小区业绩
         */
        private BigDecimal minTeamAmt;

        /**
         * 累积业绩
         */
        private BigDecimal teamAmt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapaXsInfo{
        /**
         * 每条独立线条数星级人数
         */
        private Integer num;

        /**
         * 独立线条数星级
         */
        private Long capaXsId;

        /**
         * 独立线条数星级名称
         */
        private String capaXsName;

        private Boolean ifOk;
    }

}
