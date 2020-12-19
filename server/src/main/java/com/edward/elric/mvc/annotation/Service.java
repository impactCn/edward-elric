package com.edward.elric.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author: impactCn
 * @createTime: 2020-12-13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";

}
