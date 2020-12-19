package com.edward.elric.core.annotion;

import java.lang.annotation.*;

/**
 * @author: impactCn
 * @createTime: 2020-12-13
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}
