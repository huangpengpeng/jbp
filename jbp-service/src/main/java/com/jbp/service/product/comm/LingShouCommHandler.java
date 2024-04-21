package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Component
 */
@Component
public class LingShouCommHandler extends AbstractProductCommHandler {

    @Resource
    private UserCapaService userCapaService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;
    @Override
    public Integer getType() {
        return ProductCommEnum.零售佣金.getType();
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.零售佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(productComm);
        if (rule == null || ObjectUtils.anyNull(rule.getTwoRatio(), rule.getOneRatio(),
                rule.getOneCapaId(), rule.getTwoCapaId())) {
            throw new CrmebException(ProductCommEnum.零售佣金.getName() + "参数不完整");
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
            return JSONObject.parseObject(productComm.getRule(), Rule.class);
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
        // 增加对碰积分业绩
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        List<FundClearing> list = Lists.newArrayList();
        // 订单总PV
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 总PV
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = BigDecimal.valueOf(totalPv.multiply(productComm.getScale()).intValue());
            if (ArithmeticUtils.lessEquals(totalPv, BigDecimal.ZERO)) {
                continue;
            }
            Rule rule = getRule(productComm);
            // 最近的满足等级的用户获得直推
            Integer uid = order.getUid();
            Integer oneId = null;
            do {
                Integer pid = invitationService.getPid(uid);
                if (pid == null) {
                    break;
                }
                UserCapa userCapa = userCapaService.getByUser(pid);
                if (userCapa != null && NumberUtils.compare(userCapa.getCapaId(), rule.getOneCapaId()) >= 0) {
                    oneId = pid;
                    break;
                }
                uid = pid;
            } while (true);
            // 没有上级就不用继续分钱了
            if (oneId == null) {
                continue;
            }
            BigDecimal oneFee = totalPv.multiply(rule.getOneRatio());
            FundClearing oneFun = new FundClearing();
            oneFun.setUid(oneId);
            oneFun.setCommName("直推");
            oneFun.setCommAmt(oneFee);
            list.add(oneFun);

            // 分给直推的培育人
            UserInvitation oneUserInvitation = invitationService.getByUser(oneId);
            if (oneUserInvitation.getMId() != null) {
                UserCapa userCapa = userCapaService.getByUser(oneUserInvitation.getMId());
                if (userCapa != null && NumberUtils.compare(userCapa.getCapaId(), rule.getTwoCapaId()) >= 0) {
                    BigDecimal threeFee = totalPv.multiply(rule.getTwoRatio());
                    FundClearing threeFund = new FundClearing();
                    threeFund.setUid(oneUserInvitation.getMId());
                    threeFund.setCommName("培育");
                    threeFund.setCommAmt(threeFee);
                    list.add(threeFund);
                }
            }

        }

        // 相同用户相同佣金类型合并发放
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        User orderUser = userService.getById(order.getUid());
        Map<Integer, List<FundClearing>> fundClearingMap = FunctionUtil.valueMap(list, FundClearing::getUid);
        fundClearingMap.forEach((uid, fundClearingList) -> {
            String description = "";
            Map<BigDecimal, List<FundClearing>> map = FunctionUtil.valueMap(fundClearingList, FundClearing::getCommAmt);
            BigDecimal totalAmt = BigDecimal.ZERO;
            List<FundClearing> fundClearings1 = map.get("直推");
            if (CollectionUtils.isNotEmpty(fundClearings1)) {
                BigDecimal commAmt = fundClearings1.stream().map(FundClearing::getCommAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalAmt = totalAmt.add(commAmt);
                description = description + "店长获奖:" + totalAmt;
            }
            List<FundClearing> fundClearings3 = map.get("培育");
            if (CollectionUtils.isNotEmpty(fundClearings3)) {
                BigDecimal commAmt = fundClearings3.stream().map(FundClearing::getCommAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalAmt = totalAmt.add(commAmt);
                description = description + "培育获奖:" + totalAmt;
            }
            fundClearingService.create(uid, order.getOrderNo(), ProductCommEnum.零售佣金.getName(),
                    totalAmt, null, orderUser.getAccount() + "下单,获得" + ProductCommEnum.零售佣金.getName() + "【" + description + "】", "");
        });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 比例
         */
        private BigDecimal oneRatio;

        /**
         * 等级
         */
        private Long oneCapaId;

        /**
         * 间推比例
         */
        private BigDecimal twoRatio;

        /**
         * 间推最小比例
         */
        private Long twoCapaId;
    }
}
