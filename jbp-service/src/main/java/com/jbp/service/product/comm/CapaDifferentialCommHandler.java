package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 等级级差
 */
@Component
public class CapaDifferentialCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private CapaService capaService;
    @Resource
    private UserService userService;
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
        return ProductCommEnum.级差佣金.getType();
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.级差佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.级差佣金.getName() + "参数不完整");
        }
        Set<String> set = Sets.newHashSet();
        for (Rule rule : rules) {
            if (rule.getCapaId() == null || rule.getRatio() == null || ArithmeticUtils.lessEquals(rule.getRatio(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.级差佣金.getName() + "参数不完整");
            }
            if (StringUtils.isNotEmpty(rule.getType())) {
                set.add(rule.getType());
            }
        }
        if(!rules.isEmpty() && rules.size() > 1) {
            throw new CrmebException(ProductCommEnum.级差佣金.getName() + "类型不能同时存在金额和比例");
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
    public List<Rule> getRule(ProductComm productComm) {
        try {
            return JSONArray.parseArray(productComm.getRule(), Rule.class);
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
        Map<Integer, UserCapa> uidCapaMap = userCapaService.getUidMap(allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        // 最大等级
        Capa maxCapa = capaService.getMaxCapa();
        // 更高头衔的用户【拿钱用户】
        LinkedList<UserCapa> userList = Lists.newLinkedList();
        Long capaId = 0L;
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() != null) {
                UserCapa userCapa = uidCapaMap.get(upperDto.getPId());
                if(userCapa == null){
                    continue;
                }
                if (NumberUtils.compare(userCapa.getCapaId(), capaId) > 0) {
                    userList.add(userCapa);
                    capaId = userCapa.getCapaId();
                }
                if (NumberUtils.compare(maxCapa.getId(), capaId) == 0) {
                    break;
                }
            }
        }
        // 没人退出
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        Map<Integer, List<FundClearingProduct>> productMap = Maps.newConcurrentMap();
        Map<Integer, Double> userAmtMap = Maps.newConcurrentMap();

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
            List<Rule> rules = getRule(productComm);
            Map<Long, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getCapaId);
            String type = rules.get(0).getType();
            if(StringUtils.isNotEmpty(type) && "金额".equals(type)){
                type= "金额";
            }else{
                type= "比例";
            }

            // 已发比例【或者金额】
            BigDecimal usedRatio = BigDecimal.ZERO;
            // 每个人拿钱
            for (UserCapa userCapa : userList) {
                Rule rule = ruleMap.get(userCapa.getCapaId());
                BigDecimal ratio = BigDecimal.ZERO; // 当前头衔获得比例或者金额
                if (rule != null) {
                    ratio = rule.getRatio();
                }
                if("金额".equals(type)){
                    ratio = ratio.multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                }
                // 佣金
                if (ArithmeticUtils.gt(ratio, usedRatio)) {
                    double amt = 0.0;
                    BigDecimal usableRatio = ratio.subtract(usedRatio); // 可发比例 、金额
                    if("金额".equals(type)){
                        amt = usableRatio.doubleValue();
                    }else{
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
                fundClearingService.create(uid, order.getOrderNo(), ProductCommEnum.级差佣金.getName(), clearingFee, fundClearingProducts, orderUser.getAccount() + "下单获得" + ProductCommEnum.级差佣金.getName(), "");
            }
        });
    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 等级
         */
        private Long capaId;

        /**
         * 比例
         */
        private BigDecimal ratio;

        /**
         * 比例 金额
         */
        private String type;
    }

}
