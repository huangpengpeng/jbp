package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理佣金
 */
@Component
public class ManagerCommHandler extends AbstractProductCommHandler {

    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;

    @Override
    public Integer getType() {
        return ProductCommEnum.管理佣金.getType();
    }

    @Override
    public Integer order() {
        return 10;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return true;
    }

    @Override
    public Rule getRule(ProductComm productComm) {
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONObject.parseObject(productCommConfig.getRatioJson(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {
        ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
        if (!productCommConfig.getIfOpen()) {
            return;
        }
        // 社群佣金
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.社群佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }

        Rule rule = getRule(null);
        for (CommCalculateResult calculateResult : collisionFeeList) {
            User user = userService.getById(calculateResult.getUid());
            Integer uid = calculateResult.getUid();


            // 往上找拿钱的人
            BigDecimal upperAmt = calculateResult.getAmt().multiply(rule.getUpperRatio()).setScale(2, BigDecimal.ROUND_DOWN);
            if(ArithmeticUtils.gt(upperAmt, BigDecimal.ZERO)){
                List<UserUpperDto> allUpper = userInvitationService.getAllUpper(uid);
                int i = 1;
                if(CollectionUtils.isNotEmpty(allUpper)) {
                    for (UserUpperDto upperDto : allUpper) {
                        if (i > rule.getUpperLevel() || upperDto.getPId() == null) {
                            break;
                        }
                        UserCapa userCapa = userCapaService.getByUser(upperDto.getPId());
                        if (userCapa.getCapaId().intValue() >= rule.getCapaId().intValue()) {
                            fundClearingService.create(upperDto.getPId(), order.getOrderNo(), ProductCommEnum.管理佣金.getName(), upperAmt,
                                     null, user.getAccount() + "获得社群佣金, 奖励下拿" + ProductCommEnum.管理佣金.getName(), "");
                            i++;
                        }
                    }
                }
            }



            BigDecimal underAmt = calculateResult.getAmt().multiply(rule.getUnderRatio()).setScale(2, BigDecimal.ROUND_DOWN);
            if(ArithmeticUtils.gt(underAmt, BigDecimal.ZERO)){
                // 往下找拿钱人
                LinkedList<List<UserInvitation>> levelList = userInvitationService.getLevelList(uid, rule.getUnderLevel());
                List<UserInvitation> allUser = Lists.newArrayList();
                levelList.forEach(s->{
                    allUser.addAll(s);
                });
                if (CollectionUtils.isNotEmpty(allUser)) {
                     Map<Integer, UserCapa> capaMap = userCapaService.getUidMap(allUser.stream().map(UserInvitation::getUId).collect(Collectors.toList()));

                    for (List<UserInvitation> list : levelList) {
                        if(CollectionUtils.isEmpty(list)){
                            continue;
                        }
                        List<UserInvitation> sendUserList = list.stream().filter(u -> capaMap.get(u.getUId()).getCapaId().intValue() >= rule.getCapaId().intValue()).collect(Collectors.toList());
                        if(sendUserList.isEmpty()){
                            continue;
                        }
                        BigDecimal userAmt = underAmt.divide(BigDecimal.valueOf(sendUserList.size()), 2, BigDecimal.ROUND_DOWN);
                        if(ArithmeticUtils.gt(userAmt, BigDecimal.ZERO)){
                            for (UserInvitation userInvitation : sendUserList) {
                                fundClearingService.create(userInvitation.getUId(), order.getOrderNo(), ProductCommEnum.管理佣金.getName(), userAmt,
                                         null, user.getAccount() + "获得社群佣金, 奖励上拿" + ProductCommEnum.管理佣金.getName(), "");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Rule rule = new Rule(2L, 7, BigDecimal.valueOf(0.03), 3, BigDecimal.valueOf(0.03));
        System.out.println(JSONObject.toJSONString(rule));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        // 等级要求
        private Long capaId;

        // 往上找几层
        private int upperLevel;

        // 往上分佣拿钱
        private BigDecimal upperRatio;

        // 往下找几层
        private int underLevel;

        // 往下分佣拿钱
        private BigDecimal underRatio;
    }

}
