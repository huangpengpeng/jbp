package com.jbp.service.event;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherContext implements ApplicationEventPublisher {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 发送事件
     *
     * @param event
     */
    public void publishEvent(@NonNull AbstractEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }
}
