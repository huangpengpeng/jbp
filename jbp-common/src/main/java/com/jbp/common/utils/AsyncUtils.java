package com.jbp.common.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AsyncUtils {
    @Async
    public void exec(Object param, Consumer consumer){
        consumer.accept(param);
    }
}
