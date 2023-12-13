package com.jbp.common.annotation;


import java.lang.annotation.*;

import com.jbp.common.enums.MethodType;

/**
 * 自定义注解，拦截service
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface LogControllerAnnotation {

    // 日志是否存入数据库
    boolean intoDB() default false;

    // 操作类型, 默认是查询
    MethodType methodType() default MethodType.SELECT;

    // 接口描述
    String description() default "";

}
