package com.jbp.common.annotation;

import java.lang.annotation.*;

import org.springframework.data.domain.DomainEvents;

/**
 * @ClassName CustomResponseAnnotation
 * @Description 自定义响应结果注解，由此注解表示返回值不经过统一封装返回 此类已经被加密拦截忽略注解替换
 * @Author HZW
 * @Date 2023/2/21 17:19
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})  //作用于方法和类（接口）上
@Documented
public @interface CustomResponseAnnotation {


}
