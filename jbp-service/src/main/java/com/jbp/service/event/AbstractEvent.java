package com.jbp.service.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 抽象事件类
 *
 */
@Getter
@Setter
public class AbstractEvent extends ApplicationEvent {

    protected String msgType;

    protected AbstractEvent(Object source) {
        super(source);
    }

    public static enum EventMsgTypeEnum {
        等级变更
    }

}
