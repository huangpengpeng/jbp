package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 渠道佣金
 */
@Component
public class CollisionCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private RelationScoreService relationScoreService;
    @Resource
    private UserRelationService userRelationService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;

    @Override
    public Integer getType() {
        return ProductCommEnum.渠道佣金.getType();
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.渠道佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(null);
        if (rule == null) {
            throw new CrmebException(ProductCommEnum.渠道佣金.getName() + "参数不完整");
        }
        if (CollectionUtils.isEmpty(rule.getCapaRatioList()) || CollectionUtils.isEmpty(rule.getLevelRatioList())) {
            throw new CrmebException(ProductCommEnum.渠道佣金.getName() + "参数不完整");
        }
        for (CapaRatio capaRatio : rule.getCapaRatioList()) {
            if (capaRatio.getCapaId() == null || capaRatio.getRatio() == null || ArithmeticUtils.lessEquals(capaRatio.getRatio(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.渠道佣金.getName() + "参数不完整");
            }
        }
        for (LevelRatio levelRatio : rule.getLevelRatioList()) {
            if (levelRatio.getLevel() == null || levelRatio.getRatio() == null || ArithmeticUtils.lessEquals(levelRatio.getRatio(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.渠道佣金.getName() + "参数不完整");
            }
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
        // 对碰的奖励比例是系统级别的配置
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
        if(!productCommConfig.getIfOpen()){
            return;
        }
        // 增加对碰积分业绩
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        // 订单总PV
        BigDecimal score = BigDecimal.ZERO;
        List<ProductInfoDto> productInfoList = Lists.newArrayList();
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 业绩金额为0忽略
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            // 折算系数
            totalPv = BigDecimal.valueOf(totalPv.multiply(productComm.getScale()).intValue());
            // 明细商品
            BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee());
            ProductInfoDto productInfo = new ProductInfoDto(productId, orderDetail.getProductName(), orderDetail.getPayNum(), payPrice, totalPv);
            productInfoList.add(productInfo);
            // 订单总PV
            score = score.add(totalPv);
        }
        // 没有积分退出
        if (!ArithmeticUtils.gt(score, BigDecimal.ZERO)) {
            return;
        }
        // 服务积分明细
        LinkedList<RelationScoreFlow> relationScoreFlowList = new LinkedList<>();
        // 查询所有的上级增加积分
        List<UserUpperDto> allUpper = userRelationService.getAllUpper(order.getUid());
        for (UserUpperDto upperDto : allUpper) {
            if (upperDto.getPId() == null) {
                break;
            }
            RelationScoreFlow flow = relationScoreService.orderSuccessIncrease(upperDto.getPId(), order.getUid(), score,
                    upperDto.getNode(), order.getOrderNo(), order.getPayTime(), productInfoList, upperDto.getLevel());
            relationScoreFlowList.add(flow);
        }

        // 等级比例
        Rule rule = getRule(null);
        Map<Long, CapaRatio> capaRatioMap = FunctionUtil.keyValueMap(rule.getCapaRatioList(), CapaRatio::getCapaId);
        Map<Integer, LevelRatio> levelRatioMap = FunctionUtil.keyValueMap(rule.getLevelRatioList(), LevelRatio::getLevel);

        // 根据增加明细有序进行对碰减少
        int level = 1;
        for (RelationScoreFlow flow : relationScoreFlowList) {
            Integer uid = flow.getUid();
            int node = flow.getNode() == 0 ? 1 : 0; // 反方向
            // 正
            BigDecimal frontScore = flow.getScore();
            // 反
            RelationScore backRelationScore = relationScoreService.getByUser(uid, node);
            BigDecimal backScore = backRelationScore == null ? BigDecimal.ZERO : backRelationScore.getUsableScore();
            // 最小积分
            BigDecimal minScore = BigDecimal.valueOf(Math.min(frontScore.doubleValue(), backScore.doubleValue()));
            if (ArithmeticUtils.lessEquals(minScore, BigDecimal.ZERO)) {
                continue;
            }
            // 等级比例
            BigDecimal ratio = BigDecimal.ZERO;
            UserCapa userCapa = userCapaService.getByUser(uid);
            if (userCapa != null) {
                CapaRatio capaRatio = capaRatioMap.get(userCapa.getCapaId());
                ratio = capaRatio == null ? BigDecimal.ZERO : capaRatio.getRatio();
            }
            // 层级比例
            BigDecimal lRatio = BigDecimal.ZERO;
            LevelRatio levelRatio = levelRatioMap.get(level);
            if (levelRatio != null) {
                lRatio = levelRatio.getRatio();
            }
            // 奖金比例
            ratio = ratio.multiply(lRatio);
            // 奖励金额[保留2为小数]
            BigDecimal amt = ratio.multiply(minScore).setScale(2, BigDecimal.ROUND_DOWN);
            // 减少反方向
            relationScoreService.orderSuccessReduce(uid, flow.getOrderUid(), minScore, node, flow.getOrdersSn(),
                    flow.getPayTime(), level, amt, ratio);
            // 减少正方向
            relationScoreService.orderSuccessReduce(uid, flow.getOrderUid(), minScore, flow.getNode(), flow.getOrdersSn(),
                    flow.getPayTime(), level, amt, ratio);
            // 增加奖励
            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                User orderUser = userService.getById(flow.getOrderUid());
                fundClearingService.create(uid, flow.getOrdersSn(), ProductCommEnum.渠道佣金.getName(), amt,
                        null, null, orderUser.getAccount() + "下单获得" + ProductCommEnum.渠道佣金.getName(), "");
                // 将奖金透传出去
                int sort =  resultList.size() + 1;
                CommCalculateResult calculateResult = new CommCalculateResult(uid, getType(), ProductCommEnum.渠道佣金.getName(),
                        null, null, frontScore,
                        1, minScore, BigDecimal.ONE, ratio, amt, sort);
                resultList.add(calculateResult);
                level++;
            }
        }
    }


    public static void main(String[] args) {
        Rule rule = new Rule();
        List<CapaRatio> capaRatioList = Lists.newArrayList();
        for (int i = 1; i <= 5 ; i++) {
            CapaRatio capaRatio = new CapaRatio(Long.valueOf(i), BigDecimal.valueOf(0.1));
            if(i==5){
                capaRatio.setRatio(BigDecimal.valueOf(0.12));
            }
            capaRatioList.add(capaRatio);
        }
        List<LevelRatio> levelRatioList = Lists.newArrayList();
        for (int i = 1; i <= 8 ; i++) {
            LevelRatio levelRatio = new LevelRatio(i, BigDecimal.valueOf(1));
            if(i==6 || i==7){
                levelRatio.setRatio(BigDecimal.valueOf(0.5));
            }
            if(i==8){
                levelRatio.setRatio(BigDecimal.valueOf(0.25));
            }
            levelRatioList.add(levelRatio);
        }
        rule.setCapaRatioList(capaRatioList);
        rule.setLevelRatioList(levelRatioList);
        System.out.println(JSONObject.toJSONString(rule));

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        private List<CapaRatio> capaRatioList;
        private List<LevelRatio> levelRatioList;
    }

    /**
     * 渠道佣金规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapaRatio {
        /**
         * 等级
         */
        private Long capaId;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelRatio {
        /**
         * 层级
         */
        private Integer level;

        /**
         * 等级
         */
        private BigDecimal ratio;


    }

}
