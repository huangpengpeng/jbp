package com.jbp.common.condition;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.CapaRiseCondition;
import com.mall.order.business.handler.AbstractBusinessSystemHandler;
import com.mall.order.business.handler.BusinessSystemHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 商品佣金处理链
 */
@Component
@Slf4j
public class ConditionChain implements ApplicationContextAware {


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, ConditionHandler> beans =
                applicationContext.getBeansOfType(ConditionHandler.class);
        log.warn("conditionChain:{}", JSONObject.toJSONString(beans));
        beans.values().stream().forEach(s -> addHandler(s));
    }

    private Table<Integer, String, ConditionHandler> handlers = HashBasedTable.create();

    public void addHandler(ConditionHandler handler) {handlers.put(handler.getType(), handler.getName(), handler);
    }


    /**
     * 保存
     */
    public void save(CapaRiseCondition riseCondition) {
        ConditionHandler handler = handlers.get(riseCondition.getType(), riseCondition.getName());
        if(handler == null){
            throw new CrmebException("当前升级条件不存在");
        }
        handler.save(riseCondition);
    }






}
