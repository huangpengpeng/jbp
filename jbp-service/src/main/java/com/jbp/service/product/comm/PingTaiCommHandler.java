package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Component
public class PingTaiCommHandler extends AbstractProductCommHandler {

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
        return ProductCommEnum.平台分红.getType();
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
        }
        for (Rule rule : rules) {
            if (rule.getRatio() == null || rule.getLevel() == null || StringUtils.isEmpty(rule.getLevelName())) {
                throw new CrmebException(ProductCommEnum.平台分红.getName() + "参数不完整");
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
    public List<Rule> getRule(ProductComm productComm) {
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONArray.parseArray(productCommConfig.getRatioJson(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }


    @Override
    public void clearing(ClearingFinal clearingFinal) {
       // 获取结算名单


    }

    @Override
    public void del4Clearing(ClearingFinal clearingFinal) {



    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 结算级别
         */
        private Long level;

        /**
         * 结算名称
         */
        private String levelName;

        /**
         * 比例
         */
        private BigDecimal ratio;

        /**
         * 关联结算级别
         */
        private Long refLevel;

        /**
         * 最大金额
         */
        private BigDecimal maxFee;
    }
}
