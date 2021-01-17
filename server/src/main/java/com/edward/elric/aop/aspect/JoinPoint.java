package com.edward.elric.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author: impactCn
 * @createTime: 2021-01-17
 */
public interface JoinPoint {

    Method getMethod();

    Object[] getArgs();

    Object getThis();

    void setAttribute();

    void getAttribute();


}
