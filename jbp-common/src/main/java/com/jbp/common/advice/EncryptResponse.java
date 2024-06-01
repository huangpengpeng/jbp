package com.jbp.common.advice;

import java.lang.annotation.*;

/**
 * 对返回数据加密
 */
@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {

    boolean value() default true;
}
