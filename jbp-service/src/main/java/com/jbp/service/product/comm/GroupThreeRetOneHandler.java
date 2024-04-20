package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
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
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.分组推三返一.getName() + "参数不完整");
        }
        for (Rule rule : rules) {
            if (rule.getNum()== null ||  ArithmeticUtils.lessEquals(rule.getRatio(), BigDecimal.ZERO) || ArithmeticUtils.lessEquals(rule.getAmt(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.分组推三返一.getName() + "参数不完整");
            }
        }
        Set<Integer> set = rules.stream().map(Rule::getNum).collect(Collectors.toSet());
        if (set.size() != rules.size()) {
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
    public List<Rule> getRule(ProductComm productComm) {
        try {
            List<Rule> rules = JSONArray.parseArray(productComm.getRule(), Rule.class);
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
        orderDetails.forEach(e->{
        });


        Integer pid = invitationService.getPid(order.getUid());
        UserCapa userCapa =  userCapaService.getByUser(pid);
        if (pid == null) {
            return;
        }
        BigDecimal totalAmt = BigDecimal.ZERO;
        // 获取订单产品
        List<FundClearingProduct> productList = Lists.newArrayList();

//
//        Rule rule= getRule().get(0);

        //计算所有推三返一商品
        List<ProductComm> productCommList = productCommService.list(new QueryWrapper<ProductComm>().lambda().eq(ProductComm::getType,getType()));
        String goodsId = "";
        productCommList.forEach(e ->{
            List<Rule> rules = JSONArray.parseArray(e.getRule(), Rule.class);
//            if(!rules.isEmpty() && rules.get(0).getAmt().compareTo() == 0){
//
//            }

        });

        // 计算单数
        List<OrderDetail> orderList= orderDetailService.getNextOrderGoods(pid,goodsId,userCapa.getCapaId());


        // 按订单保存佣金
//        totalAmt = totalAmt.setScale(2, BigDecimal.ROUND_DOWN);
//        if (ArithmeticUtils.gt(totalAmt, BigDecimal.ZERO)) {
//            User orderUser = userService.getById(order.getUid());
//            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.推三返一.getName(), totalAmt,
//                     productList, orderUser.getAccount() + "下单获得" + ProductCommEnum.推三返一.getName(), "");
//        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 单数
         */
        private Integer num;
        /**
         * 比例
         */
        private BigDecimal ratio;
        /**
         * 分组金额
         */
        private BigDecimal amt;

    }

}
