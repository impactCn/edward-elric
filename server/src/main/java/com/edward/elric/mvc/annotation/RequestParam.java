package com.edward.elric.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author: impactCn
 * @createTime: 2020-12-13
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
