package com.jbp.common.encryptapi;

import java.lang.annotation.*;

/**
 * Author:Bobby
 * DateTime:2019/4/9 16:45
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})  //作用于方法和类（接口）上
@Documented
public @interface EncryptIgnore{

}