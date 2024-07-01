package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WhiteUserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 星级级差
 */
@Component
public class WhiteCapaXsDifferentialCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private CapaXsService capaXsService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private WhiteUserService whiteUserService;

    @Override
    public Integer getType() {
        return ProductCommEnum.白名单星级级差佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.白名单星级级差佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(productComm);
        if (rule == null) {
            throw new CrmebException(ProductCommEnum.白名单星级级差佣金.getName() + "参数不完整");
        }
        Set<String> set = Sets.newHashSet();
        for (XsCapaRule xsCapaRule : rule.getXsCapaRuleList()) {
            if (xsCapaRule.getCapaXsId() == null || xsCapaRule.getRatio() == null || ArithmeticUtils.lessEquals(xsCapaRule.getRatio(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.白名单星级级差佣金.getName() + "参数不完整");
            }
            if (StringUtils.isNotEmpty(xsCapaRule.getType())) {
                set.add(xsCapaRule.getType());
            }
        }
        if (!set.isEmpty() && set.size() > 1) {
            throw new CrmebException(ProductCommEnum.白名单星级级差佣金.getName() + "类型不能同时存在金额和比例");
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
    public Rule getRule(ProductComm productComm) {
        try {
            WhiteCapaXsDifferentialCommHandler.Rule rules = JSONObject.parseObject(productComm.getRule(), WhiteCapaXsDifferentialCommHandler.Rule.class);
            return rules;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {
        // 查询所有上级
        List<UserUpperDto> allUpper = invitationService.getNoMountAllUpper(order.getUid());
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        Map<Integer, UserCapaXs> uidCapaXsMap = userCapaXsService.getUidMap(allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        // 最大星级
        CapaXs maxCapa = capaXsService.getMaxCapa();
        // 更高头衔的用户【拿钱用户】
        LinkedList<UserCapaXs> userList = Lists.newLinkedList();
        Long capaId = 0L;
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() != null) {
                UserCapaXs userCapaXs = uidCapaXsMap.get(upperDto.getPId());
                if (userCapaXs != null) {
                    if (NumberUtils.compare(userCapaXs.getCapaId(), capaId) > 0) {
                        userList.add(userCapaXs);
                        capaId = userCapaXs.getCapaId();
                    }
                    if (NumberUtils.compare(maxCapa.getId(), capaId) == 0) {
                        break;
                    }
                }
            }
        }
        // 没人退出
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // 分钱用户产品
        LinkedHashMap<Integer, List<FundClearingProduct>> productMap = Maps.newLinkedHashMap();
        // 分钱用户金额
        LinkedHashMap<Integer, Double> userAmtMap = Maps.newLinkedHashMap();


        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            // 佣金配置
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 真实业绩
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = totalPv.multiply(productComm.getScale());
            // 佣金规则
            Rule rule = getRule(productComm);
            List<XsCapaRule> xsCapaRuleList = rule.getXsCapaRuleList();
            String type = xsCapaRuleList.get(0).getType();
            if (StringUtils.isNotEmpty(type) && "金额".equals(type)) {
                type = "金额";
            } else {
                type = "比例";
            }

            Map<Long, XsCapaRule> ruleMap = FunctionUtil.keyValueMap(xsCapaRuleList, XsCapaRule::getCapaXsId);
            // 已发比例
            BigDecimal usedRatio = BigDecimal.ZERO;
            // 每个人拿
            for (UserCapaXs userCapa : userList) {

                WhiteUser whiteUser = whiteUserService.getByUser(userCapa.getUid(), rule.getWhiteId());
                if (whiteUser == null) {
                    continue;
                }

                XsCapaRule xsCapaRule = ruleMap.get(userCapa.getCapaId());
                BigDecimal ratio = BigDecimal.ZERO;
                if (xsCapaRule != null) {
                    ratio = xsCapaRule.getRatio();
                }
                if ("金额".equals(type)) {
                    ratio = ratio.multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                }
                // 佣金
                if (ArithmeticUtils.gt(ratio, usedRatio)) {
                    double amt = 0.0;
                    BigDecimal usableRatio = ratio.subtract(usedRatio); // 可发比例 、金额
                    if ("金额".equals(type)) {
                        amt = usableRatio.doubleValue();
                    } else {
                        amt = totalPv.multiply(usableRatio).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
                    }
                    usedRatio = ratio;
                    userAmtMap.put(userCapa.getUid(), MapUtils.getDoubleValue(userAmtMap, userCapa.getUid(), 0d) + amt);
                    FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                            orderDetail.getPayNum(), ratio, BigDecimal.valueOf(amt));

                    List<FundClearingProduct> productList = productMap.get(userCapa.getUid());
                    if (CollectionUtils.isEmpty(productList)) {
                        productList = Lists.newArrayList();
                    }
                    productList.add(clearingProduct);
                    productMap.put(userCapa.getUid(), productList);
                }
            }
        }

        if (userAmtMap.isEmpty()) {
            return;
        }
        // 分钱
        User orderUser = userService.getById(order.getUid());
        userAmtMap.forEach((uid, amt) -> {
            BigDecimal clearingFee = BigDecimal.valueOf(amt).setScale(2, BigDecimal.ROUND_DOWN);
            if (ArithmeticUtils.gt(clearingFee, BigDecimal.ZERO)) {
                List<FundClearingProduct> fundClearingProducts = productMap.get(uid);
                fundClearingService.create(uid, order.getOrderNo(), ProductCommEnum.星级级差佣金.getName(), clearingFee,
                        fundClearingProducts, orderUser.getNickname() + "|" + orderUser.getAccount() + "下单获得" + ProductCommEnum.星级级差佣金.getName(), "");

                int sort = resultList.size() + 1;
                CommCalculateResult calculateResult = new CommCalculateResult(uid, getType(), ProductCommEnum.星级级差佣金.getName(),
                        null, null, null,
                        1, null, BigDecimal.ONE, null, clearingFee, sort);
                resultList.add(calculateResult);
            }
        });
    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class XsCapaRule {
        /**
         * 等级
         */
        private Long capaXsId;

        /**
         * 比例
         */
        private BigDecimal ratio;

        private String type;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        private List<XsCapaRule> xsCapaRuleList;
        /**
         * 白名单id
         */
        private Long whiteId;
    }


}
