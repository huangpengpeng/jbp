package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 星级见点佣金2
 */
@Component
public class CapaXsPointComm2Handler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
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

    @Override
    public Integer getType() {
        return ProductCommEnum.星级见点佣金2.getType();
    }

    @Override
    public Integer order() {
        return 2;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.星级见点佣金2.getName() + "参数不完整0");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(productComm);
        if (rule.getCapaXsId() == null || CollectionUtils.isEmpty(rule.getXsComm())) {
            throw new CrmebException(ProductCommEnum.星级见点佣金2.getName() + "参数不完整1");
        }
        List<Comm> xsComm = rule.getXsComm();
        for (Comm comm : xsComm) {
            if (StringUtils.isEmpty(comm.getType()) && comm.getValue() == null || ArithmeticUtils.less(comm.getValue(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.星级见点佣金2.getName() + "参数不完整2");
            }
        }
        Set<Integer> collect = xsComm.stream().map(Comm::getNum).collect(Collectors.toSet());
        if (collect.size() != xsComm.size()) {
            throw new CrmebException(ProductCommEnum.星级见点佣金2.getName() + " 等级序号相同");
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
            Rule rules = JSONObject.parseObject(productComm.getRule(), Rule.class);
            return rules;
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
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());

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
            int i = 0;
            Integer pid = invitationService.getPid(order.getUid());

             List<Comm> xsCommList = rule.getXsComm();
            do {
                if (pid == null) {
                    break;
                }
                UserCapaXs userCapaXs = userCapaXsService.getByUser(pid);

                //判断星级是否满足佣金规则
                if (userCapaXs == null || userCapaXs.getCapaId().intValue() < rule.getCapaXsId().intValue()) {
                    pid = invitationService.getPid(pid);
                    continue;
                }
                Comm comm = xsCommList.get(i);

                BigDecimal amount = BigDecimal.ZERO;
                if(comm.getType().equals("比例")){
                    amount = totalPv.multiply(comm.getValue()).multiply(new BigDecimal(orderDetail.getPayNum()));
                }else{
                    amount = comm.getValue();
                }

                amount.setScale(2, BigDecimal.ROUND_DOWN);
                if(ArithmeticUtils.gt(amount, BigDecimal.ZERO)) {
                    List<FundClearingProduct> fundClearingProducts = new ArrayList<>();
                    FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                            orderDetail.getPayNum(), comm.getValue(), amount);
                    fundClearingProducts.add(clearingProduct);
                    User orderUser = userService.getById(order.getUid());
                    fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.星级见点佣金2.getName(), amount,
                            fundClearingProducts, orderUser.getAccount() + "下单获得" + comm.getType() + ProductCommEnum.星级见点佣金2.getName(), "");
                    pid = invitationService.getPid(pid);
                    i++;
                }
            } while (i < rule.getXsComm().size());
        }

    }

    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setCapaXsId(1L);
        List<Comm> xsComm = Lists.newArrayList();
        Comm comm = new Comm();
        comm.setNum(1);
        comm.setType("金额");
        comm.setValue(BigDecimal.valueOf(5));
        xsComm.add(comm);

        Comm comm2 = new Comm();
        comm2.setNum(2);
        comm2.setType("比例");
        comm2.setValue(BigDecimal.valueOf(0.05));
        xsComm.add(comm2);

        rule.setXsComm(xsComm);

        System.out.println(JSONObject.toJSONString(rule));

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        private Long capaXsId;
        private List<Comm> xsComm;
    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comm {
        /**
         * 代数
         */
        private int num;

        /**
         * 比例 类型  金额  比例
         */
        private String type;

        /**
         * 数值【小数位 不要限制】
         */
        private BigDecimal value;
    }
}