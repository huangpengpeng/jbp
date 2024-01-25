package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.ProductComm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 商品佣金处理链
 */
@Component
@Slf4j
public class ProductCommChain implements ApplicationContextAware {


    private LinkedList<AbstractProductCommHandler> handlers = new LinkedList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, AbstractProductCommHandler> beans =
                applicationContext.getBeansOfType(AbstractProductCommHandler.class);
        log.warn("ProductCommChain:{}", JSONObject.toJSONString(beans));
        beans.values().stream().sorted(Comparator.comparing(s -> s.order())).forEach(s -> handlers.add(s));
    }

    /**
     * 保存
     */
    public void saveOrUpdate(ProductComm productComm) {
        for (AbstractProductCommHandler handler : handlers) {
            if (handler.getType() == productComm.getType()) {
                handler.saveOrUpdate(productComm);
            }
        }

    }


}
