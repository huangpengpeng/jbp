package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分组推三返一
 */
@Component
public class GroupThreeRetOneHandler extends AbstractProductCommHandler {

    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserCapaService userCapaService;


    @Override
    public Integer getType() {
        return ProductCommEnum.分组推三返一.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.分组推三返一.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rules = getRule(productComm);
        if (rules == null) {
            throw new CrmebException(ProductCommEnum.分组推三返一.getName() + "参数不完整");
        }
        for (Comm rule : rules.getGroupComm()) {
            if (rule.getNum() == null || rule.getRatio() == null) {
                throw new CrmebException(ProductCommEnum.分组推三返一.getName() + "参数不完整");
            }
        }
        Set<Integer> set = rules.getGroupComm().stream().map(Comm::getNum).collect(Collectors.toSet());
        if (set.size() != rules.getGroupComm().size()) {
            throw new CrmebException("单数不能重复");
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
            GroupThreeRetOneHandler.Rule rules = JSONObject.parseObject(productComm.getRule(), GroupThreeRetOneHandler.Rule.class);
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

        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        // 获取订单产品
        List<FundClearingProduct> productList = Lists.newArrayList();

        Integer pid = invitationService.getPid(order.getUid());
        UserCapa userCapa = userCapaService.getByUser(pid);

        for (OrderDetail orderDetail : orderDetailList) {

            ProductComm productComm = productCommService.getByProduct(orderDetail.getProductId(), getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            GroupThreeRetOneHandler.Rule rule = getRule(productComm);
            Map<String, Object> map = productCommService.getMap(new QueryWrapper<ProductComm>().select(" group_concat(product_id) as product_id ").last(" where  type = " + getType() + " and  JSON_EXTRACT(rule, '$.amt') = " + rule.getAmt() + ""));

            // 计算历史单数
            List<OrderDetail> orderList = orderDetailService.getNextOrderGoods(pid, map.get("product_id").toString(), userCapa.getCapaId());


            int remainder = orderList.size() % rule.getGroupComm().size();

            if(remainder == 0){
                remainder = 3;
            }

            GroupThreeRetOneHandler.Comm commList = rule.getGroupComm().get(remainder - 1);
            BigDecimal totalAmt = rule.getAmt().multiply(commList.getRatio()).multiply(productComm.getScale());

            FundClearingProduct clearingProduct = new FundClearingProduct(orderDetail.getProductId(), orderDetail.getProductName(), rule.getAmt(),
                    orderDetail.getPayNum(), commList.getRatio(), totalAmt);
            productList.add(clearingProduct);

            User orderUser = userService.getById(order.getUid());
            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.分组推三返一.getName(), totalAmt,
                    productList, orderUser.getAccount() + "下单获得" + ProductCommEnum.分组推三返一.getName(), "");
            //一笔订单只计算一次推三返一
            break;

        }


        if (pid == null) {
            return;
        }

    }

    public static void main(String[] args) {
        int remainder =12 % 3;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        private BigDecimal amt;
        private List<GroupThreeRetOneHandler.Comm> groupComm;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comm {
        /**
         * 单数
         */
        private Integer num;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }

}